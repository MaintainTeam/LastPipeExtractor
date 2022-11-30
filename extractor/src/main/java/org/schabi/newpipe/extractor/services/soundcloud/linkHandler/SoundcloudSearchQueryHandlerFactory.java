package org.schabi.newpipe.extractor.services.soundcloud.linkHandler;

import static org.schabi.newpipe.extractor.services.soundcloud.SoundcloudParsingHelper.SOUNDCLOUD_API_V2_URL;

import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;
import org.schabi.newpipe.extractor.search.filter.FilterItem;
import org.schabi.newpipe.extractor.services.soundcloud.SoundcloudParsingHelper;
import org.schabi.newpipe.extractor.services.soundcloud.search.filter.SoundcloudFilters;
import org.schabi.newpipe.extractor.utils.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SoundcloudSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static final int ITEMS_PER_PAGE = 10;
    private static SoundcloudSearchQueryHandlerFactory instance = null;

    private SoundcloudSearchQueryHandlerFactory() {
        super(new SoundcloudFilters());
    }

    public static synchronized SoundcloudSearchQueryHandlerFactory getInstance() {
        if (instance == null) {
            instance = new SoundcloudSearchQueryHandlerFactory();
        }
        return instance;
    }

    @Override
    public String getUrl(final String id,
                         @Nonnull final List<FilterItem> selectedContentFilter,
                         @Nullable final List<FilterItem> selectedSortFilter)
            throws ParsingException {

        String url = SOUNDCLOUD_API_V2_URL + "search";
        String sortQuery = "";

        searchFilters.setSelectedContentFilter(selectedContentFilter);
        searchFilters.setSelectedSortFilter(selectedSortFilter);
        url += searchFilters.evaluateSelectedContentFilters();
        sortQuery = searchFilters.evaluateSelectedSortFilters();

        try {
            return url + "?q=" + Utils.encodeUrlUtf8(id)
                    + "&client_id=" + SoundcloudParsingHelper.clientId()
                    + "&limit=" + ITEMS_PER_PAGE + "&offset=0"
                    + sortQuery;

        } catch (final UnsupportedEncodingException e) {
            throw new ParsingException("Could not encode query", e);
        } catch (final ReCaptchaException e) {
            throw new ParsingException("ReCaptcha required", e);
        } catch (final IOException | ExtractionException e) {
            throw new ParsingException("Could not get client id", e);
        }
    }
}
