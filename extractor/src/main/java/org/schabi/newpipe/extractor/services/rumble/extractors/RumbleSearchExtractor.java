package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.MetaInfo;
import org.schabi.newpipe.extractor.MultiInfoItemsCollector;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandler;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.services.rumble.linkHandler.RumbleSearchQueryHandlerFactory;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;

import javax.annotation.Nonnull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.schabi.newpipe.extractor.utils.Utils.isNullOrEmpty;

public class RumbleSearchExtractor extends SearchExtractor {
    private Document doc;
    RumbleCommonCodeTrendingAndSearching rumbleCommonCodeTrendingAndSearching;

    public RumbleSearchExtractor(final StreamingService service,
                                 final SearchQueryHandler linkHandler) {
        super(service, linkHandler);
        rumbleCommonCodeTrendingAndSearching = new RumbleCommonCodeTrendingAndSearching();

    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        doc = Jsoup.parse(getDownloader().get(getUrl()).responseBody());
    }

    @Nonnull
    @Override
    public String getSearchSuggestion() {
        return "";
    }

    @Override
    public boolean isCorrectedSearch() {
        return false;
    }

    @Nonnull
    @Override
    public List<MetaInfo> getMetaInfo() throws ParsingException {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        return extractAndGetInfoItemsFromPage();
    }

    private InfoItemsPage<InfoItem> extractAndGetInfoItemsFromPage()
            throws ExtractionException {
        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());

        final List<String> contentFilters = super.getLinkHandler().getContentFilters();
        int infoItemsListSize = 0;

        // default to video search if no content filter is set.
        String searchType = RumbleSearchQueryHandlerFactory.VIDEOS;
        if (!isNullOrEmpty(contentFilters)) {
            searchType = contentFilters.get(0);
        }

        if (searchType.equals(RumbleSearchQueryHandlerFactory.VIDEOS)) {
            final List<StreamInfoItemExtractor> infoItemsList =
                    rumbleCommonCodeTrendingAndSearching
                            .getSearchOrTrendingResultsItemList(doc);
            for (final StreamInfoItemExtractor infoItemExtractor : infoItemsList) {
                collector.commit(infoItemExtractor);
            }
            infoItemsListSize = infoItemsList.size();
        } else if (searchType.equals(RumbleSearchQueryHandlerFactory.CHANNELS)) {
            final List<RumbleChannelSearchInfoItemExtractor> infoItemsList =
                    extractChannelsFromSearchResult();
            for (final RumbleChannelSearchInfoItemExtractor infoItemExtractor : infoItemsList) {
                collector.commit(infoItemExtractor);
            }
            infoItemsListSize = infoItemsList.size();
        }

        final Page nextPage =
                rumbleCommonCodeTrendingAndSearching.getNewPageIfThereAreMoreThanOnePageResults(
                        infoItemsListSize, doc, getUrl() + "&page=");

        return new InfoItemsPage<>(collector, nextPage);
    }

    private List<RumbleChannelSearchInfoItemExtractor> extractChannelsFromSearchResult()
            throws ParsingException {
        final Elements elements = doc.select("li.video-listing-entry");
        final List<RumbleChannelSearchInfoItemExtractor> extractors = new ArrayList<>();

        for (final Element element : elements) {
            final RumbleChannelSearchInfoItemExtractor infoItem =
                    new RumbleChannelSearchInfoItemExtractor(element, doc);
            extractors.add(infoItem);
        }

        return extractors;
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(final Page page)
            throws IOException, ExtractionException {
        if (null == page) {
            return null;
        }

        doc = Jsoup.parse(getDownloader().get(page.getUrl()).responseBody());
        return extractAndGetInfoItemsFromPage();
    }
}
