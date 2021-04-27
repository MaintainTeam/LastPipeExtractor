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
import org.schabi.newpipe.extractor.services.rumble.linkHandler.RumbleTrendingLinkHandlerFactory;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;

import java.io.IOException;
import java.time.OffsetDateTime;

import javax.annotation.Nonnull;

import static org.schabi.newpipe.extractor.ServiceList.Rumble;

/**
 * TODO This implementation is build on RumbleSearchExtractor and should be merged into one class later.
 */
public class RumbleTrendingExtractor extends KioskExtractor<StreamInfoItem> {
    private Document doc;

    public RumbleTrendingExtractor(StreamingService service,
                                    ListLinkHandler linkHandler,
                                    String kioskId) {
        super(service, linkHandler, kioskId);
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

    private String getClassValue(Element element, String className, String attr) {
        return element.getElementsByClass(className).first().attr(attr);
    }

    private InfoItemsPage<StreamInfoItem> extractAndGetInfoItemsFromPage() throws IOException, ExtractionException {
        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());

        Elements elements = doc.select("li.video-listing-entry");
        StreamInfoItemExtractor infoItemExtractor;

        for (Element element : elements) {
            String duration = getClassValue(element, "video-item--duration", "data-value");
            String views = null;

            /**
             * catch exception for live events there are sometimes no views at all
             * see {@link RumbleTrendingLinkHandlerFactory.LIVE} */
            try {
                views = getClassValue(element, "video-item--views", "data-value");
            } catch (NullPointerException e) {
            }
            String textualDate = getClassValue(element, "video-item--time", "datetime");
            String title = element.select("h3.video-item--title").first().childNodes().get(0).toString();

            String thumbUrl = null;
            try {
                thumbUrl = element.select("img.video-item--img").first().absUrl("src");
            } catch (NullPointerException e) {
                /**
                 * In case of the battle-leaderboard the image might be found here
                 * see {@link RumbleTrendingLinkHandlerFactory.TODAYS_BATTLE_LEADERBOARD_TOP_50} */
                thumbUrl = element.select("img.video-item--img-img").first().absUrl("src");
            }
            String url = Rumble.getBaseUrl() + element.select("a.video-item--a").first().attr("href");
            String uploaderUrl = Rumble.getBaseUrl() + element.select("address.video-item--by > a").first().attr("href");
            String uploader = element.select("address.video-item--by").first().getElementsByTag("div").first().text();
            DateWrapper uploadDate = new DateWrapper(OffsetDateTime.parse(textualDate), false);

            //switch (kind) {
            //    case BitchuteConstants.KIND_CHANNEL:
            //        infoItemExtractor = new BitchuteSearchExtractor.BitchuteQuickChannelInfoItemExtractor(
            //                name,
            //                url,
            //                thumbUrl,
            //                desc
            //        );
            //        break;
            //    case BitchuteConstants.KIND_VIDEO:
            //    default:
                    infoItemExtractor = new RumbleSearchExtractor.RumbleSearchVideoStreamInfoItemExtractor(
                            title,
                            url,
                            thumbUrl,
                            views,
                            textualDate,
                            duration,
                            uploader,
                            uploaderUrl,
                            uploadDate
                    );
            //}

            collector.commit(infoItemExtractor);
        }

        Page nextPage = RumbleSearchExtractor.CommonCodeBetweenTrendingAndSearching
                .getNewPageIfThereAreMoreThanOnePageResults(
                elements, doc, getUrl()+ "?page=" );

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
