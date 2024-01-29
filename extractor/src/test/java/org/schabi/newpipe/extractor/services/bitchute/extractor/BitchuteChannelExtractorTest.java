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
import static org.schabi.newpipe.extractor.ServiceList.Bitchute;

/**
 * Test for {@link BitchuteChannelExtractor}
 */
public class BitchuteChannelExtractorTest {

    public abstract static class TestMarkDiceChannel extends TestChannel {
        @SuppressWarnings("checkstyle:LineLength")
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(KeysForTestDataMap.channelUrl, "https://www.bitchute.com/channel/u3QMwGD7bSW6");
                put(KeysForTestDataMap.expectedOriginalUrl, "https://www.bitchute.com/channel/u3QMwGD7bSW6");
                put(KeysForTestDataMap.expectedUrl, "https://www.bitchute.com/channel/u3QMwGD7bSW6");
                put(KeysForTestDataMap.expectedChannelName, "Mark Dice");
                put(KeysForTestDataMap.expectedId, "u3QMwGD7bSW6");
                put(KeysForTestDataMap.expectedMinSubscriberCount, "70000");
                put(KeysForTestDataMap.expectedDescription, "Mark Dice - Exposing liberal lunatics, celebrity scum, mainstream media manipulation, and social justice warrior psychos. BEST CONSERVATIVE CHANNEL ON BITCHUTE.");
                put(KeysForTestDataMap.expectedAvatarUrl, "ePTmphLaTzTvJgILYVieZtEv_small.jpg");
                put(KeysForTestDataMap.expectedBannerlUrl, "ePTmphLaTzTvJgILYVieZtEv_small.jpg");
                put(KeysForTestDataMap.doTestMoreRelatedItems, "true");
            }};
            TestChannel.setUp();
        }
    }

    public abstract static class TestMissionCommanderChannel extends TestChannel {
        @BeforeAll
        @SuppressWarnings("checkstyle:LineLength")
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(KeysForTestDataMap.channelUrl, "https://www.bitchute.com/channel/missoncommander/");
                put(KeysForTestDataMap.expectedOriginalUrl, "https://www.bitchute.com/channel/missoncommander/");
                put(KeysForTestDataMap.expectedUrl, "https://www.bitchute.com/channel/missoncommander");
                put(KeysForTestDataMap.expectedChannelName, "Dr. Jeffrey Horelick");
                put(KeysForTestDataMap.expectedId, "missoncommander");
                put(KeysForTestDataMap.expectedMinSubscriberCount, "400");
                // 20210414 at the writing of this test the channel has no description added
                put(KeysForTestDataMap.expectedDescription, "");
                put(KeysForTestDataMap.expectedAvatarUrl, "2fqQXi6GCbgyrXaPQ540PLNu_small.jpg");
                put(KeysForTestDataMap.expectedBannerlUrl, "2fqQXi6GCbgyrXaPQ540PLNu_small.jpg");
                // 20210414 at the writing of this test the channel has not more than one page videos are added
                put(KeysForTestDataMap.doTestMoreRelatedItems, "false");
            }};
            TestChannel.setUp();
        }
    }

    public abstract static class TestChannel implements BaseChannelExtractorTest {
        public enum KeysForTestDataMap {
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
        protected static Map<KeysForTestDataMap, String> testDataMap;

        public static void setUp() throws Exception {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = (BitchuteChannelExtractor) Bitchute
                    .getChannelExtractor(testDataMap.get(KeysForTestDataMap.channelUrl));
            extractor.fetchPage();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // Extractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId() {
            assertEquals(Bitchute.getServiceId(), extractor.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            assertEquals(testDataMap.get(KeysForTestDataMap.expectedChannelName),
                    extractor.getName());
        }

        @Test
        public void testId() throws Exception {
            assertEquals(testDataMap.get(KeysForTestDataMap.expectedId), extractor.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals(testDataMap.get(KeysForTestDataMap.expectedUrl), extractor.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals(testDataMap.get(KeysForTestDataMap.expectedOriginalUrl),
                    extractor.getOriginalUrl());
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ListExtractor
        //////////////////////////////////////////////////////////////////////////*/

        //@Test
        //public void testRelatedItems() throws Exception {
        //    defaultTestRelatedItems(extractor);
        //}

        //@Test
        //public void testMoreRelatedItems() throws Exception {
        //    if (Boolean.parseBoolean(testDataMap
        //          .get(KeysForTestDataMap.doTestMoreRelatedItems))) {
        //        defaultTestMoreItems(extractor);
        //    } else {
        //        assertTrue(true);
        //    }
        //}

        /*//////////////////////////////////////////////////////////////////////////
        // ChannelExtractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription() throws Exception {
            ExtractorAsserts.assertContains(testDataMap.get(KeysForTestDataMap.expectedDescription),
                    extractor.getDescription());
        }

        //@Test
        //public void testAvatarUrl() throws Exception {
        //    final String avatarUrl = extractor.getAvatarUrl();
        //    assertIsSecureUrl(avatarUrl);
        //    assertTrue(avatarUrl.contains(testDataMap.get(KeysForTestDataMap.expectedAvatarUrl)),
        //            avatarUrl);
        //}

        //@Test
        //public void testBannerUrl() throws Exception {
        //    final String bannerUrl = extractor.getBannerUrl();
        //    assertIsSecureUrl(bannerUrl);
        //    assertTrue(bannerUrl.contains(testDataMap.get(KeysForTestDataMap.expectedBannerlUrl)),
        //            bannerUrl);
        //}

        @Test
        public void testFeedUrl() throws Exception {
            assertNull(extractor.getFeedUrl());
        }

        @Test
        public void testSubscriberCount() throws Exception {
            final long subscriberCount =
                    Long.parseLong(testDataMap.get(KeysForTestDataMap.expectedMinSubscriberCount));
            assertTrue(extractor.getSubscriberCount() >= subscriberCount, "Wrong subscriber count");
        }

        @Override
        public void testVerified() throws Exception {
            assertFalse(extractor.isVerified());
        }
    }
}
