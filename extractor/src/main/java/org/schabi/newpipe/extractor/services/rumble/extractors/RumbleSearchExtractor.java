package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;

import javax.annotation.Nonnull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RumbleSearchExtractor extends SearchExtractor {
    private Document doc;
    RumbleCommonCodeTrendingAndSearching rumbleCommonCodeTrendingAndSearching;

    public RumbleSearchExtractor(final StreamingService service, final SearchQueryHandler linkHandler) {
        super(service, linkHandler);
        rumbleCommonCodeTrendingAndSearching = new RumbleCommonCodeTrendingAndSearching();

    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws IOException, ExtractionException {
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

    @Override
    public List<MetaInfo> getMetaInfo() throws ParsingException {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws IOException, ExtractionException {
        return extractAndGetInfoItemsFromPage();
    }

    private InfoItemsPage<InfoItem> extractAndGetInfoItemsFromPage() throws IOException, ExtractionException {
        final MultiInfoItemsCollector collector = new MultiInfoItemsCollector(getServiceId());

        List<StreamInfoItemExtractor> infoItemsList =
                rumbleCommonCodeTrendingAndSearching.getSearchOrTrendingResultsItemList(doc);

        for (StreamInfoItemExtractor infoItemExtractor : infoItemsList) {
            collector.commit(infoItemExtractor);
        }

        Page nextPage = rumbleCommonCodeTrendingAndSearching.getNewPageIfThereAreMoreThanOnePageResults(
                infoItemsList.size(), doc, getUrl()+ "&page=" );

        return new InfoItemsPage<>(collector, nextPage);
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(final Page page) throws IOException, ExtractionException {
        if (null == page)
            return null;

        doc = Jsoup.parse(getDownloader().get(page.getUrl()).responseBody());
        return extractAndGetInfoItemsFromPage();
    }
}
