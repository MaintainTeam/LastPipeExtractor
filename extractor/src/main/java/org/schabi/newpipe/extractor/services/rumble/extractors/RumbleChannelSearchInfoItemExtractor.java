package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.schabi.newpipe.extractor.channel.ChannelInfoItemExtractor;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.services.rumble.RumbleParsingHelper;
import org.schabi.newpipe.extractor.utils.Utils;

import static org.schabi.newpipe.extractor.ListExtractor.ITEM_COUNT_UNKNOWN;
import static org.schabi.newpipe.extractor.ServiceList.Rumble;

class RumbleChannelSearchInfoItemExtractor implements ChannelInfoItemExtractor {

    private long subscriberCount;
    private String description = "";
    private String name;
    private String url;
    private String thumbUrl;
    private boolean verified;

    RumbleChannelSearchInfoItemExtractor(final Element element, final Document doc)
            throws ParsingException {
        extractData(element, doc);
    }

    private void extractData(final Element element, final Document doc) throws ParsingException {
        this.name = RumbleParsingHelper.extractSafely(true,
                "Could not extract the channel name",
                () -> element.select("h3.channel-item--title").text());

        this.subscriberCount = extractSubscriberCount(element, doc);

        this.url = RumbleParsingHelper.extractSafely(true,
                "Could not extract the stream url",
                () -> Rumble.getBaseUrl() + element
                        .select("a.channel-item--a").first().attr("href"));

        this.thumbUrl = extractTheThumbnailOfAChannelInASearchForChannels(element, doc);

        this.verified = !element.select("svg.channel-item--by-verified").isEmpty();
    }

    private long extractSubscriberCount(final Element element, final Document document)
            throws ParsingException {

        final String errorMsg = "Could not get subscriber count";
        final String amountOfSubscribers = RumbleParsingHelper.extractSafely(true,
                errorMsg,
                () -> element.select("span.channel-item--subscribers").first().text());

        if (null != amountOfSubscribers) {
            try {
                return Utils.mixedNumberWordToLong(amountOfSubscribers.replace(",", ""));
            } catch (final NumberFormatException e) {
                throw new ParsingException(errorMsg, e);
            }
        } else {
            return ITEM_COUNT_UNKNOWN;
        }
    }

    // extract the thumbnail of a channel in a channel search
    private String extractTheThumbnailOfAChannelInASearchForChannels(final Element element,
                                                                     final Document document)
            throws ParsingException {
        return RumbleParsingHelper.extractThumbnail(document, element.toString(),
                () -> {
                    final String thumbUrlIdentifier = "i." + element
                            .select("i.user-image").attr("class")
                            .split("user-image user-image--img ")[1];
                    return thumbUrlIdentifier;
                });
    }

    @Override
    public String getDescription() throws ParsingException {
        return description;
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        return subscriberCount;
    }

    @Override
    public long getStreamCount() throws ParsingException {
        return -1;
    }

    @Override
    public boolean isVerified() throws ParsingException {
        return verified;
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
