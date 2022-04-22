package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.nodes.Document;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;

import java.io.IOException;
import java.util.List;

/**
 * shared code for {@link RumbleTrendingExtractor} and {@link RumbleChannelExtractor}
 */
public class RumbleCommonCodeTrendingAndChannel {
    private final int serviceId;
    private final String baseUrl;
    private final RumbleCommonCodeTrendingAndSearching rumbleCommonCodeTrendingAndSearching;

    RumbleCommonCodeTrendingAndChannel(final int serviceId,
                                       final String baseUrl) {
        this.serviceId = serviceId;
        this.rumbleCommonCodeTrendingAndSearching = new RumbleCommonCodeTrendingAndSearching();
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
                        infoItemsList.size(), doc, baseUrl + "?page=");

        return new ListExtractor.InfoItemsPage<>(collector, nextPage);
    }
}
