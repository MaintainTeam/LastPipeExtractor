package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
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
 * Currently only supporting the 'livestream browse' of Rumble
 */
public class RumbleBrowseCategory extends RumbleCommonCodeTrendingAndSearching {

    @SuppressWarnings("checkstyle:InvalidJavadocPosition")
    public List<StreamInfoItemExtractor> getSearchOrTrendingResultsItemList(final Document doc)
            throws ParsingException {
        final List<StreamInfoItemExtractor> list = new LinkedList<>();

        final Elements elements = doc.select("div.videostream");
        StreamInfoItemExtractor infoItemExtractor;

        for (final Element element : elements) {

            /** set {@value shouldThrowOnError} to 'false' as live events sometimes have no views
             * or obviously no duration at all. see {@link RumbleTrendingLinkHandlerFactory.LIVE} */
            final String views =
                    RumbleParsingHelper.extractSafely(false, "Could not extract the view count",
                            () -> element.getElementsByClass("videostream__number").first().text());
            final String duration =
                    RumbleParsingHelper.extractSafely(false, "Could not extract the duration",
                            () -> getClassValue(element, "video-item--duration", "data-value"));

            final String msg = "Could not extract the thumbnail url";
            final String thumbUrl = RumbleParsingHelper.extractSafely(true, msg,
                    () -> element.select("img.videostream__image")
                            .first().absUrl("src"));

            // currently only category live supported -> no Date -> see inherited class
            final String textualDate = null;

            final String title =
                    RumbleParsingHelper.extractSafely(true, "Could not extract the stream title",
                            () -> element.select("h3.videostream__title")
                                    .first().text());
            final String url =
                    RumbleParsingHelper.extractSafely(true, "Could not extract the stream url",
                            () -> Rumble.getBaseUrl()
                                    + element.select("a.videostream__link")
                                    .first().attr("href"));
            final String uploaderUrl =
                    RumbleParsingHelper.extractSafely(true, "Could not extract the uploader url",
                            () -> Rumble.getBaseUrl()
                                    + element.select("a.channel__link").first()
                                    .attr("href"));
            final String uploader =
                    RumbleParsingHelper.extractSafely(true, "Could not extract the uploader name",
                            () -> element.select("span.channel__name").first()
                                    .text());

            boolean isLive = false;
            final String liveStream =
                    RumbleParsingHelper.extractSafely(false, "Could not extract the live thingy",
                            () -> element.select(
                                            "div.videostream__badge.videostream__status"
                                                    + ".videostream__status--live")
                                    .first().text());
            if ("LIVE".equals(liveStream)) {
                isLive = true;
            }

            final DateWrapper uploadDate = null;
            if (null != textualDate) {
                new DateWrapper(OffsetDateTime.parse(textualDate), false);
            }

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
}
