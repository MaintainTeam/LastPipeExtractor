package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.channel.ChannelInfoItemExtractor;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.services.rumble.linkHandler.RumbleChannelLinkHandlerFactory;
import org.schabi.newpipe.extractor.utils.Utils;

public class RumbleChannelInfoItemExtractor implements ChannelInfoItemExtractor {

    public RumbleChannelInfoItemExtractor(final String something) {
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        try {
            final String url = ""; // TODO
            return url;
        } catch (final Exception e) {
            throw new ParsingException("Could not get thumbnail url", e);
        }
    }

    @Override
    public String getName() throws ParsingException {
        try {
            return ""; // TODO
        } catch (final Exception e) {
            throw new ParsingException("Could not get name", e);
        }
    }

    @Override
    public String getUrl() throws ParsingException {
        try {
            final String id = ""; // TODO
            return RumbleChannelLinkHandlerFactory.getInstance().getUrl(id);
        } catch (final Exception e) {
            throw new ParsingException("Could not get url", e);
        }
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        try {
            final String subscriptionCount = ""; // TODO
            if (false /* hasSubscriberCountText*/) {
                // Subscription count is not available for this channel item.
                return -1;
            }

            return Utils.mixedNumberWordToLong(subscriptionCount);
        } catch (final Exception e) {
            throw new ParsingException("Could not get subscriber count", e);
        }
    }

    @Override
    public long getStreamCount() throws ParsingException {
        try {
            final String streamCount = ""; // TODO
            if (false /*hasVideoCountText*/) {
                // Video count is not available, channel probably has no public uploads.
                return ListExtractor.ITEM_COUNT_UNKNOWN;
            }

            return Long.parseLong(Utils.removeNonDigitCharacters(streamCount));
        } catch (final Exception e) {
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
            if (false /* hasDescriptionSnippet*/) {
                // Channel has no description.
                return null;
            }

            return ""; // TODO
        } catch (final Exception e) {
            throw new ParsingException("Could not get description", e);
        }
    }
}
