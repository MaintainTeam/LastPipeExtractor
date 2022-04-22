package org.schabi.newpipe.extractor.services.rumble.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.schabi.newpipe.extractor.MediaFormat;
import org.schabi.newpipe.extractor.MetaInfo;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandler;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.services.rumble.RumbleParsingHelper;
import org.schabi.newpipe.extractor.services.youtube.ItagItem;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.Description;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;
import org.schabi.newpipe.extractor.stream.StreamSegment;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.stream.SubtitlesStream;
import org.schabi.newpipe.extractor.stream.VideoStream;
import org.schabi.newpipe.extractor.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.schabi.newpipe.extractor.utils.Utils.isNullOrEmpty;

public class RumbleStreamExtractor extends StreamExtractor {

    private final String videoUploaderJsonKey = "author";
    private final String videoTitleJsonKey = "title";
    private final String videoCoverImageJsonKey = "i";
    private final String videoDateJsonKey = "pubDate";
    private final String videoDurationJsonKey = "duration";

    private final String videoViewerCountHtmlKey = "span.media-heading-info";
    private final String relatedStreamHtmlKey = "ul.mediaList-list";

    private Document doc;
    JsonObject embedJsonStreamInfoObj;
    private String realVideoId;

    private int ageLimit = -1;
    private List<VideoStream> videoStreams;

    public RumbleStreamExtractor(final StreamingService service, final LinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        assertPageFetched();
        final String title =
                Parser.unescapeEntities(embedJsonStreamInfoObj.getString(videoTitleJsonKey), true);

        return title;
    }

    @Nullable
    @Override
    public String getTextualUploadDate() throws ParsingException {

        final String textualDate = embedJsonStreamInfoObj.getString(videoDateJsonKey);
        return textualDate;
    }

    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        final String textualUploadDate = getTextualUploadDate();


