package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.schabi.newpipe.extractor.channel.ChannelInfoItemExtractor;
import org.schabi.newpipe.extractor.exceptions.ParsingException;

class RumbleChannelSearchInfoItemExtractor implements ChannelInfoItemExtractor {

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
