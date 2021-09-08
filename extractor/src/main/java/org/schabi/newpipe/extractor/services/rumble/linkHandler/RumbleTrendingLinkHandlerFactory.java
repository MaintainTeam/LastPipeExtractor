package org.schabi.newpipe.extractor.services.rumble.linkHandler;

import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RumbleTrendingLinkHandlerFactory extends ListLinkHandlerFactory {

    public static final String LIVE = "Live";
    public static final String EDITOR_PICKS = "Editor Picks";
    public static final String NEWS = "News";
    public static final String VIRAL = "Viral";
    public static final String PODCASTS = "Podcasts";
    public static final String TODAYS_BATTLE_LEADERBOARD_TOP_50 = "Today's Battle Leaderboard Top 50";
    public static final String SPORTS = "Sports";

    public static final String DEFAULT_TRENDING = TODAYS_BATTLE_LEADERBOARD_TOP_50;

    final private List<String> trendingIdList = new LinkedList<>();
    final private Map<String, String> trendingId2UrlMap = new HashMap();
    final private Map<String,String> trendingUrl2IdMap = new HashMap();

    private RumbleTrendingLinkHandlerFactory() {
        trendingUrl2IdMap.put("https://rumble.com/live-videos", LIVE);
        trendingUrl2IdMap.put("https://rumble.com/editor-picks", EDITOR_PICKS);
        trendingUrl2IdMap.put("https://rumble.com/category/news", NEWS);
        trendingUrl2IdMap.put("https://rumble.com/category/viral", VIRAL);
        trendingUrl2IdMap.put("https://rumble.com/category/podcasts", PODCASTS);
        trendingUrl2IdMap.put("https://rumble.com/battle-leaderboard", TODAYS_BATTLE_LEADERBOARD_TOP_50);
        trendingUrl2IdMap.put("https://rumble.com/category/sports", SPORTS);

        for(Map.Entry<String, String> entry : trendingUrl2IdMap.entrySet()){
            trendingId2UrlMap.put(entry.getValue(), entry.getKey());
            trendingIdList.add(entry.getValue());
        }
    }

    public static RumbleTrendingLinkHandlerFactory instance = new RumbleTrendingLinkHandlerFactory();

    public static RumbleTrendingLinkHandlerFactory getInstance() {
        return instance;
    }

    public List<String> getTrendingKioskIdsList() {
        return trendingIdList;
    }

    @Override
    public String getUrl(String id, List<String> contentFilters, String sortFilter) {
        if ("".equals(id)) {
            return trendingId2UrlMap.get(DEFAULT_TRENDING);
        }
        return trendingId2UrlMap.get(id);
    }

    @Override
    public String getId(String url) {
        return trendingUrl2IdMap.get(url);
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        if (trendingUrl2IdMap.containsKey(url))
            return true;
        return false;
    }
}
