package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    public RumbleChannelExtractor(final StreamingService service,
                                  final ListLinkHandler linkHandler) {
        super(service, linkHandler);

        try {
            sharedTrendingAndChannelCode =
                    new RumbleCommonCodeTrendingAndChannel(getServiceId(), getUrl());
        } catch (final ParsingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        doc = Jsoup.parse(getDownloader().get(getUrl()).responseBody());
    }

    @Nonnull
    @Override
    public String getUrl() throws ParsingException {
        try {
            return RumbleChannelLinkHandlerFactory.getInstance().getUrl(getId());
        } catch (final ParsingException e) {
            return super.getUrl();
        }
    }

    @Nonnull
    @Override
    public String getId() throws ParsingException {
        final String channelId = RumbleParsingHelper.extractSafely(true,
                "Could not get channel id",
                () -> getChannelId());
        return channelId;
    }

    private String getChannelId() {
        final Element idData =
                doc.select("div[class~=(listing|channel)-header--buttons] div").first();
        final String channelName = idData.attr("data-slug");
        final String type = idData.attr("data-type");
        if ("channel".equals(type)) {
            return "c/" + channelName;
        } else if ("user".equals(type)) {
            return "user/" + channelName;
        }
        return null;
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        final String name = RumbleParsingHelper.extractSafely(true,
                "Could not get channel name",
                () -> doc.getElementsByTag("title").first().text()
        );
        return name;
    }

    @Override
    public String getAvatarUrl() throws ParsingException {
        final String url = RumbleParsingHelper.extractSafely(true,
                "Could not get avatar url",
                () -> doc.select("div[class~=(channel|listing)-header--content] img")
                        .first().attr("src")
        );
        return url;
    }

    @Override
    public String getBannerUrl() throws ParsingException {
        return RumbleParsingHelper.extractSafely(false,
                "Could not get banner url",
                this::extractBannerUrl);
    }

    private String extractBannerUrl() {
        final Elements elements =
                doc.select("div[class~=(channel|listing)-header--backsplash] img");
        if (elements.isEmpty()) { // some have no banner so return null
            return null;
        } else {
            return elements.first().attr("src");
        }
    }

    @Override
    public String getFeedUrl() throws ParsingException {
        return null; // there is no feed
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        final String errorMsg = "Could not get subscriber count";

        final String viewCount = RumbleParsingHelper.extractSafely(true,
                errorMsg,
                () -> doc.select("span.subscribe-button-count").first().text()
        );

        if (null != viewCount) {
            try {
                return Utils.mixedNumberWordToLong(viewCount);
            } catch (final NumberFormatException e) {
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
        final String verified = RumbleParsingHelper.extractSafely(false,
                "",
                () -> doc.select("svg.listing-header--verified").first().text()
        );

        return verified != null;
    }


    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        if (null == page) {
            return null;
        }

        doc = Jsoup.parse(getDownloader().get(page.getUrl()).responseBody());
        return sharedTrendingAndChannelCode.extractAndGetInfoItemsFromPage(doc);
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        return sharedTrendingAndChannelCode.extractAndGetInfoItemsFromPage(doc);
    }
}
