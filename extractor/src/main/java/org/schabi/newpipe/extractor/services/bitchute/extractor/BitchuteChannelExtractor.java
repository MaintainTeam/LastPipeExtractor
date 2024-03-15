package org.schabi.newpipe.extractor.services.bitchute.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.schabi.newpipe.extractor.Image;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelExtractor;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants;
import org.schabi.newpipe.extractor.services.bitchute.BitchuteParserHelper;
import org.schabi.newpipe.extractor.services.bitchute.linkHandler.BitchuteChannelTabLinkHandlerFactory;
import org.schabi.newpipe.extractor.utils.BraveNewPipeExtractorUtils;
import org.schabi.newpipe.extractor.utils.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class BitchuteChannelExtractor extends ChannelExtractor {
    private Document doc;
    private String channelName;
    private String avatarUrl;

    public BitchuteChannelExtractor(final StreamingService service,
                                    final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        final Response response = getDownloader().get(getUrl(),
                BitchuteParserHelper.getBasicHeader());
        doc = Jsoup.parse(response.responseBody(), getUrl());
    }

    private String getChannelID() {
        final String canonicalUrl = doc.getElementById("canonical").attr("href");
        final String[] urlSegments = canonicalUrl.split("/");
        return urlSegments[urlSegments.length - 1];
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        if (channelName == null) {
            channelName = BitchuteParserHelper.getChannelName(doc);
        }
        return channelName;
    }

    @Nonnull
    @Override
    public List<Image> getAvatars() throws ParsingException {
        try {
            if (avatarUrl == null) {
                avatarUrl = doc.select("#page-bar > div > div > div.image-container > img")
                        .first().attr("data-src");
                if (avatarUrl.startsWith("/")) {
                    avatarUrl = BitchuteConstants.BASE_URL + avatarUrl;
                }
            }
            return List.of(
                    new Image(avatarUrl,
                            Image.HEIGHT_UNKNOWN,
                            Image.WIDTH_UNKNOWN,
                            Image.ResolutionLevel.UNKNOWN));
        } catch (final Exception e) {
            throw new ParsingException("Error parsing Channel Avatar Url");
        }
    }

    @Override
    public String getDescription() throws ParsingException {
        try {
            return doc.select("#channel-description").first().text();
        } catch (final Exception e) {
            throw new ParsingException("Error parsing Channel Description");
        }
    }

    @Override
    public String getParentChannelName() throws ParsingException {
        return null;
    }

    @Override
    public String getParentChannelUrl() throws ParsingException {
        return null;
    }

    @Nonnull
    @Override
    public List<Image> getParentChannelAvatars() throws ParsingException {
        return Collections.emptyList();
    }

    @Override
    public boolean isVerified() throws ParsingException {
        return false;
    }

    @Nonnull
    @Override
    public List<ListLinkHandler> getTabs() throws ParsingException {
        final String id = getId();

        final Map<FilterItem, String> tab2Suffix =
                BitchuteChannelTabLinkHandlerFactory.getTab2UrlSuffixes();

        return BraveNewPipeExtractorUtils.generateTabsFromSuffixMap(getUrl(), id, tab2Suffix);
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        try {
            return Utils.mixedNumberWordToLong(BitchuteParserHelper
                    .getSubscriberCountForChannelID(getChannelID()));
        } catch (final Exception e) {
            throw new ParsingException("Error parsing Channel Subscribers");
        }
    }

    @Nonnull
    @Override
    public List<Image> getBanners() throws ParsingException {
        return getAvatars();
    }

    @Override
    public String getFeedUrl() {
        return null;
    }
}
