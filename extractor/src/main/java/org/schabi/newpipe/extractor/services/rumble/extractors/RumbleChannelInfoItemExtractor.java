package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.channel.ChannelInfoItemExtractor;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.services.rumble.linkHandler.RumbleChannelLinkHandlerFactory;
import org.schabi.newpipe.extractor.utils.Utils;

public class RumbleChannelInfoItemExtractor implements ChannelInfoItemExtractor {

    public RumbleChannelInfoItemExtractor(String something) {
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        try {
            String url = "" ; // TODO

            return url;
        } catch (Exception e) {
            throw new ParsingException("Could not get thumbnail url", e);
        }
    }

    @Override
    public String getName() throws ParsingException {
        try {
            return "" ; // TODO
        } catch (Exception e) {
            throw new ParsingException("Could not get name", e);
        }
    }

    @Override
    public String getUrl() throws ParsingException {
        try {
            String id = "" ; // TODO
            return RumbleChannelLinkHandlerFactory.getInstance().getUrl(id);
        } catch (Exception e) {
            throw new ParsingException("Could not get url", e);
        }
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        try {
            String subscriptionCount = ""; // TODO
            if (! true /* hasSubscriberCountText*/) {
                // Subscription count is not available for this channel item.
                return -1;
            }

            return Utils.mixedNumberWordToLong(subscriptionCount);
        } catch (Exception e) {
            throw new ParsingException("Could not get subscriber count", e);
        }
    }

    @Override
    public long getStreamCount() throws ParsingException {
        try {
            String streamCount = ""; // TODO
            if (! true /*hasVideoCountText*/) {
                // Video count is not available, channel probably has no public uploads.
                return ListExtractor.ITEM_COUNT_UNKNOWN;
            }

            return Long.parseLong(Utils.removeNonDigitCharacters(streamCount));
        } catch (Exception e) {
            throw new ParsingException("Could not get stream count", e);
        }
    }

    @Override
    public boolean isVerified() throws ParsingException {
        return false; // TODO check if possible
    }

    @Override
    public String getDescription() throws ParsingException {
        try {
            if (! true /* hasDescriptionSnippet*/) {
                // Channel has no description.
                return null;
            }

            return ""; // TODO
        } catch (Exception e) {
            throw new ParsingException("Could not get description", e);
        }
    }
}
