package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.kiosk.KioskExtractor;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

public class RumbleTrendingExtractor extends KioskExtractor<StreamInfoItem> {

    private Document doc;
    private RumbleCommonCodeTrendingAndSearching rumbleCommonCodeTrendingAndSearching;

    public RumbleTrendingExtractor(StreamingService service,
                                    ListLinkHandler linkHandler,
                                    String kioskId) {
        super(service, linkHandler, kioskId);
        rumbleCommonCodeTrendingAndSearching = new RumbleCommonCodeTrendingAndSearching();
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        doc = Jsoup.parse(getDownloader().get(getUrl()).responseBody());
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) throws IOException, ExtractionException {
        if (null == page)
            return null;

        doc = Jsoup.parse(getDownloader().get(page.getUrl()).responseBody());
        return extractAndGetInfoItemsFromPage();
    }

    private InfoItemsPage<StreamInfoItem> extractAndGetInfoItemsFromPage() throws IOException, ExtractionException {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());

        List<StreamInfoItemExtractor> infoItemsList =
                rumbleCommonCodeTrendingAndSearching.getSearchOrTrendingResultsItemList(doc);

        for (StreamInfoItemExtractor infoItemExtractor : infoItemsList) {
            collector.commit(infoItemExtractor);
        }

        Page nextPage = rumbleCommonCodeTrendingAndSearching
                .getNewPageIfThereAreMoreThanOnePageResults(
                infoItemsList.size(), doc, getUrl()+ "?page=" );

        return new InfoItemsPage<>(collector, nextPage);
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return getId();
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        return extractAndGetInfoItemsFromPage();
    }
}
