package org.schabi.newpipe.extractor.services.bitchute.extractor;

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
import org.schabi.newpipe.extractor.services.bitchute.BitchuteParserHelper;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BitchuteSearchExtractor extends SearchExtractor {


    public BitchuteSearchExtractor(StreamingService service, SearchQueryHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {

    }


    @Override
    public String getSearchSuggestion() {
        return "";
    }

    @Override
    public boolean isCorrectedSearch() throws ParsingException {
        return false; // TODO evermind: this is just to get it compiled not verified
    }

    @Nonnull
    @Override
    public List<MetaInfo> getMetaInfo() throws ParsingException {
        return Collections.emptyList(); // TODO evermind verify what really should be done
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        return getPage(new Page(String.format(getUrl(), String.valueOf(1))));
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(Page page) throws IOException, ExtractionException {

        Document document = Jsoup.parse(getDownloader().get(page.getUrl(),
                BitchuteParserHelper.getBasicHeader()).responseBody());

        InfoItemsSearchCollector collector = new InfoItemsSearchCollector(getServiceId());
        InfoItemExtractor infoItemExtractor;
        Elements elements = document.select(".oss-result > *");
        if (elements.size() == 0) {
            return new InfoItemsPage<>(collector, null);
        }
        Elements toParse = new Elements();
        for (Element element : elements) {
            if (element.tagName().equals("br")) {
                StringBuilder sb = new StringBuilder();
                for (Element e : toParse) {
                    sb.append(e.outerHtml()).append(" ");
                }
                Element e = Jsoup.parse(sb.toString());
                String name = e.select("div.ossfieldrdr1 a").first().text();

                String url = e.select("div.ossfieldrdr1 a").first().attr("href");

                String thumbUrl = e.select("div.ossfieldrdr2 a img")
                        .first().attr("src");

                Element descElement = e.select("div.ossfieldrdr3").first();
                String desc = descElement != null ? descElement.text() : "";

                String textualDate = e.select("div.oss-item-date").first().text();

                Element viewsElement = e.select("div.oss-item-views").first();
                String views = viewsElement != null ? viewsElement.
                        text().replace(">", "") : "0";

                String type = e.select("div.oss-item-kind").first().text();

                switch (type) {
                    case "channel":
                        infoItemExtractor = new BitchuteQuickChannelInfoItemExtractor(
                                name,
                                url,
                                thumbUrl,
                                desc
                        );
                        break;
                    case "video":
                    default:
                        infoItemExtractor = new BitchuteQuickStreamInfoItemExtractor(
                                name,
                                url,
                                thumbUrl,
                                views,
                                textualDate
                        );
                }
                collector.commit(infoItemExtractor);
                toParse.clear();
            } else {
                toParse.add(element);
            }
        }
        try {
            int max = Integer.parseInt(document
                    .select(".oss-paging > a:last-of-type").first().text());
            int current = Integer.parseInt(document
                    .select(".oss-paging > strong > a").first().text());
            if (max > current) {
                current += 1;
                return new InfoItemsPage<>(collector, new Page(String.format(getUrl(), String.valueOf(current))));
            } else {
                return new InfoItemsPage<>(collector, null);
            }
        } catch (Exception e) {
            return new InfoItemsPage<>(collector, null);
        }
    }

    private static class BitchuteQuickStreamInfoItemExtractor implements StreamInfoItemExtractor {
        String viewCount;
        String textualDate;
        String name;
        String url;
        String thumbUrl;

        public BitchuteQuickStreamInfoItemExtractor(String name, String url, String thumbUrl,
                                                    String viewCount, String textualDate) {
            this.viewCount = viewCount;
            this.textualDate = textualDate;
            this.name = name;
            this.url = url;
            this.thumbUrl = thumbUrl;
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
        public long getDuration() {
            return -1;
        }

        @Override
        public long getViewCount() throws ParsingException {
            return Utils.mixedNumberWordToLong(viewCount);
        }

        @Override
        public String getUploaderName() {
            return null;
        }

        @Override
        public String getUploaderUrl() {
            return null;
        }

        @Override
        public boolean isUploaderVerified() throws ParsingException {
            return false; // TODO evermind: this is just to get it compiled not verified
        }

        @Nullable
        @Override
        public String getTextualUploadDate() {
            return textualDate;
        }

        @Nullable
        @Override
        public DateWrapper getUploadDate() throws ParsingException {
            try {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(sf.parse(getTextualUploadDate().split("T")[0]));
                return new DateWrapper(calendar);
            } catch (Exception e) {
                throw new ParsingException("Error Parsing Upload Date");
            }
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

    private static class BitchuteQuickChannelInfoItemExtractor implements ChannelInfoItemExtractor {

        String description;
        String name;
        String url;
        String thumbUrl;

        public BitchuteQuickChannelInfoItemExtractor(String name, String url,
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
            return false; // TODO evermind: this is just to get it compiled not verified
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