package org.schabi.newpipe.extractor.services.bitchute.extractor;

import org.jsoup.nodes.Element;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants;
import org.schabi.newpipe.extractor.services.bitchute.misc.BitchuteHelpers;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public abstract class BitchuteChannelStreamInfoItemExtractor implements StreamInfoItemExtractor {

    private final Element element;

    public BitchuteChannelStreamInfoItemExtractor(final Element element) {
        this.element = element;

        BitchuteHelpers.VideoDurationCache.extractVideoIdAndAddItToDurationCache(element,
                this,
                ".channel-videos-image-container a");
    }

    @Override
    public StreamType getStreamType() throws ParsingException {
        return StreamType.VIDEO_STREAM;
    }

    @Override
    public boolean isAd() throws ParsingException {
        return false;
    }

    @Override
    public long getDuration() throws ParsingException {
        return YoutubeParsingHelper.parseDurationString(element.
                select("span.video-duration").first().text());
    }

    @Override
    public long getViewCount() throws ParsingException {
        return Utils.mixedNumberWordToLong(element.select("span.video-views").first().text());
    }

    @Nullable
    @Override
    public String getTextualUploadDate() throws ParsingException {
        return element.select("div.channel-videos-details").first().text();
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {

        final Date date;
        try {
            final SimpleDateFormat df =
                    new SimpleDateFormat("MMM dd, yyyy", BitchuteConstants.BITCHUTE_LOCALE);
            date = df.parse(getTextualUploadDate());
        } catch (final ParseException e) {
            throw new ParsingException("Couldn't parse date:" + getTextualUploadDate());
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return new DateWrapper(calendar);
    }

    @Override
    public String getName() throws ParsingException {
        return element.select("div.channel-videos-title").first().text();
    }

    @Override
    public String getUrl() throws ParsingException {
        return element.select("div.channel-videos-title > a").first()
                .absUrl("href");
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return element.select("div.channel-videos-image > img").first()
                .absUrl("data-src");
    }

    /**
     * create a json object. Basically this is useful for compact unit testing or exporting the data
     */
    @Override
    public String toString() {
        try {
            return "{"
                    + "\"streamType\": \"" + getStreamType().toString()
                    + "\", \"isAd\": \"" + isAd()
                    + "\", \"duration\": \"" + getDuration()
                    + "\", \"viewCount\": \"" + getViewCount()
                    + "\", \"uploadDate\": \"" + getUploadDate().offsetDateTime().toString()
                    + "\", \"uploaderName\": \"" + getUploaderName()
                    + "\", \"uploaderUrl\": \"" + getUploaderUrl()
                    + "\", \"name\": \"" + getName()
                    + "\", \"url\": \"" + getUrl()
                    + "\", \"thumbnailUrl\": \"" + getThumbnailUrl()
                    + "\", \"isUploaderVerified\": \"" + isUploaderVerified()
                    + "\"}";
        } catch (final ParsingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    @Override
    public String getUploaderAvatarUrl() throws ParsingException {
        return null;
    }
}
