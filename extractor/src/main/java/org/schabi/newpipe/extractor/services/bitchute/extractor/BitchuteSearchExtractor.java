package org.schabi.newpipe.extractor.services.bitchute.extractor;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;

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
import org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteParserHelper;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
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
        return getPage(new Page(getUrl(), "0")); // id is used as the page number
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(Page page) throws IOException, ExtractionException {

        /*
        //TODO evermind: do we need document for something?
        Document document = Jsoup.parse(getDownloader().get(page.getUrl(),
                BitchuteParserHelper.getBasicHeader()).responseBody());
         */


        // retrieve the results via json result
        String query = getLinkHandler().getId();
        /* TODO for now we restrict to only search for videos and not for channel as I don't know
           how to handle both. As they are separated query calls.
         */
        int currentPageNumber = Integer.parseInt(page.getId());
        JsonObject jsonResponse = BitchuteParserHelper.getSearchResultForQuery(query, BitchuteConstants.KIND_VIDEO, currentPageNumber);

        InfoItemsSearchCollector collector = new InfoItemsSearchCollector(getServiceId());
        InfoItemExtractor infoItemExtractor;

        String jsonResultArrayKey = "results";
        /* below keys for the array with jsonResultArrayKey as key */
        String jsonTitleKey = "name";
        String jsonVideoPathKey = "path";
        String jsonDescKey = "description";
        String jsonPublishedKey = "published";
        String jsonViewsKey = "views";
        String jsonKindKey = "kind";
        String jsonDurationKey = "duration";
        String jsonUploaderKey = "channel_name";
        String jsonUploaderUrlKey = "channel_path";

        JsonArray results = jsonResponse.getArray(jsonResultArrayKey);
        if (results.size() == 0) {
            return new InfoItemsPage<>(collector, null);
        }

        for ( Object elem : results) {
            if ( elem instanceof JsonObject) {
                JsonObject result = (JsonObject) elem;
                String name = result.getString(jsonTitleKey);
                String url = BitchuteConstants.BASE_URL + result.getString(jsonVideoPathKey);

                String thumbUrl = result.getObject("images").getString("thumbnail");
                String desc = result.getString(jsonDescKey);

                String textualDate = result.getString(jsonPublishedKey);
                String views = result.getString(jsonViewsKey);
                String duration = result.getString(jsonDurationKey);
                String kind = result.getString(jsonKindKey);
                String uploader = result.getString(jsonUploaderKey);
                String uploaderUrl = BitchuteConstants.BASE_URL + result.getString(jsonUploaderUrlKey);

                switch (kind) {
                    case BitchuteConstants.KIND_CHANNEL:
                        infoItemExtractor = new BitchuteQuickChannelInfoItemExtractor(
                                name,
                                url,
                                thumbUrl,
                                desc
                        );
                        break;
                    case BitchuteConstants.KIND_VIDEO:
                    default:
                        infoItemExtractor = new BitchuteQuickStreamInfoItemExtractor(
                                name,
                                url,
                                thumbUrl,
                                views,
                                textualDate,
                                duration,
                                uploader,
                                uploaderUrl
                        );
                }

                collector.commit(infoItemExtractor);
            }
        }

        try {

            String jsonResultsCount = "count";
            String jsonResultsTotal = "total";

            long count = jsonResponse.getLong(jsonResultsCount);
            long total = jsonResponse.getLong(jsonResultsTotal);

            int maxPages = (count != 0) ? (int) (total / count) : 0;
            if (maxPages > currentPageNumber) {
                currentPageNumber += 1;
                return new InfoItemsPage<>(collector, new Page(getUrl(), String.valueOf(currentPageNumber)));
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
        String duration;
        String uploader;
        String uploaderUrl;

        public BitchuteQuickStreamInfoItemExtractor(String name, String url, String thumbUrl,
                                                    String viewCount, String textualDate,
                                                    String duration, String uploader,
                                                    String uploaderUrl) {
            this.viewCount = viewCount;
            this.textualDate = textualDate;
            this.name = name;
            this.url = url;
            this.thumbUrl = thumbUrl;
            this.duration = duration;
            this.uploader = uploader;
            this.uploaderUrl = uploaderUrl;
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
