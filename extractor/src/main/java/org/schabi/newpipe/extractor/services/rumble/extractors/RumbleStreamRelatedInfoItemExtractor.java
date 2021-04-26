package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.localization.TimeAgoParser;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamType;

import javax.annotation.Nullable;

public class RumbleStreamRelatedInfoItemExtractor implements StreamInfoItemExtractor {

    private final Element element;
    private final TimeAgoParser parser;
    private String channelName;
    private String channelUrl;

    public RumbleStreamRelatedInfoItemExtractor(TimeAgoParser parser, Element element) {
        this.element = element;
        this.parser = parser;
    }
    public RumbleStreamRelatedInfoItemExtractor(TimeAgoParser parser, Node element) {
        this.element = (Element)element;
        this.parser = parser;
    }

    public RumbleStreamRelatedInfoItemExtractor(TimeAgoParser parser, Element element, String channelName, String channelUrl) {
        this.element = element;
        this.parser = parser;
        this.channelName = channelName;
        this.channelUrl = channelUrl;
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
        try {

            String data = ""; // TODO
            long duration = -1; // TODO
            return duration;
        } catch (Exception e) {
            throw new ParsingException("Error parsing duration");
        }
    }

    @Override
    public long getViewCount() throws ParsingException {
        return -1; // TODO
    }

    @Override
    public String getUploaderName() {
        String res = ""; // TODO

        return res;
    }

    @Override
    public String getUploaderUrl() {
        return channelUrl;
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return false;
    }

    @Nullable
    @Override
    public String getTextualUploadDate() throws ParsingException {
        return null;
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        return null;
    }

    @Override
    public String getName() throws ParsingException {

        try {
            String title = ""; // TODO
            return title;
        } catch (Exception e) {
            throw new ParsingException("Error parsing Stream title");
        }
    }

    @Override
    public String getUrl() throws ParsingException {
        try {
            String url = ""; // TODO
            return url;
        } catch (Exception e) {
            throw new ParsingException("Error parsing Stream url");
        }
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        try {
            String thumbUrl = ""; // TODO
            return thumbUrl;
        } catch (Exception e) {
            throw new ParsingException("Error parsing thumbnail url");
        }
    }
}