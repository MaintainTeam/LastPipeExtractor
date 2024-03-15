package org.schabi.newpipe.extractor.services.bitchute.extractor;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.grack.nanojson.JsonWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.MultiInfoItemsCollector;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabExtractor;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteParserHelper;
import org.schabi.newpipe.extractor.services.bitchute.linkHandler.BitchuteStreamLinkHandlerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants.BASE_RSS;
import static org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants.BASE_URL;

public class BitchuteChannelTabExtractor extends ChannelTabExtractor {
    private Document doc;
    private Document xmlFeed;

    public BitchuteChannelTabExtractor(final StreamingService service,
                                       final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final Response response = getDownloader().get(getUrl(),
                BitchuteParserHelper.getBasicHeader());
        doc = Jsoup.parse(response.responseBody(), getUrl());
    }

    private String getChannelID() {
        final String canonicalUrl = doc.getElementById("canonical").attr("href");
        final String[] urlSegments = canonicalUrl.split("/");
        return urlSegments[urlSegments.length - 1];
    }

    /**
     * Bitchute provides more detailed channel video information via RSS than plain html.
     * <p>
     * This only works for the latest 15 videos as the RSS feed only provides info about
     * newest videos. There is no known parameter to get more data via a offset value.
     * <p>
     * See {@link #getPubDateViaRssFeed(String)}
     *
     * @throws IOException
     * @throws ExtractionException
     */
    private void fetchRssFeed() throws IOException, ExtractionException {
        final String channelRss;
        try {
            channelRss = getUrl().replace(BASE_URL, BASE_RSS);
        } catch (final ParsingException e) {
            throw new ExtractionException("Could not create channel RSS Url: " + e.getMessage());
        }
        final Response rssFeed = getDownloader().get(channelRss);

        xmlFeed = Jsoup.parse(rssFeed.responseBody(), "", Parser.xmlParser());
    }

    /**
     * Get a precise upload date via RSS feed.
     * <p>
     * Bitchute provides very non-precise video upload dates on a channel (html) page.
     * They only display the date and no time information. So the ordering will be a mess.
     * Nevertheless they provide a RSS feed for the channel that has precise information
     * about the upload date/time. This method will use the RSS video upload date if the
     * video is listed there.
     *
     * @param videoId the Bitchute video id
     * @return
     * @throws ParsingException if there is no RSS feed data for requested videoId
     * @throws ParseException
     */
    private Date getPubDateViaRssFeed(final String videoId)
            throws ParsingException, ParseException {
        if (null == xmlFeed) {
            throw new ParsingException("xml feed is not present");
        }

        final Elements rssFeedItems = xmlFeed.select("item");

        for (final Element rssFeedItem : rssFeedItems) {
            final Element linkData = rssFeedItem.select("link").first();
            if (null == linkData) {
                throw new ParsingException("RSS data is incomplete <link> missing");
            }

            final String itemVideoUrl = linkData.text();
            final String itemVideoId = BitchuteStreamLinkHandlerFactory.getInstance()
                    .getId(itemVideoUrl);

            if (videoId.equalsIgnoreCase(itemVideoId)) {
                final String pubDate = rssFeedItem.select("pubDate").text();

                // input data: Wed, 30 Aug 2023 12:37:49 +0000
                final SimpleDateFormat df = new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss Z",
                        BitchuteConstants.BITCHUTE_LOCALE);
                return df.parse(pubDate);
            }
        }
        throw new ParsingException("For this video there is no RSS Feed data: " + videoId);
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        fetchRssFeed();
        final JsonObject jsonObject = new JsonObject();
        jsonObject.put("offset", "0");
        jsonObject.put("name", BitchuteParserHelper.getChannelName(this.doc));
        jsonObject.put("url", getUrl());
        return getInfoItemsPage(this.doc, jsonObject);
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        try {
            final JsonObject jsonObject = JsonParser.object().from(page.getUrl());
            return getInfoItemsPage(BitchuteParserHelper
                            .getExtendDocumentForUrl(getUrl(), jsonObject.getString("offset")),
                    jsonObject);
        } catch (final JsonParserException e) {
            throw new ParsingException("Error parsing url json");
        }
    }

    private InfoItemsPage<InfoItem> getInfoItemsPage(final Document document,
                                                     final JsonObject jsonObject) {
        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());
        final Elements videos = document.select(".channel-videos-container");
        for (final Element e : videos) {
            collector.commit(new BitchuteChannelStreamInfoItemExtractor(e) {

                @Override
                public String getUploaderName() throws ParsingException {
                    return jsonObject.getString("name");
                }

                @Override
                public String getUploaderUrl() throws ParsingException {
                    return jsonObject.getString("url");
                }

                @Override
                public boolean isUploaderVerified() throws ParsingException {
                    return false;
                }

                @Nullable
                @Override
                public DateWrapper getUploadDate() throws ParsingException {
                    try {
                        // try to get upload date via channel RSS feed
                        final String url = super.getUrl();
                        final String id =
                                BitchuteStreamLinkHandlerFactory.getInstance().getId(url);
                        final Date date = getPubDateViaRssFeed(id);
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        return new DateWrapper(calendar);
                    } catch (final ParseException | ParsingException ex) {
                        // use the html way if there is no RSS feed information available
                        return super.getUploadDate();
                    }
                }
            });
        }
        int offset = Integer.parseInt(jsonObject.getString("offset"));
        if (videos.size() < 25) {
            return new InfoItemsPage<>(collector, null);
        }
        offset += 25;
        jsonObject.put("offset", String.valueOf(offset));
        return new InfoItemsPage<>(collector, new Page(JsonWriter.string(jsonObject)));
    }
}
