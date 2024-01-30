package org.schabi.newpipe.extractor.utils;

import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.search.filter.FilterItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public final class BraveNewPipeExtractorUtils {
    private BraveNewPipeExtractorUtils() {
    }

    @Nonnull
    public static List<ListLinkHandler> generateTabsFromSuffixMap(
            final String baseUrl,
            final String id,
            final Map<FilterItem, String> tab2Suffix) {

        final List<ListLinkHandler> tabs = new ArrayList<>();

        for (final Map.Entry<FilterItem, String> tab : tab2Suffix.entrySet()) {
            final String url = baseUrl + tab.getValue();
            tabs.add(new ListLinkHandler(url, url, id, List.of(tab.getKey()), List.of()));
        }
        return tabs;
    }
}
