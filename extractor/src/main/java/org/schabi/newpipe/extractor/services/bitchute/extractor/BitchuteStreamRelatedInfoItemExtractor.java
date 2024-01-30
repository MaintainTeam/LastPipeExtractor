package org.schabi.newpipe.extractor.services.bitchute.extractor;

import org.jsoup.nodes.Element;
import org.schabi.newpipe.extractor.Image;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.localization.TimeAgoParser;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.utils.Utils;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BitchuteStreamRelatedInfoItemExtractor implements StreamInfoItemExtractor {

    private final Element element;
    private final TimeAgoParser parser;
    private String channelName;
    private String channelUrl;

    public BitchuteStreamRelatedInfoItemExtractor(final TimeAgoParser parser,
                                                  final Element element) {
        this.element = element;
        this.parser = parser;
    }

    public BitchuteStreamRelatedInfoItemExtractor(final TimeAgoParser parser, final Element element,
                                                  final String channelName,
                                                  final String channelUrl) {
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
            return YoutubeParsingHelper
                    .parseDurationString(element.select(".video-duration")
                            .first().text());
        } catch (final Exception e) {
            throw new ParsingException("Error parsing duration");
        }
    }

    @Override
    public long getViewCount() throws ParsingException {
        try {
            return Utils.mixedNumberWordToLong(element
                    .select(".video-views").first().text());
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ParsingException("Error parsing view count");
        }
    }

    @Override
    public String getUploaderName() {
        return channelName;
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
        try {
            return element.select(".video-card-published").first().text();
        } catch (final Exception e) {
            throw new ParsingException("Error parsing Textual Upload Date");
        }
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        return parser.parse(getTextualUploadDate());
    }

    @Override
    public String getName() throws ParsingException {
        try {
            return element.select(".video-card-title").first().text();
        } catch (final Exception e) {
            throw new ParsingException("Error parsing Stream title");
        }
    }

    @Override
    public String getUrl() throws ParsingException {
        try {
            return element.select(".video-card-title a").first().absUrl("href");
        } catch (final Exception e) {
            throw new ParsingException("Error parsing Stream url");
        }
    }

    @Nonnull
    @Override
    public List<Image> getThumbnails() throws ParsingException {
        try {
            final String thumbnailUrl = element.select(
                    ".video-card-image img").first().attr("data-src");
            return List.of(new Image(thumbnailUrl,
                    Image.HEIGHT_UNKNOWN, Image.WIDTH_UNKNOWN, Image.ResolutionLevel.UNKNOWN));
        } catch (final Exception e) {
            throw new ParsingException("Error parsing thumbnail url");
        }
    }
}
