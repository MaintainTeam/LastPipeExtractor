package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.services.rumble.RumbleChannelParsingHelper;
import org.schabi.newpipe.extractor.services.rumble.RumbleParsingHelper;

import static org.schabi.newpipe.extractor.ServiceList.Rumble;

public class RumbleChannelTabItemsExtractorImpl extends RumbleBrowseLiveItemExtractorImpl {

    @Override
    protected Elements getAllItems(final Document doc) {
        return doc.select("div.videostream.thumbnail__grid--item");
    }

    @Override
    protected String getStreamUploaderUrl(
            final Element element)
            throws ParsingException {
        final String errMsg = "Could not extract the uploader url";
        String uploaderUrl = RumbleParsingHelper.extractSafely(false, errMsg,
                () -> Rumble.getBaseUrl()
                        + element.select("address.video-item--by > a").first()
                        .attr("href"));

        if (null == uploaderUrl) { // default to Channel uploader url
            uploaderUrl = Rumble.getBaseUrl() + "/" + RumbleChannelParsingHelper
                    .getChannelId(element.parents().last());

            if (null == uploaderUrl) {
                throw new ParsingException(errMsg);
            }
        }
        return uploaderUrl;
    }

    @Override
    protected String getStreamUploaderName(
            final Element element)
            throws ParsingException {

        final String errMsg = "Could not extract the uploader name";
        String uploaderName = RumbleParsingHelper.extractSafely(false, errMsg,
                () -> element.select("address.video-item--by").first()
                        .getElementsByTag("div").first().text());

        if (null == uploaderName) { // default to Channel uploader url
            uploaderName = RumbleChannelParsingHelper
                    .getChannelName(element.parents().last());

            if (null == uploaderName) {
                throw new ParsingException(errMsg);
            }
        }
        return uploaderName;
    }
}
