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
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelExtractor;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteParserHelper;
import org.schabi.newpipe.extractor.services.bitchute.linkHandler.BitchuteStreamLinkHandlerFactory;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;
import org.schabi.newpipe.extractor.utils.Utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants.BASE_RSS;

public class BitchuteChannelExtractor extends ChannelExtractor {
    private Document doc;
    private String channelName;
    private String avatarUrl;
    private Document xmlFeed;

    public BitchuteChannelExtractor(final StreamingService service,
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

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        try {
            if (channelName == null) {
                channelName = doc.select("#channel-title").first().text();
            }
            return channelName;
        } catch (final Exception e) {
            throw new ParsingException("Error parsing Channel Name");
        }
    }

    @Override
    public String getAvatarUrl() throws ParsingException {
        try {
            if (avatarUrl == null) {
                avatarUrl = doc.select("#page-bar > div > div > div.image-container > img")
                        .first().attr("data-src");
                if (avatarUrl.startsWith("/")) {
                    avatarUrl = BitchuteConstants.BASE_URL + avatarUrl;
                }
            }
            return avatarUrl;
        } catch (final Exception e) {
            throw new ParsingException("Error parsing Channel Avatar Url");
        }
    }

    @Override
    public String getDescription() throws ParsingException {
        try {
            return doc.select("#channel-description").first().text();
        } catch (final Exception e) {
            throw new ParsingException("Error parsing Channel Description");
        }
    }

    @Override
    public String getParentChannelName() throws ParsingException {
        return null;
    }

    @Override
    public String getParentChannelUrl() throws ParsingException {
        return null;
    }

    @Override
    public String getParentChannelAvatarUrl() throws ParsingException {
        return null;
    }

    @Override
    public boolean isVerified() throws ParsingException {
        return false;
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        try {
            return Utils.mixedNumberWordToLong(BitchuteParserHelper
                    .getSubscriberCountForChannelID(getChannelID()));
        } catch (final Exception e) {
            throw new ParsingException("Error parsing Channel Subscribers");
        }
    }

    /**
     * Bitchute provides more detailed channel video information via RSS than plain html.
     *
     * This only works for the latest 15 videos as the RSS feed only provides info about
     * newest videos. There is no known parameter to get more data via a offset value.
     *
     * See {@link #getPubDateViaRssFeed(String)}
     * @throws IOException
     * @throws ReCaptchaException
     */
    private void fetchRssFeed() throws IOException, ReCaptchaException {
        final String channelUrlName =
                this.doc.select("div.details > p.name > a").attr("href");
        final String channelRss = BASE_RSS + channelUrlName;
        final Response rssFeed = getDownloader().get(channelRss);

        xmlFeed = Jsoup.parse(rssFeed.responseBody(), "", Parser.xmlParser());
    }

    /**
     * Get a precise upload date via RSS feed.
     *
     * Bitchute provides very non-precise video upload dates on a channel (html) page.
     * They only display the date and no time information. So the ordering will be a mess.
     * Nevertheless they provide a RSS feed for the channel that has precise information
     * about the upload date/time. This method will use the RSS video upload date if the
     * video is listed there.
     *
     * @param videoId the Bitchute video id
     * @return
     * @throws IOException
     * @throws ReCaptchaException
     * @throws ParsingException if there is no RSS feed data for requested videoId
     * @throws ParseException
     */
    private Date getPubDateViaRssFeed(final String videoId)
            throws IOException, ReCaptchaException, ParsingException, ParseException {
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
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        fetchRssFeed();
        final JsonObject jsonObject = new JsonObject();
        jsonObject.put("offset", "0");
        jsonObject.put("name", getName());
        jsonObject.put("url", getUrl());
        return getInfoItemsPage(this.doc, jsonObject);
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page)
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

    @Override
    public String getBannerUrl() throws ParsingException {
        return getAvatarUrl();
    }

    @Override
    public String getFeedUrl() {
        return null;
    }

    private InfoItemsPage<StreamInfoItem> getInfoItemsPage(final Document document,
                                                           final JsonObject jsonObject) {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
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
                    } catch (final ParseException | ReCaptchaException
                                   | IOException | ParsingException ex) {
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
