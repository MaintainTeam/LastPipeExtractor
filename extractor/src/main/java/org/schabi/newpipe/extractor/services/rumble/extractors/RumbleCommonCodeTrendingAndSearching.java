package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.services.rumble.RumbleParsingHelper;
import org.schabi.newpipe.extractor.services.rumble.linkHandler.RumbleTrendingLinkHandlerFactory;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.schabi.newpipe.extractor.ServiceList.Rumble;

/**
 * common code shared between {@link RumbleSearchExtractor} and {@link RumbleTrendingExtractor}
 */
public class RumbleCommonCodeTrendingAndSearching {

    public Page getNewPageIfThereAreMoreThanOnePageResults(int numberOfCollectedItems, Document doc, String urlPrefix) {

        Page nextPage = null;

        // check if there is a next page
        if (numberOfCollectedItems > 0) { // if numberOfCollectedItems is 0 than we have no results at all -> assume no more pages
            String currentPageStrNumber = doc.getElementsByClass("paginator--link--current").attr("aria-label");
            boolean hasMorePages;
            if (currentPageStrNumber.isEmpty()) {
                hasMorePages = false;
            } else {
                // check if we are on the last page of available search results
                int currentPageIsLastPageIfGreaterThanZero =
                        doc.getElementsByClass("paginator--link").last()
                                .getElementsByClass("paginator--link paginator--link--current").size();
                hasMorePages = (currentPageIsLastPageIfGreaterThanZero > 0) ? false : true;
            }

            if (hasMorePages) {
                int currentPageNumber = Integer.parseInt(currentPageStrNumber);
                nextPage = new Page(urlPrefix + ++currentPageNumber);
            }
        }
        return nextPage;
    }

    public List<StreamInfoItemExtractor> getSearchOrTrendingResultsItemList(Document doc) throws ParsingException {
        List<StreamInfoItemExtractor> list = new LinkedList<>();

        Elements elements = doc.select("li.video-listing-entry");
        StreamInfoItemExtractor infoItemExtractor;

        for (Element element : elements) {

            /** set {@value shouldThrowOnError} to 'false' as live events sometimes have no views
             * or obviously no duration at all. see {@link RumbleTrendingLinkHandlerFactory.LIVE} */
            String views = RumbleParsingHelper.extractSafely(false, "Could not extract the view count",
                    () -> getClassValue(element, "video-item--views", "data-value") );
            String duration = RumbleParsingHelper.extractSafely(false, "Could not extract the duration",
                    () -> getClassValue(element, "video-item--duration", "data-value"));

            String msg = "Could not extract the thumbnail url";
            String thumbUrl = RumbleParsingHelper.extractSafely(false, msg,
                    () -> element.select("img.video-item--img").first().absUrl("src") );
            if (null == thumbUrl) {
                /** In case of the battle-leaderboard the image might be found here
                 * see {@link RumbleTrendingLinkHandlerFactory.TODAYS_BATTLE_LEADERBOARD_TOP_50} */
                thumbUrl = RumbleParsingHelper.extractSafely(true, msg,
                        () -> element.select("img.video-item--img-img").first().absUrl("src") );
            }

            String textualDate = RumbleParsingHelper.extractSafely(true, "Could not extract the textual upload date",
                    () -> getClassValue(element, "video-item--time", "datetime") );
            String title = RumbleParsingHelper.extractSafely(true, "Could not extract the stream title",
                    () -> element.select("h3.video-item--title").first().childNodes().get(0).toString() );
            String url = RumbleParsingHelper.extractSafely(true, "Could not extract the stream url",
                    () -> Rumble.getBaseUrl() + element.select("a.video-item--a").first().attr("href") );
            String uploaderUrl = RumbleParsingHelper.extractSafely(true, "Could not extract the uploader url",
                    () -> Rumble.getBaseUrl() + element.select("address.video-item--by > a").first().attr("href") );
            String uploader = RumbleParsingHelper.extractSafely(true, "Could not extract the uploader name",
                    () -> element.select("address.video-item--by").first().getElementsByTag("div").first().text() );

            boolean isLive = false;
            String liveStream = RumbleParsingHelper.extractSafely(false, "Could not extract the live thingy",
                    () -> getClassValue(element, "video-item--live", "data-value") );
            if ("LIVE".equals(liveStream)) {
                isLive = true;
            }

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
            infoItemExtractor = new RumbleSearchVideoStreamInfoItemExtractor(
                    title,
                    url,
                    thumbUrl,
                    views,
                    textualDate,
                    duration,
                    uploader,
                    uploaderUrl,
                    uploadDate,
                    isLive
            );
            //}

            list.add(infoItemExtractor);
        }

        return list;
    }

    private String getClassValue(Element element, String className, String attr) {
        return element.getElementsByClass(className).first().attr(attr);
    }
}
