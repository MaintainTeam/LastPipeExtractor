package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelExtractor;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.services.rumble.linkHandler.RumbleChannelLinkHandlerFactory;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;
import org.schabi.newpipe.extractor.utils.Utils;

import java.io.IOException;

import javax.annotation.Nonnull;

import static org.schabi.newpipe.extractor.utils.Utils.isNullOrEmpty;


public class RumbleChannelExtractor extends ChannelExtractor {

    public RumbleChannelExtractor(StreamingService service, ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
    }

    @Nonnull
    @Override
    public String getUrl() throws ParsingException {
        try {
           // TODO
            return RumbleChannelLinkHandlerFactory.getInstance().getUrl("channel/" + getId());
        } catch (ParsingException e) {
            return super.getUrl();
        }
    }

    @Nonnull
    @Override
    public String getId() throws ParsingException {
        final String channelId = ""; // TODO

        if (!channelId.isEmpty()) {
            return channelId;
        } else {
            throw new ParsingException("Could not get channel id");
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        try {
            return ""; // TODO
        } catch (Exception e) {
            throw new ParsingException("Could not get channel name", e);
        }
    }

    @Override
    public String getAvatarUrl() throws ParsingException {
        try {
            String url = ""; // TODO

            return url;
        } catch (Exception e) {
            throw new ParsingException("Could not get avatar", e);
        }
    }

    @Override
    public String getBannerUrl() throws ParsingException {
        try {
            String url = ""; // TODO

            if (url == null || url.isEmpty()) {
                return null;
            }

            return url;
        } catch (Exception e) {
            throw new ParsingException("Could not get banner", e);
        }
    }

    @Override
    public String getFeedUrl() throws ParsingException {
        return null; // there is no feed
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        if (true /* hasSubscriberCountText*/) {
            String viewCount = ""; // TODO
            try {
                return Utils.mixedNumberWordToLong(viewCount);
            } catch (NumberFormatException e) {
                throw new ParsingException("Could not get subscriber count", e);
            }
        } else {
            return ITEM_COUNT_UNKNOWN;
        }
    }

    @Override
    public String getDescription() throws ParsingException {
        try {
            return ""; // TODO
        } catch (Exception e) {
            throw new ParsingException("Could not get channel description", e);
        }
    }

    @Override
    public String getParentChannelName() throws ParsingException {
        return "";
    }

    @Override
    public String getParentChannelUrl() throws ParsingException {
        return "";
    }

    @Override
    public String getParentChannelAvatarUrl() throws ParsingException {
        return "";
    }

    @Override
    public boolean isVerified() throws ParsingException {
        return false; // TODO
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());

        // TODO
        Page nextPage = null;

        return new InfoItemsPage<>(collector, nextPage);
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) throws IOException, ExtractionException {
        if (page == null || isNullOrEmpty(page.getUrl())) {
            throw new IllegalArgumentException("Page doesn't contain an URL");
        }
        // TODO

        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());

        Page nextPage = null;

        return new InfoItemsPage<>(collector, nextPage);
    }
}
