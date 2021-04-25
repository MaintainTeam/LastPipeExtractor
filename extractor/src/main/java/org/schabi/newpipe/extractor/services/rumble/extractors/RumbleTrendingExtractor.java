package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.kiosk.KioskExtractor;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;

import java.io.IOException;
import java.time.OffsetDateTime;

import javax.annotation.Nonnull;

import static org.schabi.newpipe.extractor.ServiceList.Rumble;

public class RumbleTrendingExtractor extends KioskExtractor<StreamInfoItem> {

    public RumbleTrendingExtractor(StreamingService service,
                                    ListLinkHandler linkHandler,
                                    String kioskId) {
        super(service, linkHandler, kioskId);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        // TODO
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) throws IOException, ExtractionException {
        if (null == page)
            return null;

        return null; // TODO
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return ""; // TODO
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        return null; // TODO
    }
}
