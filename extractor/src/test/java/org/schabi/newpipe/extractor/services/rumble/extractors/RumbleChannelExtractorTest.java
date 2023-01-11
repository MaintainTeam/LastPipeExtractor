package org.schabi.newpipe.extractor.services.rumble.extractors;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderFactory;
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
import static org.schabi.newpipe.downloader.DownloaderFactory.RESOURCE_PATH;
import static org.schabi.newpipe.extractor.ExtractorAsserts.assertIsSecureUrl;
import static org.schabi.newpipe.extractor.ServiceList.Rumble;
import static org.schabi.newpipe.extractor.services.DefaultTests.defaultTestMoreItems;
import static org.schabi.newpipe.extractor.services.DefaultTests.defaultTestRelatedItems;
import static org.schabi.newpipe.extractor.services.rumble.extractors.RumbleSharedTests.infoItemsResultsTest;

@SuppressWarnings({"checkstyle:LineLength", "checkstyle:MethodName", "checkstyle:InvalidJavadocPosition"})
/**
 * Test for {@link RumbleChannelExtractor}
 */
public class RumbleChannelExtractorTest {

    public static class SinglePageChannel extends TestChannel {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(KeysForTestDataMap.channelUrl, "https://rumble.com/c/GovRonDeSantis");
                put(KeysForTestDataMap.expectedOriginalUrl, "https://rumble.com/c/GovRonDeSantis");
                put(KeysForTestDataMap.expectedUrl, "https://rumble.com/c/GovRonDeSantis");
                put(KeysForTestDataMap.expectedChannelName, "Ron DeSantis");
                put(KeysForTestDataMap.expectedId, "c/GovRonDeSantis");
                put(KeysForTestDataMap.expectedMinSubscriberCount, "52000");
                put(KeysForTestDataMap.expectedDescription, "");
                put(KeysForTestDataMap.expectedAvatarUrl,
                        "https://sp.rmbl.ws/z8/7/o/2/c/7o2ca.baa-GovRonDeSantis-qrikr6.jpeg");
                put(KeysForTestDataMap.expectedBannerlUrl, "{\"hasBanner\":\""
                        + /* ===expected to have banner? ===*/
                        "true"
                        + "\",\"url\":\""
                        + /* ===set the expected url=== */
                        "https://sp.rmbl.ws/z8/7/o/2/c/7o2ca.caa-GovRonDeSantis-qrikr9.jpeg"
                        + "\"}");
                put(KeysForTestDataMap.doTestMoreRelatedItems, "false");
                put(KeysForTestDataMap.mockPath, "channelTest");
            }};
            System.setProperty("downloader", "MOCK");
            // System.setProperty("downloader", "RECORDING");
            TestChannel.setUp();
            /** more info see: {@link RumbleSharedTests#infoItemsResultsTest} */
            someExpectedResults = new String[]{
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Ron DeSantis', textualUploadDate='2023-01-10T10:13:51-04:00', viewCount=4710, duration=2630, uploaderUrl='https://rumble.com/c/GovRonDeSantis', infoType=STREAM, serviceId=6, url='https://rumble.com/v24qrsw-gov.-desantis-unveils-the-future-of-conservation-in-florida.html', name='Gov. DeSantis Unveils the Future of Conservation in Florida', thumbnailUrl='https://sp.rmbl.ws/s8/6/a/T/T/R/aTTRh.oq1b.1.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Ron DeSantis', textualUploadDate='2023-01-03T16:42:41-04:00', viewCount=3230, duration=3319, uploaderUrl='https://rumble.com/c/GovRonDeSantis', infoType=STREAM, serviceId=6, url='https://rumble.com/v23r6yw-governor-ron-desantis-second-inaugural-address.html', name='Governor Ron DeSantis' Second Inaugural Address', thumbnailUrl='https://sp.rmbl.ws/s8/6/i/C/y/L/iCyLh.oq1b.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Ron DeSantis', textualUploadDate='2022-12-15T10:55:14-04:00', viewCount=4200, duration=1308, uploaderUrl='https://rumble.com/c/GovRonDeSantis', infoType=STREAM, serviceId=6, url='https://rumble.com/v20scmg-gov.-desantis-signs-toll-relief-legislation.html', name='Gov. DeSantis Signs Toll Relief Legislation', thumbnailUrl='https://sp.rmbl.ws/s8/6/O/B/x/s/OBxsh.oq1b.1.jpg', uploaderVerified='false'}"
            };
        }
    }

    public static class MultiplePageChannel extends TestChannel {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(KeysForTestDataMap.channelUrl, "https://rumble.com/c/Bongino");
                put(KeysForTestDataMap.expectedOriginalUrl, "https://rumble.com/c/Bongino");
                put(KeysForTestDataMap.expectedUrl, "https://rumble.com/c/Bongino");
                put(KeysForTestDataMap.expectedChannelName, "The Dan Bongino Show");
                put(KeysForTestDataMap.expectedId, "c/Bongino");
                put(KeysForTestDataMap.expectedMinSubscriberCount, "1500000");
                put(KeysForTestDataMap.expectedDescription, "");
                put(KeysForTestDataMap.expectedAvatarUrl, "https://sp.rmbl.ws/z8/U/K/d/b/UKdba.baa.1-Bongino-rj8lv1.jpeg");
                put(KeysForTestDataMap.expectedBannerlUrl, "{\"hasBanner\":\""
                        + /* ===expected to have banner? ===*/
                        "true"
                        + "\",\"url\":\""
                        + /* ===set the expected url=== */
                        "https://sp.rmbl.ws/z8/U/K/d/b/UKdba.caa.3-Bongino-rjp4dn.jpeg"
                        + "\"}");
                put(KeysForTestDataMap.doTestMoreRelatedItems, "true");
                put(KeysForTestDataMap.mockPath, "channelTestMulitplePages");
            }};
            System.setProperty("downloader", "MOCK");
            // System.setProperty("downloader", "RECORDING");
            TestChannel.setUp();
            /** more info see: {@link RumbleSharedTests#infoItemsResultsTest} */
            someExpectedResults = new String[]{
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='The Dan Bongino Show', textualUploadDate='2022-11-28T11:37:21-04:00', viewCount=235000, duration=3324, uploaderUrl='https://rumble.com/c/Bongino', infoType=STREAM, serviceId=6, url='https://rumble.com/v1xnqmk-famed-investor-has-a-frightening-prediction-ep.-1902-the-dan-bongino-show.html', name='Famed Investor Has A Frightening Prediction (Ep. 1902) -The Dan Bongino Show', thumbnailUrl='https://sp.rmbl.ws/s8/1/m/U/u/-/mUu-g.oq1b.2-small-Famed-Investor-Has-A-Fright.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='The Dan Bongino Show', textualUploadDate='2022-11-27T10:30:00-04:00', viewCount=51400, duration=3658, uploaderUrl='https://rumble.com/c/Bongino', infoType=STREAM, serviceId=6, url='https://rumble.com/v1woxvo-sunday-special-w-pete-hegseth-joey-jones-dave-rubin-and-tyrus-the-dan-bongi.html', name='SUNDAY SPECIAL w/ Pete Hegseth, Joey Jones, Dave Rubin and Tyrus - The Dan Bongino Show', thumbnailUrl='https://sp.rmbl.ws/s8/1/e/w/i/4/ewi4g.oq1b.2-small-SUNDAY-SPECIAL-w-Pete-Hegse.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='The Dan Bongino Show', textualUploadDate='2022-11-25T10:30:00-04:00', viewCount=45900, duration=3214, uploaderUrl='https://rumble.com/c/Bongino', infoType=STREAM, serviceId=6, url='https://rumble.com/v1wqjaw-holiday-special-best-of-the-dan-bongino-show.html', name='HOLIDAY SPECIAL: Best of The Dan Bongino Show', thumbnailUrl='https://sp.rmbl.ws/s8/1/4/G/A/4/4GA4g.oq1b.2-small-HOLIDAY-SPECIAL-Best-of-The.jpg', uploaderVerified='false'}"
            };
        }
    }
    public static class MultiplePageUser extends TestChannel {
        @BeforeAll
        public static void setUp() throws Exception {
            testDataMap = new HashMap() {{
                put(KeysForTestDataMap.channelUrl, "https://rumble.com/user/Vlemx");
                put(KeysForTestDataMap.expectedOriginalUrl, "https://rumble.com/user/Vlemx");
                put(KeysForTestDataMap.expectedUrl, "https://rumble.com/user/Vlemx");
                put(KeysForTestDataMap.expectedChannelName, "Vlemx");
                put(KeysForTestDataMap.expectedId, "user/Vlemx");
                put(KeysForTestDataMap.expectedMinSubscriberCount, "915");
                put(KeysForTestDataMap.expectedDescription, "");
                put(KeysForTestDataMap.expectedAvatarUrl, "https://graph.facebook.com/10153436058551636/picture?type=large");
                put(KeysForTestDataMap.expectedBannerlUrl, "{\"hasBanner\":\"false\",\"url\":\"\"}");
                put(KeysForTestDataMap.doTestMoreRelatedItems, "true");
                put(KeysForTestDataMap.mockPath, "userTestMulitplePages");
            }};
            System.setProperty("downloader", "MOCK");
            //System.setProperty("downloader", "RECORDING");
            TestChannel.setUp();
            /** more info see: {@link RumbleSharedTests#infoItemsResultsTest} */
            someExpectedResults = new String[]{
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Vlemx', textualUploadDate='2022-09-20T13:38:22-04:00', viewCount=-1, duration=107, uploaderUrl='https://rumble.com/user/Vlemx', infoType=STREAM, serviceId=6, url='https://rumble.com/v1ku4ih-don-lemon-was-not-ready.html', name='Don Lemon was NOT ready', thumbnailUrl='https://sp.rmbl.ws/s8/6/z/P/k/S/zPkSf.oq1b.1.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Vlemx', textualUploadDate='2022-09-06T20:49:42-04:00', viewCount=16, duration=3, uploaderUrl='https://rumble.com/user/Vlemx', infoType=STREAM, serviceId=6, url='https://rumble.com/v1izhpm-no-no-no.html', name='no no no', thumbnailUrl='https://sp.rmbl.ws/s8/1/Q/P/t/G/QPtGf.oq1b.2-small-no-no-no.jpg', uploaderVerified='false'}"
            };
        }
    }

    public abstract static class TestChannel implements BaseChannelExtractorTest {

        protected static final String MOCK_PATH =
                RESOURCE_PATH + "/services/rumble/extractor/channel/";
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
            doTestMoreRelatedItems,
            mockPath
        }

        protected static RumbleChannelExtractor extractor;
        protected static Map<KeysForTestDataMap, String> testDataMap;
        /** more info see: {@link RumbleSharedTests#infoItemsResultsTest} */
        protected static String[] someExpectedResults = null;

        public static void setUp() throws Exception {
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/"
                    + testDataMap.get(KeysForTestDataMap.mockPath)));
            extractor = (RumbleChannelExtractor) Rumble
                    .getChannelExtractor(testDataMap.get(KeysForTestDataMap.channelUrl));
            extractor.fetchPage();
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
            assertEquals(testDataMap.get(KeysForTestDataMap.expectedChannelName), extractor.getName());
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
            assertEquals(testDataMap.get(KeysForTestDataMap.expectedOriginalUrl), extractor.getOriginalUrl());
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ListExtractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testRelatedItems() throws Exception {
            defaultTestRelatedItems(extractor);

            infoItemsResultsTest(extractor.getService(),
                    extractor.getInitialPage().getItems(),
                    extractor.getInitialPage().getErrors(),
                    someExpectedResults
            );
        }

        @Test
        public void testMoreRelatedItems() throws Exception {
            if (Boolean.parseBoolean(testDataMap.get(KeysForTestDataMap.doTestMoreRelatedItems))) {
                defaultTestMoreItems(extractor);
            } else {
                assertTrue(true);
            }
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ChannelExtractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription() throws Exception {
            ExtractorAsserts.assertContains(testDataMap.get(KeysForTestDataMap.expectedDescription), extractor.getDescription());
        }

        @Test
        public void testAvatarUrl() throws Exception {
            final String avatarUrl = extractor.getAvatarUrl();
            assertIsSecureUrl(avatarUrl);
            assertEquals(testDataMap.get(KeysForTestDataMap.expectedAvatarUrl), avatarUrl);
        }

        @Test
        public void testBannerUrl() throws Exception {

            final String bannerUrl = extractor.getBannerUrl();
            final JsonObject testData = (JsonObject) JsonParser.any().from(testDataMap.get(KeysForTestDataMap.expectedBannerlUrl));

            final boolean hasBanner = testData.getBoolean("hasBanner");
            final String expectedBannerUrl = testData.getString("url");
            if (hasBanner) {
                assertIsSecureUrl(bannerUrl);
                assertEquals(expectedBannerUrl, bannerUrl);
            } else {
                assertEquals(null, bannerUrl);
            }
        }

        @Test
        public void testFeedUrl() throws Exception {
            assertNull(extractor.getFeedUrl());
        }

        @Test
        public void testSubscriberCount() throws Exception {
            final long subscriberCount = Long.parseLong(testDataMap.get(KeysForTestDataMap.expectedMinSubscriberCount));
            assertTrue(extractor.getSubscriberCount() >= subscriberCount, "Wrong subscriber count");
        }

        @Override
        public void testVerified() throws Exception {
            assertFalse(extractor.isVerified());
        }
    }
}
