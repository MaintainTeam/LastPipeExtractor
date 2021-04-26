package org.schabi.newpipe.extractor.services.rumble.extractors;

import com.grack.nanojson.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.InfoItemExtractor;
import org.schabi.newpipe.extractor.MetaInfo;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelInfoItemExtractor;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandler;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.search.InfoItemsSearchCollector;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.schabi.newpipe.extractor.ServiceList.Rumble;

public class RumbleSearchExtractor extends SearchExtractor {
    private Document doc;

    public RumbleSearchExtractor(final StreamingService service, final SearchQueryHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws IOException, ExtractionException {
        doc = Jsoup.parse(getDownloader().get(getUrl()).responseBody());
    }

    @Nonnull
    @Override
    public String getSearchSuggestion() {
        return "";
    }

    @Override
    public boolean isCorrectedSearch() {
        return false;
    }

    @Override
    public List<MetaInfo> getMetaInfo() throws ParsingException {
        return Collections.emptyList();
    }

    private String getClassValue(Element element, String className, String attr) {
        return element.getElementsByClass(className).first().attr(attr);
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        return extractAndGetInfoItemsFromPage();
    }

    private InfoItemsPage<InfoItem> extractAndGetInfoItemsFromPage() throws IOException, ExtractionException {
        final InfoItemsSearchCollector collector = new InfoItemsSearchCollector(getServiceId());

        Elements elements = doc.select("li.video-listing-entry");
        InfoItemExtractor infoItemExtractor;

        for (Element element : elements) {
            String duration = getClassValue(element, "video-item--duration", "data-value");
            String views = getClassValue(element, "video-item--views", "data-value");
            String textualDate = getClassValue(element, "video-item--time", "datetime");
            String title = element.select("h3.video-item--title").first().childNodes().get(0).toString();
            String thumbUrl = element.select("img.video-item--img").first().absUrl("src");
            String url = Rumble.getBaseUrl() + element.select("a.video-item--a").first().attr("href");
            String uploaderUrl = Rumble.getBaseUrl() + element.select("address.video-item--by > a").first().attr("href");
            String uploader = element.select("address.video-item--by").first().getElementsByTag("div").first().text();
            DateWrapper uploadDate = new DateWrapper(OffsetDateTime.parse(textualDate), false);

            //switch (kind) {
            //    case BitchuteConstants.KIND_CHANNEL:
            //        infoItemExtractor = new BitchuteSearchExtractor.BitchuteQuickChannelInfoItemExtractor(
            //                name,
            //                url,
            //                thumbUrl,
            //                desc
            //        );
            //        break;
            //    case BitchuteConstants.KIND_VIDEO:
            //    default:
                    infoItemExtractor = new RumbleSearchVideoStreamInfoItemExtractor(
                            title,
                            url,
                            thumbUrl,
                            views,
                            textualDate,
                            duration,
                            uploader,
                            uploaderUrl,
                            uploadDate
                    );
            //}

            collector.commit(infoItemExtractor);
        }


        Page nextPage = null;

        // check if there is a next page
        if (elements.size() > 0) { // if .size() is 0 than we have no results at all -> assume no more pages
            String currentPageStrNumber = doc.getElementsByClass("paginator--link--current").attr("aria-label");
            int currentPageNumber = Integer.parseInt(currentPageStrNumber);

            // check if we are on the last page of available search results
            int currentPageIsLastPageIfGreaterThanZero = doc.getElementsByClass("paginator--link").last().getElementsByClass("paginator--link paginator--link--current").size();
            boolean hasMorePages = (currentPageIsLastPageIfGreaterThanZero > 0 ) ? false : true;

            if (hasMorePages) {
                nextPage = new Page(getUrl()+ "&page=" + ++currentPageNumber);
            }
        }

        return new InfoItemsPage<>(collector, nextPage);
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(final Page page) throws IOException, ExtractionException {
        if (null == page)
            return null;

        doc = Jsoup.parse(getDownloader().get(page.getUrl()).responseBody());
        return extractAndGetInfoItemsFromPage();
    }

    public static class RumbleSearchVideoStreamInfoItemExtractor implements StreamInfoItemExtractor {
        String viewCount;
        String textualDate;
        String name;
        String url;
        String thumbUrl;
        String duration;
        String uploader;
        String uploaderUrl;
        DateWrapper uploadDate;

        public RumbleSearchVideoStreamInfoItemExtractor(String name, String url, String thumbUrl,
                                                        String viewCount, String textualDate,
                                                        String duration, String uploader,
                                                        String uploaderUrl, DateWrapper uploadDate) {
            this.viewCount = viewCount;
            this.textualDate = textualDate;
            this.name = name;
            this.url = url;
            this.thumbUrl = thumbUrl;
            this.duration = duration;
            this.uploader = uploader;
            this.uploaderUrl = uploaderUrl;
            this.uploadDate = uploadDate;
        }

        @Override
        public StreamType getStreamType() {
            return StreamType.VIDEO_STREAM;
        }

        @Override
        public boolean isAd() {
            return false;
        }

        @Override
        public long getDuration() throws ParsingException {
            return YoutubeParsingHelper.parseDurationString(duration);
        }

        @Override
        public long getViewCount() throws ParsingException {
            return Utils.mixedNumberWordToLong(viewCount);
        }

        @Override
        public String getUploaderName() {
            return this.uploader;
        }

        @Override
        public String getUploaderUrl() {
            return this.uploaderUrl;
        }

        @Override
        public boolean isUploaderVerified() throws ParsingException {
            return false;
        }

        @Nullable
        @Override
        public String getTextualUploadDate() {
            return textualDate;
        }

        @Nullable
        @Override
        public DateWrapper getUploadDate() throws ParsingException {
            return uploadDate;
        }

        @Override
        public String getName() throws ParsingException {
            return name;
        }

        @Override
        public String getUrl() throws ParsingException {
            return url;
        }

        @Override
        public String getThumbnailUrl() throws ParsingException {
            return thumbUrl;
        }
    }

    private static class RumbleChannelSearchInfoItemExtractor implements ChannelInfoItemExtractor {

        String description;
        String name;
        String url;
        String thumbUrl;

        public RumbleChannelSearchInfoItemExtractor(String name, String url,
                                                    String thumbUrl, String description) {
            this.description = description;
            this.name = name;
            this.url = url;
            this.thumbUrl = thumbUrl;
        }

        @Override
        public String getDescription() throws ParsingException {
            return description;
        }

        @Override
        public long getSubscriberCount() throws ParsingException {
            return -1;
        }

        @Override
        public long getStreamCount() throws ParsingException {
            return -1;
        }

        @Override
        public boolean isVerified() throws ParsingException {
            return false;
        }

        @Override
        public String getName() throws ParsingException {
            return name;
        }

        @Override
        public String getUrl() throws ParsingException {
            return url;
        }

        @Override
        public String getThumbnailUrl() throws ParsingException {
            return thumbUrl;
        }
    }
}