        if (isNullOrEmpty(textualUploadDate)) {
            return null;
        }
        // the format is: 2021-02-08T19:37:25+00:00  youtube-dl pares it with iso8601b
        return new DateWrapper(YoutubeParsingHelper.parseDateFrom(textualUploadDate), false);
    }

    @Nonnull
    @Override
    public String getThumbnailUrl() throws ParsingException {
        assertPageFetched();
        final String thumbUrl = embedJsonStreamInfoObj.getString(videoCoverImageJsonKey);
        return thumbUrl;
    }

    @Nonnull
    @Override
    public Description getDescription() throws ParsingException {
        assertPageFetched();
        final List<Node> nodes = doc.select("p.media-description").first().childNodes();
        final String description = "";

        // the 3rd item has the description. Some videos do not have a description
        // therefore check if there are enough nodes.
        if (nodes.size() >= 3) {
            doc.select("p.media-description").first().childNodes().get(2).toString();
        }
        return new Description(Parser.unescapeEntities(description, false), Description.PLAIN_TEXT);
    }

    @Override
    public int getAgeLimit() throws ParsingException {
        if (ageLimit == -1) {
            ageLimit = NO_AGE_LIMIT;
        }

        return ageLimit;
    }

    @Override
    public long getLength() throws ParsingException {
        assertPageFetched();
        final Number duration = embedJsonStreamInfoObj.getNumber(videoDurationJsonKey);

        return duration.longValue();
    }

    /**
     * @return 0 means no timestamp is found.
     */
    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public long getViewCount() throws ParsingException {
        assertPageFetched();
        // <span class="media-heading-info">2,043 Views</span>
        final String views = doc.select(videoViewerCountHtmlKey).get(1).text();

        return Long.parseLong(Utils.removeNonDigitCharacters(views));
    }

    @Override
    public long getLikeCount() throws ParsingException {
        return -1;
    }

    @Override
    public long getDislikeCount() throws ParsingException {
        return -1;
    }

    @Nonnull
    @Override
    public String getUploaderUrl() throws ParsingException {
        assertPageFetched();
        return embedJsonStreamInfoObj.getObject(videoUploaderJsonKey).getString("url");
    }

    @Nonnull
    @Override
    public String getUploaderName() throws ParsingException {
        assertPageFetched();
        final String uploaderName =
                embedJsonStreamInfoObj.getObject(videoUploaderJsonKey).getString("name");
        return uploaderName;
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        // TODO can be done
        return false;
    }

    @Nonnull
    @Override
    public String getUploaderAvatarUrl() throws ParsingException {
        assertPageFetched();
        final Elements elems = doc.getElementsByClass("media-by--a");
        final String theUserPathToHisAvatar =
                elems.get(0).getElementsByTag("i").first().attributes().get("class");
        try {
            final String thumbnailUrl = RumbleParsingHelper
                    .totalMessMethodToGetUploaderThumbnailUrl(theUserPathToHisAvatar, doc);
            return thumbnailUrl;
        } catch (final Exception e) {
            throw new ParsingException(
                    "Could not extract the avatar url: " + theUserPathToHisAvatar);
        }
    }

    @Nonnull
    @Override
    public String getSubChannelUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String getSubChannelName() {
        return "";
    }

    @Nonnull
    @Override
    public String getSubChannelAvatarUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String getDashMpdUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String getHlsUrl() {
        return "";
    }

    @Override
    public List<AudioStream> getAudioStreams() throws ExtractionException {
        if (videoStreams == null) {
            videoStreams = extractVideoStreams();
        }

        final Map<String, Integer> mapResolutionToAudioBitrate = new HashMap() {{
            put("240p", 96);
            put("360p", 128);
            put("480p", 160);
            put("720p", 192);
            put("1080p", 200);

        }};
        final List<AudioStream> audioStreams = new ArrayList<>();

        for (final VideoStream stream : videoStreams) {
            Integer res = mapResolutionToAudioBitrate.get(stream.getResolution());
            if (null == res) {
                res = new Integer(-1);
            }
            final AudioStream audioStream = new AudioStream(stream.getUrl(),
                    stream.getFormat(), res);
            audioStreams.add(audioStream);
        }
        return audioStreams;
    }

    @Override
    public List<VideoStream> getVideoStreams() throws ExtractionException {
        if (videoStreams == null) {
            videoStreams = extractVideoStreams();
        }
        return videoStreams;
    }

    private List<VideoStream> extractVideoStreams() throws ExtractionException {
        assertPageFetched();

        final List<VideoStream> videoStreamsList = new ArrayList<>();
        final String videoAlternativesKey = "ua";
        final String videoMetaKey = "meta";
        final String videoUrlKey = "url";

        final Set<String> formatKeys =
                embedJsonStreamInfoObj.getObject(videoAlternativesKey).keySet();
        for (final String formatKey : formatKeys) { // mp4 or webm or whatever format
            // todo validate if we want to support this formats or not
            final JsonObject formatObj =
                    embedJsonStreamInfoObj.getObject(videoAlternativesKey).getObject(formatKey);
            final Set<String> resolutionKeys = formatObj.keySet();
            for (final String res : resolutionKeys) { // 240, 360 , 480 ...
                // todo validate if we support this resolution

                final JsonObject metadata =
                        formatObj.getObject(res).getObject(videoMetaKey); // size w h bitrate
                final String videoUrl =
                        formatObj.getObject(res).getString(videoUrlKey); // where the mp4 sits

                final MediaFormat format = MediaFormat.getFromSuffix(formatKey);
                final ItagItem itagItem =
                        new ItagItem(-1, ItagItem.ItagType.VIDEO, format, res + "p");
                itagItem.setWidth(metadata.getNumber("w").intValue());
                itagItem.setHeight(metadata.getNumber("h").intValue());
                itagItem.setBitrate(metadata.getNumber("bitrate").intValue());
                // even though fps is available but NewPipeExtractor
                // can only handle integer set so we set it to -1
                itagItem.fps = -1;

                videoStreamsList.add(new VideoStream(videoUrl, false, itagItem));
            }
        }

        return videoStreamsList;
    }

    @Override
    public StreamType getStreamType() {
        assertPageFetched();
        final String videoLiveStreamKey = "live";
        final Number isLive = embedJsonStreamInfoObj.getNumber(videoLiveStreamKey);
        final boolean isLiveStream = isLive.intValue() == 1; // is live stream or not
        return isLiveStream ? StreamType.LIVE_STREAM : StreamType.VIDEO_STREAM;
    }

    @Nullable
    @Override
    public StreamInfoItemsCollector getRelatedItems() throws ExtractionException {
        final List<Node> nodes = doc.select(relatedStreamHtmlKey).first().childNodes();
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        for (final Node node : nodes) {
            collector.commit(new RumbleStreamRelatedInfoItemExtractor(
                    getTimeAgoParser(), node, doc
            ));
        }

        return collector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorMessage() {
        return null;
    }

    @SuppressWarnings("checkstyle:LineLength")
    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {

        final Response response = downloader.get(getUrl());
        doc = Jsoup.parse(response.responseBody(), getUrl());

        final String jsonString =
                doc.getElementsByAttributeValueContaining("type", "application/ld+json").first()
                        .childNodes().get(0).toString();

        // extract the internal video id for a rumble video
        final JsonArray jsonObj;
        try {
            jsonObj = JsonParser.array().from(jsonString);
            final String embedUrl = jsonObj.getObject(0).getString("embedUrl");


            final URL url = Utils.stringToURL(embedUrl);
            final String[] splitPaths = url.getPath().split("/");

            if (splitPaths.length == 3 && splitPaths[1].equalsIgnoreCase("embed")) {
                realVideoId = splitPaths[2];
            }

        } catch (final JsonParserException e) {
            e.printStackTrace();
        }

        final String queryUrl = "https://rumble.com/embedJS/u3/?request=video&ver=2&v="
                + realVideoId;

        final Response response2 = downloader.get(
                queryUrl);

        // TODO keep some cookies to be more browser like
        //curl 'https://rumble.com/embedJS/u3/?request=video&ver=2&v=vb294t&ext=%7B%22ad_count%22%3Anull%7D&ad_wt=0'
        // -H 'Referer: https://rumble.com/vdofb7-1-year-old-pulls-pony-behind-electric-car.html'
        // -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)
        // Chrome/80.0.3987.149 Safari/537.36' -H 'Sec-Fetch-Dest: empty' --compressed
        //Document doc = Jsoup.parse(response.responseBody(), getUrl());
        try {
            embedJsonStreamInfoObj = JsonParser.object().from(response2.responseBody());
        } catch (final JsonParserException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    @Override
    public String getHost() {
        return "";
    }

    @Nonnull
    @Override
    public Privacy getPrivacy() {
        return Privacy.PUBLIC;
    }

    @Nonnull
    @Override
    public String getCategory() {
        return "";
    }

    @Nonnull
    @Override
    public String getLicence() {
        return "";
    }

    @Override
    public Locale getLanguageInfo() {
        return null;
    }

    @Nonnull
    @Override
    public List<String> getTags() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public String getSupportInfo() {
        return "";
    }

    @Nonnull
    @Override
    public List<StreamSegment> getStreamSegments() throws ParsingException {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<MetaInfo> getMetaInfo() throws ParsingException {
        return Collections.emptyList();
    }

    @Override
    public List<VideoStream> getVideoOnlyStreams() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<SubtitlesStream> getSubtitlesDefault() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<SubtitlesStream> getSubtitles(final MediaFormat format) {
        return Collections.emptyList();
    }
}
