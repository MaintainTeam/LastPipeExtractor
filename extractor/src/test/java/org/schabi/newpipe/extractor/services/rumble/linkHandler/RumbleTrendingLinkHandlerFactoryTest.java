package org.schabi.newpipe.extractor.services.rumble.linkHandler;

import org.junit.BeforeClass;
import org.junit.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandlerFactory;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.schabi.newpipe.extractor.ServiceList.Rumble;

/**
 * Test for {@link RumbleTrendingLinkHandlerFactory}
 */

public class RumbleTrendingLinkHandlerFactoryTest {
    private static LinkHandlerFactory linkHandlerFactory;

    private final static String LIVE_KEY = "https://rumble.com/live-videos";
    private final static String EDITOR_PICKS_KEY = "https://rumble.com/editor-picks";


    private static Map<String,String> expectedTrendingUrl2IdMap = new HashMap()
    {{
        put(LIVE_KEY,                                 "Live");
        put(EDITOR_PICKS_KEY,                         "Editor Picks");
        put("https://rumble.com/category/news",     "News");
        put("https://rumble.com/category/viral",    "Viral");
        put("https://rumble.com/category/podcasts", "Podcasts");
        put("https://rumble.com/battle-leaderboard","Today's Battle Leaderboard Top 50");
        put("https://rumble.com/category/sports",   "Sports");
    }};

    @BeforeClass
    public static void setUp() throws Exception {
        // get the one and only instance. All 'trending' thingies use the same.
        linkHandlerFactory = Rumble.getKioskList().getListLinkHandlerFactoryByType(expectedTrendingUrl2IdMap.get(LIVE_KEY));
        NewPipe.init(DownloaderTestImpl.getInstance());
    }

    @Test
    public void getUrl() throws Exception {
        // assume EDITOR_PICKS_KEY is set as DEFAULT_TRENDING in RumbleTrendingLinkHandlerFactory
        assertEquals(EDITOR_PICKS_KEY, linkHandlerFactory.fromId("").getUrl());
    }

    @Test
    public void getId() throws Exception {

        for (Map.Entry<String, String> entry : expectedTrendingUrl2IdMap.entrySet()) {
            assertEquals(linkHandlerFactory.fromUrl(entry.getKey()).getId(), entry.getValue());
            assertThrows(ParsingException.class, () -> linkHandlerFactory.fromUrl(entry.getValue()).getId());
        }
    }

    @Test
    public void acceptUrl() throws ParsingException {

        for (Map.Entry<String, String> entry : expectedTrendingUrl2IdMap.entrySet()) {
            assertTrue(linkHandlerFactory.acceptUrl(entry.getKey()));
            assertFalse(linkHandlerFactory.acceptUrl(entry.getValue()));
        }

        assertFalse(linkHandlerFactory.acceptUrl("https://famous.com/feed/trending"));
        assertFalse(linkHandlerFactory.acceptUrl("nope"));
        assertFalse(linkHandlerFactory.acceptUrl("https://www.rumble.com/take/abc"));
        assertFalse(linkHandlerFactory.acceptUrl("    rumble.com/trending"));
        assertFalse(linkHandlerFactory.acceptUrl(""));
    }
}
