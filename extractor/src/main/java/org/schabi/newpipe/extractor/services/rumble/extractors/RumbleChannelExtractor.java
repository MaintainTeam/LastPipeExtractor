package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelExtractor;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.services.rumble.RumbleParsingHelper;
import org.schabi.newpipe.extractor.services.rumble.linkHandler.RumbleChannelLinkHandlerFactory;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.utils.Utils;

import java.io.IOException;

import javax.annotation.Nonnull;

public class RumbleChannelExtractor extends ChannelExtractor {

    private RumbleCommonCodeTrendingAndChannel sharedTrendingAndChannelCode;
    private Document doc;

    public RumbleChannelExtractor(StreamingService service, ListLinkHandler linkHandler) {
        super(service, linkHandler);

        try {
            sharedTrendingAndChannelCode = new RumbleCommonCodeTrendingAndChannel(getServiceId(), getUrl());
        } catch (ParsingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        doc = Jsoup.parse(getDownloader().get(getUrl()).responseBody());
    }

    @Nonnull
    @Override
    public String getUrl() throws ParsingException {
        try {
            return RumbleChannelLinkHandlerFactory.getInstance().getUrl( getId());
        } catch (ParsingException e) {
            return super.getUrl();
        }
    }

    @Nonnull
    @Override
    public String getId() throws ParsingException {
        String channelId = RumbleParsingHelper.extractSafely(true,
                "Could not get channel id",
                ()-> {
                    final String cssQuery = "div.listing-header--content > div > button";
                    String channelName =doc.select(cssQuery).first().attr("data-slug");
                    String type = doc.select(cssQuery).first().attr("data-type");
                    if ("channel".equals(type)) {
                        return "c/" + channelName;
                    } else if ("user".equals(type)) {
                        return "user/" + channelName;
                    }
                    return null;
                });
        return channelId;
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        String name = RumbleParsingHelper.extractSafely(true,
                "Could not get channel name",
                () -> doc.getElementsByTag("title").first().text()
        );
        return name;
    }

    @Override
    public String getAvatarUrl() throws ParsingException {
        String url = RumbleParsingHelper.extractSafely(true,
                "Could not get avatar url",
                ()-> doc.select("div.listing-header--content > img").attr("src")
        );
        return url;
    }

    @Override
    public String getBannerUrl() throws ParsingException {
        String bannerUrl = RumbleParsingHelper.extractSafely(false,
                "Could not get banner",
                () -> doc.select("div.listing-header--backsplash > div > img").attr("src")
        );
        return bannerUrl;
    }

    @Override
    public String getFeedUrl() throws ParsingException {
        return null; // there is no feed
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        final String errorMsg = "Could not get subscriber count";

        String viewCount = RumbleParsingHelper.extractSafely(true,
                errorMsg,
                () -> doc.select("span.subscribe-button-count").first().text()
        );

        if (null != viewCount) {
            try {
                return Utils.mixedNumberWordToLong(viewCount);
            } catch (NumberFormatException e) {
                throw new ParsingException(errorMsg, e);
            }
        } else {
            return ITEM_COUNT_UNKNOWN;
        }
    }

    @Override
    public String getDescription() throws ParsingException {
        return ""; // There is no description
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
        String verified = RumbleParsingHelper.extractSafely(false,
                "",
                () -> doc.select("svg.listing-header--verified").first().text()
        );

        if (verified == null)
            return false;
        else
            return true;
    }


    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) throws IOException, ExtractionException {
        if (null == page)
            return null;

        doc = Jsoup.parse(getDownloader().get(page.getUrl()).responseBody());
        return sharedTrendingAndChannelCode.extractAndGetInfoItemsFromPage(doc);
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        return sharedTrendingAndChannelCode.extractAndGetInfoItemsFromPage(doc);
    }
}
