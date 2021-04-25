package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.schabi.newpipe.extractor.MediaFormat;
import org.schabi.newpipe.extractor.MetaInfo;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandler;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.stream.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

import static org.schabi.newpipe.extractor.utils.Utils.isNullOrEmpty;

public class RumbleStreamExtractor extends StreamExtractor {

    public RumbleStreamExtractor(final StreamingService service, final LinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        assertPageFetched();
        String title = ""; // TODO

        return title;
    }

    @Nullable
    @Override
    public String getTextualUploadDate() throws ParsingException {

        String textualDate = ""; // TODO
        return textualDate;
    }

    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        final String textualUploadDate = ""; // TODO


        if (isNullOrEmpty(textualUploadDate)) {
            return null;
        }
        return null; // TODO
    }

    @Nonnull
    @Override
    public String getThumbnailUrl() throws ParsingException {
        assertPageFetched();
        String thumbUrl = ""; // TODO
        return thumbUrl;
    }

    @Nonnull
    @Override
    public Description getDescription() throws ParsingException {
        assertPageFetched();
        String description = ""; // TODO
        return new Description(description, Description.PLAIN_TEXT);
    }

    @Override
    public int getAgeLimit() throws ParsingException {
        return 0; // TODO
    }

    @Override
    public long getLength() throws ParsingException {
        assertPageFetched();
        return -1; // TODO
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
        return -1; // TODO
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
        return ""; // TODO
    }

    @Nonnull
    @Override
    public String getUploaderName() throws ParsingException {
        assertPageFetched();
        String uploaderName = ""; // TODO
        return uploaderName;
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        // TODO
        return false;
    }

    @Nonnull
    @Override
    public String getUploaderAvatarUrl() throws ParsingException {
        assertPageFetched();
        // TODO
        return "";
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
        return null; // TODO
    }

    @Override
    public List<VideoStream> getVideoStreams() throws ExtractionException {
        return null; // TODO
    }

    @Override
    public StreamType getStreamType() {
        assertPageFetched();
        return null; // TODO
    }

    @Nullable
    @Override
    public StreamInfoItemsCollector getRelatedItems() throws ExtractionException {
        return null; // TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        // TODO
    }

    @Nonnull
    @Override
    public String getHost() {
        return "";
    }

    @Nonnull
    @Override
    public Privacy getPrivacy() {
        return null; // TODO
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
    public List<SubtitlesStream> getSubtitles(MediaFormat format) {
        return Collections.emptyList();
    }
}
