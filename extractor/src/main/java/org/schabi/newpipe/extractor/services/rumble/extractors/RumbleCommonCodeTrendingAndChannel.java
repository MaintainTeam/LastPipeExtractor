package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.nodes.Document;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.services.rumble.linkHandler.RumbleTrendingLinkHandlerFactory;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

/**
 * shared code for {@link RumbleTrendingExtractor} and {@link RumbleChannelExtractor}
 */
public class RumbleCommonCodeTrendingAndChannel {
    private final int serviceId;
    private final String baseUrl;
    private final RumbleCommonCodeTrendingAndSearching rumbleCommonCodeTrendingAndSearching;

    RumbleCommonCodeTrendingAndChannel(final int serviceId,
                                       final String baseUrl,
                                       final @Nullable String kioskId) {
        this.serviceId = serviceId;

        // ATM. we only use the live category for the trending list
        if (RumbleTrendingLinkHandlerFactory.LIVE.equals(kioskId)) {
            this.rumbleCommonCodeTrendingAndSearching = new RumbleBrowseCategory();
        } else {
            this.rumbleCommonCodeTrendingAndSearching = new RumbleCommonCodeTrendingAndSearching();
        }
        this.baseUrl = baseUrl;
    }

    public ListExtractor.InfoItemsPage<StreamInfoItem> extractAndGetInfoItemsFromPage(
            final Document doc)
            throws IOException, ExtractionException {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(serviceId);

        final List<StreamInfoItemExtractor> infoItemsList =
                rumbleCommonCodeTrendingAndSearching.getSearchOrTrendingResultsItemList(doc);

        for (final StreamInfoItemExtractor infoItemExtractor : infoItemsList) {
            collector.commit(infoItemExtractor);
        }

        final Page nextPage = rumbleCommonCodeTrendingAndSearching
                .getNewPageIfThereAreMoreThanOnePageResults(
                        infoItemsList.size(), doc, generateNextPageUrl(baseUrl, "page="));

        return new ListExtractor.InfoItemsPage<>(collector, nextPage);
    }

    private String generateNextPageUrl(final String url,
                                       final String pageParameter) {
        if (url.contains("?")) {
            // already parameterized -> append parameter
            return url + '&' + pageParameter;
        }
        return url + '?' + pageParameter;
    }
}
