package org.schabi.newpipe.extractor.services.bitchute.extractor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.ExtractorAsserts;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.services.BaseChannelExtractorTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.schabi.newpipe.extractor.ExtractorAsserts.assertIsSecureUrl;
import static org.schabi.newpipe.extractor.ServiceList.Bitchute;
import static org.schabi.newpipe.extractor.services.DefaultTests.*;

/**
 * Test for {@link BitchuteChannelExtractor}
 */
public class BitchuteChannelExtractorTest {

    public static class TestMarkDiceChannel extends TestChannel {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(keysForTestDataMap.channelUrl, "https://www.bitchute.com/channel/u3QMwGD7bSW6");
                put(keysForTestDataMap.expectedOriginalUrl, "https://www.bitchute.com/channel/u3QMwGD7bSW6");
                put(keysForTestDataMap.expectedUrl, "https://www.bitchute.com/channel/u3QMwGD7bSW6");
                put(keysForTestDataMap.expectedChannelName, "Mark Dice");
                put(keysForTestDataMap.expectedId, "u3QMwGD7bSW6");
                put(keysForTestDataMap.expectedMinSubscriberCount, "70000");
                put(keysForTestDataMap.expectedDescription, "Mark Dice - Exposing liberal lunatics, celebrity scum, mainstream media manipulation, and social justice warrior psychos. BEST CONSERVATIVE CHANNEL ON BITCHUTE.");
                put(keysForTestDataMap.expectedAvatarUrl, "ePTmphLaTzTvJgILYVieZtEv_small.jpg");
                put(keysForTestDataMap.expectedBannerlUrl, "ePTmphLaTzTvJgILYVieZtEv_small.jpg");
                put(keysForTestDataMap.doTestMoreRelatedItems, "true");
            }};
            TestChannel.setUp();
        }
    }

    public static class TestMissionCommanderChannel extends TestChannel {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(keysForTestDataMap.channelUrl, "https://www.bitchute.com/channel/missoncommander/");
                put(keysForTestDataMap.expectedOriginalUrl, "https://www.bitchute.com/channel/missoncommander/");
                put(keysForTestDataMap.expectedUrl, "https://www.bitchute.com/channel/missoncommander");
                put(keysForTestDataMap.expectedChannelName, "Dr. Jeffrey Horelick");
                put(keysForTestDataMap.expectedId, "missoncommander");
                put(keysForTestDataMap.expectedMinSubscriberCount, "400");
                // 20210414 at the writing of this test the channel has no description added
                put(keysForTestDataMap.expectedDescription, "");
                put(keysForTestDataMap.expectedAvatarUrl, "https://www.bitchute.com/static/v130/images/blank_small.png");
                put(keysForTestDataMap.expectedBannerlUrl, "https://www.bitchute.com/static/v130/images/blank_small.png");
                // 20210414 at the writing of this test the channel has not more than one page videos are added
                put(keysForTestDataMap.doTestMoreRelatedItems, "false");
            }};
            TestChannel.setUp();
        }
    }

    public static abstract class TestChannel implements BaseChannelExtractorTest {
        public enum keysForTestDataMap {
            channelUrl,
            expectedOriginalUrl,
            expectedUrl,
            expectedChannelName,
            expectedId,
            expectedMinSubscriberCount,
            expectedDescription,
            expectedAvatarUrl,
            expectedBannerlUrl,
            doTestMoreRelatedItems
        }

        private static BitchuteChannelExtractor extractor;
        protected static Map<keysForTestDataMap,String> testDataMap;

        public static void setUp () throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = (BitchuteChannelExtractor) Bitchute
                    .getChannelExtractor(testDataMap.get(keysForTestDataMap.channelUrl));
            extractor.fetchPage();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // Extractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId () {
            assertEquals(Bitchute.getServiceId(), extractor.getServiceId());
        }

        @Test
        public void testName () throws Exception {
            assertEquals(testDataMap.get(keysForTestDataMap.expectedChannelName), extractor.getName());
        }

        @Test
        public void testId () throws Exception {
            assertEquals(testDataMap.get(keysForTestDataMap.expectedId), extractor.getId());
        }

        @Test
        public void testUrl () throws ParsingException {
            assertEquals(testDataMap.get(keysForTestDataMap.expectedUrl), extractor.getUrl());
        }

        @Test
        public void testOriginalUrl () throws ParsingException {
            assertEquals(testDataMap.get(keysForTestDataMap.expectedOriginalUrl), extractor.getOriginalUrl());
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ListExtractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testRelatedItems () throws Exception {
            defaultTestRelatedItems(extractor);
        }

        @Test
        public void testMoreRelatedItems () throws Exception {
            if (Boolean.parseBoolean(testDataMap.get(keysForTestDataMap.doTestMoreRelatedItems)))
                defaultTestMoreItems(extractor);
            else
                assertTrue(true);
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ChannelExtractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription () throws Exception {
            ExtractorAsserts.assertContains(testDataMap.get(keysForTestDataMap.expectedDescription), extractor.getDescription());
        }

        @Test
        public void testAvatarUrl () throws Exception {
            String avatarUrl = extractor.getAvatarUrl();
            assertIsSecureUrl(avatarUrl);
            assertTrue(avatarUrl.contains(testDataMap.get(keysForTestDataMap.expectedAvatarUrl)), avatarUrl);
        }

        @Test
        public void testBannerUrl () throws Exception {
            String bannerUrl = extractor.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl.contains(testDataMap.get(keysForTestDataMap.expectedBannerlUrl)), bannerUrl);
        }

        @Test
        public void testFeedUrl () throws Exception {
            assertNull(extractor.getFeedUrl());
        }

        @Test
        public void testSubscriberCount () throws Exception {
            long subscriberCount = Long.parseLong(testDataMap.get(keysForTestDataMap.expectedMinSubscriberCount));
            assertTrue(extractor.getSubscriberCount() >= subscriberCount, "Wrong subscriber count");
        }

        @Override
        public void testVerified () throws Exception {
            assertFalse(extractor.isVerified());
        }
    }
}