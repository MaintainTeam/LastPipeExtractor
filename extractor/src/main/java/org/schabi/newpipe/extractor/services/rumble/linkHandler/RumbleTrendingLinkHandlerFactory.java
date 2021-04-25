package org.schabi.newpipe.extractor.services.rumble.linkHandler;

import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;

import java.util.List;

public class RumbleTrendingLinkHandlerFactory extends ListLinkHandlerFactory {

    private RumbleTrendingLinkHandlerFactory() {
        // TODO
    }

    public static RumbleTrendingLinkHandlerFactory instance = new RumbleTrendingLinkHandlerFactory();

    public static RumbleTrendingLinkHandlerFactory getInstance() {
        return instance;
    }

    public List<String> getTrendingKioskIdsList() {
        return null; // TODO
    }

    public String getUrl(String id, List<String> contentFilters, String sortFilter) {
        return ""; // TODO
    }

    @Override
    public String getId(String url) {
        return ""; // TODO
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        return false; // TODO
    }
}
