package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.services.BaseListExtractorTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.schabi.newpipe.extractor.ServiceList.Rumble;
import static org.schabi.newpipe.extractor.services.DefaultTests.assertNoMoreItems;
import static org.schabi.newpipe.extractor.services.DefaultTests.defaultTestMoreItems;
import static org.schabi.newpipe.extractor.services.DefaultTests.defaultTestRelatedItems;

/**
 * Tests for {@link RumbleTrendingExtractor}
 *
 * We have atm 7 different Kiosk categories:
 *
 * - Live
 * - Editor Picks
 * - News
 * - Viral
 * - Podcasts
 * - Today's Battle Leaderboard Top 50
 * - Sports
 *
 *  Here we have a base class called Trending that implements testing for all of those categories.
 *  For every category we have one derived test class.
 */
public class RumbleTrendingExtractorTest {

    public static class Live extends Trending {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(keysForTestDataMap.name, "Live");
                put(keysForTestDataMap.id, "Live");
                put(keysForTestDataMap.url, Rumble.getBaseUrl()+ "/live-videos");
                put(keysForTestDataMap.originalUrl, Rumble.getBaseUrl() + "/live-videos");
            }};
            Trending.setUp();
        }
    }

    public static class EditorPicks extends Trending {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(keysForTestDataMap.name, "Editor Picks");
                put(keysForTestDataMap.id, "Editor Picks");
                put(keysForTestDataMap.url, Rumble.getBaseUrl()+ "/editor-picks");
                put(keysForTestDataMap.originalUrl, Rumble.getBaseUrl() + "/editor-picks");
            }};
            Trending.setUp();
        }
    }

    public static class News extends Trending {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(keysForTestDataMap.name, "News");
                put(keysForTestDataMap.id, "News");
                put(keysForTestDataMap.url, Rumble.getBaseUrl()+ "/category/news");
                put(keysForTestDataMap.originalUrl, Rumble.getBaseUrl() + "/category/news");
            }};
            Trending.setUp();
        }
    }

    public static class Viral extends Trending {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(keysForTestDataMap.name, "Viral");
                put(keysForTestDataMap.id, "Viral");
                put(keysForTestDataMap.url, Rumble.getBaseUrl()+ "/category/viral");
                put(keysForTestDataMap.originalUrl, Rumble.getBaseUrl() + "/category/viral");
            }};
            Trending.setUp();
        }
    }

    public static class Podcasts extends Trending {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(keysForTestDataMap.name, "Podcasts");
                put(keysForTestDataMap.id, "Podcasts");
                put(keysForTestDataMap.url, Rumble.getBaseUrl()+ "/category/podcasts");
                put(keysForTestDataMap.originalUrl, Rumble.getBaseUrl() + "/category/podcasts");
            }};
            Trending.setUp();
        }
    }

    public static class BattleLeaderboard extends Trending {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(keysForTestDataMap.name, "Today's Battle Leaderboard Top 50");
                put(keysForTestDataMap.id, "Today's Battle Leaderboard Top 50");
                put(keysForTestDataMap.url, Rumble.getBaseUrl()+ "/battle-leaderboard");
                put(keysForTestDataMap.originalUrl, Rumble.getBaseUrl() + "/battle-leaderboard");
            }};
            Trending.setUp();
        }

        @Override
        public void testMoreRelatedItems() throws Exception {
            assertNoMoreItems(extractor);

        }
    }

    public static class Sports extends Trending {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(keysForTestDataMap.name, "Sports");
                put(keysForTestDataMap.id, "Sports");
                put(keysForTestDataMap.url, Rumble.getBaseUrl()+ "/category/sports");
                put(keysForTestDataMap.originalUrl, Rumble.getBaseUrl() + "/category/sports");
            }};
            Trending.setUp();
        }
    }


    public static abstract class Trending implements BaseListExtractorTest {

        public enum keysForTestDataMap {
            name, id, url, originalUrl
        }
        protected static ListExtractor extractor;
        protected static Map<keysForTestDataMap,String> testDataMap;

        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());

            extractor = Rumble.getKioskList().getExtractorById(testDataMap.get(keysForTestDataMap.id), null);
            extractor.fetchPage();
        }

        @AfterAll
        public static void tearDown() throws IOException, ExtractionException {
            List<Throwable> errors = extractor.getInitialPage().getErrors();
            System.out.println(errors.toString());

        }

        /*//////////////////////////////////////////////////////////////////////////
        // Extractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId() {
            assertEquals(Rumble.getServiceId(), extractor.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            assertEquals(testDataMap.get(keysForTestDataMap.name), extractor.getName());
        }

        @Test
        public void testId() throws Exception {
            assertEquals(testDataMap.get(keysForTestDataMap.id), extractor.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            // TODO evermind figure out for what this URL really are
            //assertEquals(testDataMap.get(keysForTestDataMap.url), extractor.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            // TODO evermind figure out for what this URL really are
            //assertEquals(testDataMap.get(keysForTestDataMap.originalUrl), extractor.getOriginalUrl());
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ListExtractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testRelatedItems() throws Exception {
            defaultTestRelatedItems(extractor);
        }

        @Test
        public void testMoreRelatedItems() throws Exception {
            defaultTestMoreItems(extractor);
        }
    }
}
