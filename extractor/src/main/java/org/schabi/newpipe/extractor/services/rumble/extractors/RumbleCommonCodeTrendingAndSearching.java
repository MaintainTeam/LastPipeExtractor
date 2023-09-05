package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
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

    public Page getNewPageIfThereAreMoreThanOnePageResults(final int numberOfCollectedItems,
                                                           final Document doc,
                                                           final String urlPrefix) {

        Page nextPage = null;

        // -- check if there is a next page --
        // If numberOfCollectedItems is 0 than we have no results at all
        // -> assume no more pages
        if (numberOfCollectedItems > 0) {
            final String currentPageStrNumber =
                    doc.getElementsByClass("paginator--link--current").attr("aria-label");
            final boolean hasMorePages;
            if (currentPageStrNumber.isEmpty()) {
                hasMorePages = false;
            } else {
                // check if we are on the last page of available search results
                final int currentPageIsLastPageIfGreaterThanZero =
                        doc.getElementsByClass("paginator--link").last()
                                .getElementsByClass("paginator--link paginator--link--current")
                                .size();
                hasMorePages = !(currentPageIsLastPageIfGreaterThanZero > 0);
            }

            if (hasMorePages) {
                int currentPageNumber = Integer.parseInt(currentPageStrNumber);
                nextPage = new Page(urlPrefix + ++currentPageNumber);
            }
        }
        return nextPage;
    }

    @SuppressWarnings("checkstyle:InvalidJavadocPosition")
    public List<StreamInfoItemExtractor> getSearchOrTrendingResultsItemList(final Document doc)
            throws ParsingException {
        final List<StreamInfoItemExtractor> list = new LinkedList<>();

        final Elements elements = doc.select("li.video-listing-entry");
        StreamInfoItemExtractor infoItemExtractor;

        for (final Element element : elements) {

            /** set {@value shouldThrowOnError} to 'false' as live events sometimes have no views
             * or obviously no duration at all. see {@link RumbleTrendingLinkHandlerFactory.LIVE} */
            final String views =
                    RumbleParsingHelper.extractSafely(false, "Could not extract the view count",
                            () -> element.getElementsByClass("video-item--views").first().text());
            final String duration =
                    RumbleParsingHelper.extractSafely(false, "Could not extract the duration",
                            () -> getClassValue(element, "video-item--duration", "data-value"));

            final String msg = "Could not extract the thumbnail url";
            String thumbUrl = RumbleParsingHelper.extractSafely(false, msg,
                    () -> element.select("img.video-item--img").first().absUrl("src"));
            if (null == thumbUrl) {
                /** In case of the battle-leaderboard the image might be found here
                 * see {@link RumbleTrendingLinkHandlerFactory.TODAYS_BATTLE_LEADERBOARD_TOP_50} */
                thumbUrl = RumbleParsingHelper.extractSafely(true, msg,
                        () -> element.select("img.video-item--img-img").first().absUrl("src"));
            }

            final String textualDate = RumbleParsingHelper
                    .extractSafely(true, "Could not extract the textual upload date",
                            () -> getClassValue(element, "video-item--time", "datetime"));
            final String title =
                    RumbleParsingHelper.extractSafely(true, "Could not extract the stream title",
                            () -> element.select("h3.video-item--title").first().childNodes().get(0)
                                    .toString());
            final String url =
                    RumbleParsingHelper.extractSafely(true, "Could not extract the stream url",
                            () -> Rumble.getBaseUrl()
                                    + element.select("a.video-item--a").first().attr("href"));
            final String uploaderUrl =
                    RumbleParsingHelper.extractSafely(true, "Could not extract the uploader url",
                            () -> Rumble.getBaseUrl()
                                    + element.select("address.video-item--by > a").first()
                                    .attr("href"));
            final String uploader =
                    RumbleParsingHelper.extractSafely(true, "Could not extract the uploader name",
                            () -> element.select("address.video-item--by").first()
                                    .getElementsByTag("div").first().text());

            boolean isLive = false;
            final String liveStream =
                    RumbleParsingHelper.extractSafely(false, "Could not extract the live thingy",
                            () -> getClassValue(element, "video-item--live", "data-value"));
            if ("LIVE".equals(liveStream)) {
                isLive = true;
            }

            final DateWrapper uploadDate =
                    new DateWrapper(OffsetDateTime.parse(textualDate), false);

            infoItemExtractor = new RumbleSearchVideoStreamInfoItemExtractor(
                    Parser.unescapeEntities(title, false),
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

            list.add(infoItemExtractor);
        }

        return list;
    }

    protected String getClassValue(final Element element,
                                   final String className,
                                   final String attr) {
        return element.getElementsByClass(className).first().attr(attr);
    }
}
