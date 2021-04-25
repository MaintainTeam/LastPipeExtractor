package org.schabi.newpipe.extractor.services.rumble.linkHandler;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;

import java.util.List;

public class RumbleSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static RumbleSearchQueryHandlerFactory getInstance() {
        return new RumbleSearchQueryHandlerFactory();
    }

    @Override
    public String getUrl(String searchString, List<String> contentFilters, String sortFilter) throws ParsingException {
        return null; // TODO
    }

    @Override
    public String[] getAvailableContentFilter() {
        return null; // TODO
    }
}
