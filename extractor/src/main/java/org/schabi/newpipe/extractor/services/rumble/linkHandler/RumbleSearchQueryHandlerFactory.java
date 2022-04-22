package org.schabi.newpipe.extractor.services.rumble.linkHandler;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static org.schabi.newpipe.extractor.utils.Utils.UTF_8;

public class RumbleSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static final String ALL = "all";
    public static final String VIDEOS = "videos";
    public static final String CHANNELS = "channels";

    private static final String SEARCH_VIDEOS_URL = "https://rumble.com/search/video?q=";
    private static final String SEARCH_CHANNEL_URL = "https://rumble.com/search/channel?q=";

    public static RumbleSearchQueryHandlerFactory getInstance() {
        return new RumbleSearchQueryHandlerFactory();
    }

    @Override
    public String getUrl(final String searchString, final List<String> contentFilters,
                         final String sortFilter)
            throws ParsingException {
        try {
            if (!contentFilters.isEmpty()) {

                // TODO -> Rumble allows also to search for user and for channels
                switch (contentFilters.get(0)) {
                    case ALL:
                    default:
                        break;
                    case VIDEOS:
                        return SEARCH_VIDEOS_URL + URLEncoder.encode(searchString, UTF_8);
                    case CHANNELS:
                        return SEARCH_CHANNEL_URL + URLEncoder.encode(searchString, UTF_8);
                    //TODO case for user as we can search for search/user?=...
                }
            }

            // we default to searching videos
            return SEARCH_VIDEOS_URL + URLEncoder.encode(searchString, UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("Could not encode query", e);
        }
    }

    @Override
    public String[] getAvailableContentFilter() {
        return new String[] {
                ALL,
                VIDEOS,
                CHANNELS
        };
    }
}
