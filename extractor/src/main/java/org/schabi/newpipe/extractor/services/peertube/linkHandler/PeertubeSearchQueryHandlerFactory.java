package org.schabi.newpipe.extractor.services.peertube.linkHandler;

import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;
import org.schabi.newpipe.extractor.utils.Utils;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.peertube.PeertubeHelpers;
import org.schabi.newpipe.extractor.services.peertube.search.filter.PeertubeFilters;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PeertubeSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static final String VIDEOS = "videos";
    // sepia is the global index
    public static final String SEPIA_BASE_URL = "https://sepiasearch.org";
    public static final String SEARCH_ENDPOINT_PLAYLISTS = "/api/v1/search/video-playlists";
    public static final String SEARCH_ENDPOINT_VIDEOS = "/api/v1/search/videos";
    public static final String SEARCH_ENDPOINT_CHANNELS = "/api/v1/search/video-channels";

    private static PeertubeSearchQueryHandlerFactory instance = null;

    private PeertubeSearchQueryHandlerFactory() {
        super(new PeertubeFilters());
    }

    /**
     * Singleton to get the same objects of filters during search.
     * <p>
     * The sort filter holds a variable search parameter: (filter.getQueryData())
     *
     * @return
     */
    public static synchronized PeertubeSearchQueryHandlerFactory getInstance() {
        if (instance == null) {
            instance = new PeertubeSearchQueryHandlerFactory();
        }
        return instance;
    }

    @Override
    public String getUrl(final String searchString,
                         @Nonnull final List<FilterItem> selectedContentFilter,
                         @Nullable final List<FilterItem> selectedSortFilters)
            throws ParsingException {

        final String baseUrl;
        if (isSepiaContentFilterPresent(selectedContentFilter)) {
            baseUrl = SEPIA_BASE_URL;
        } else {
            baseUrl = ServiceList.PeerTube.getBaseUrl();
        }

        return getUrl(searchString, selectedContentFilter, selectedSortFilters, baseUrl);
    }

    @Override
    public String getUrl(final String searchString,
                         final List<FilterItem> selectedContentFilter,
                         final List<FilterItem> selectedSortFilter,
                         final String baseUrl) throws ParsingException {
        try {
            searchFilters.setSelectedSortFilter(selectedSortFilter);
            searchFilters.setSelectedContentFilter(selectedContentFilter);

            final String filterQuery = searchFilters.evaluateSelectedFilters(null);

            String endpoint = SEARCH_ENDPOINT_VIDEOS;
            if (selectedContentFilter != null
                    && // SepiaFilter only supports SEARCH_ENDPOINT_VIDEOS
                    !isSepiaContentFilterPresent(selectedContentFilter)) {
                final Optional<FilterItem> contentFilter = selectedContentFilter.stream()
                        .filter(PeertubeFilters.PeertubeContentFilterItem.class::isInstance)
                        .findFirst();
                if (contentFilter.isPresent()) {
                    endpoint = ((PeertubeFilters.PeertubeContentFilterItem) contentFilter.get())
                            .getEndpoint();
                }
            }

            return baseUrl + endpoint + "?search=" + Utils.encodeUrlUtf8(searchString)
                    + filterQuery;
        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("Could not encode query", e);
        }
    }

    private boolean isSepiaContentFilterPresent(
            @Nullable final List<FilterItem> selectedContentFilter) {
        boolean isSepiaFilterPresent = false;
        if (selectedContentFilter != null) {
            final Optional<FilterItem> sepiaFilter =
                    PeertubeHelpers.getSepiaFilter(selectedContentFilter);
            if (sepiaFilter.isPresent()) {
                isSepiaFilterPresent = true;
            }
        }

        return isSepiaFilterPresent;
    }
}
