package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.utils.Utils;

import javax.annotation.Nullable;

public class RumbleSearchVideoStreamInfoItemExtractor implements StreamInfoItemExtractor {
    String viewCount;
    String textualDate;
    String name;
    String url;
    String thumbUrl;
    String duration;
    String uploader;
    String uploaderUrl;
    DateWrapper uploadDate;
    boolean isLive;

    public RumbleSearchVideoStreamInfoItemExtractor(String name, String url, String thumbUrl,
                                                    String viewCount, String textualDate,
                                                    String duration, String uploader,
                                                    String uploaderUrl, DateWrapper uploadDate, boolean isLive) {
        this.viewCount = viewCount;
        this.textualDate = textualDate;
        this.name = name;
        this.url = url;
        this.thumbUrl = thumbUrl;
        this.duration = duration;
        this.uploader = uploader;
        this.uploaderUrl = uploaderUrl;
        this.uploadDate = uploadDate;
        this.isLive = isLive;
    }

    @Override
    public StreamType getStreamType() {
        if (isLive)
            return StreamType.LIVE_STREAM;
        return StreamType.VIDEO_STREAM;
    }

    @Override
    public boolean isAd() {
        return false;
    }

    @Override
    public long getDuration() throws ParsingException {
        if (null == duration)
            return -1;
        return YoutubeParsingHelper.parseDurationString(duration);
    }

    @Override
    public long getViewCount() throws ParsingException {
        if (null == viewCount)
            return -1;
        return Long.parseLong(Utils.removeNonDigitCharacters(viewCount));
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
