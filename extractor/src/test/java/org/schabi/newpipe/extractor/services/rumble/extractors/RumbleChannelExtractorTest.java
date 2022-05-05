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
                put(KeysForTestDataMap.expectedAvatarUrl, "https://i.rmbl.ws/z8/7/o/2/c/7o2ca.baa-GovRonDeSantis-qrikr6.jpeg");
                put(KeysForTestDataMap.expectedBannerlUrl, "{\"hasBanner\":\""
                        + /* ===expected to have banner? ===*/
                        "true"
                        + "\",\"url\":\""
                        + /* ===set the expected url=== */
                        "https://i.rmbl.ws/z8/7/o/2/c/7o2ca.caa-GovRonDeSantis-qrikr9.jpeg"
                        + "\"}");
                put(KeysForTestDataMap.doTestMoreRelatedItems, "false");
                put(KeysForTestDataMap.mockPath, "channelTest");
            }};
            TestChannel.setUp();
            /** more info see: {@link RumbleSharedTests#infoItemsResultsTest} */
            someExpectedResults = new String[]{
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Ron DeSantis', textualUploadDate='2021-04-23T14:00:32-04:00', viewCount=3115, duration=1190, uploaderUrl='https://rumble.com/c/GovRonDeSantis', infoType=STREAM, serviceId=6, url='https://rumble.com/vfzsxr-governor-ron-desantis-strikes-historic-gaming-compact-with-seminole-tribe-o.html', name='Governor Ron DeSantis Strikes Historic Gaming Compact with Seminole Tribe of Florida 4/23/21', thumbnailUrl='https://i.rmbl.ws/s8/1/_/W/E/M/_WEMb.oq1b.2-small-Governor-Ron-DeSantis-Strik.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Ron DeSantis', textualUploadDate='2021-04-22T17:47:27-04:00', viewCount=2919, duration=361, uploaderUrl='https://rumble.com/c/GovRonDeSantis', infoType=STREAM, serviceId=6, url='https://rumble.com/vfykzv-governor-desantis-joins-dana-white-ahead-of-full-capacity-sold-out-fight-in.html', name='Governor DeSantis Joins Dana White Ahead of Full Capacity, Sold Out Fight in Jacksonville 4/22/21', thumbnailUrl='https://i.rmbl.ws/s8/1/l/3/q/M/l3qMb.oq1b.2-small-Governor-DeSantis-Joins-Dan.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Ron DeSantis', textualUploadDate='2021-04-22T16:37:33-04:00', viewCount=841, duration=714, uploaderUrl='https://rumble.com/c/GovRonDeSantis', infoType=STREAM, serviceId=6, url='https://rumble.com/vfyi3x-lake-worth-food-bank-press-conference-04222021.html', name='Governor Ron DeSantis Helps Announce a $1.6M Donation to Palm Beach County Food Bank by Blackstone 4/22/21', thumbnailUrl='https://i.rmbl.ws/s8/6/T/8/p/M/T8pMb.oq1b.1.jpg', uploaderVerified='false'}"
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
                put(KeysForTestDataMap.expectedAvatarUrl, "https://i.rmbl.ws/z8/U/K/d/b/UKdba.baa-Bongino-qgs0w4.jpg");
                put(KeysForTestDataMap.expectedBannerlUrl, "{\"hasBanner\":\""
                        + /* ===expected to have banner? ===*/
                        "true"
                        + "\",\"url\":\""
                        + /* ===set the expected url=== */
                        "https://i.rmbl.ws/z8/U/K/d/b/UKdba.caa-Bongino-qgs0w8.jpg"
                        + "\"}");
                put(KeysForTestDataMap.doTestMoreRelatedItems, "true");
                put(KeysForTestDataMap.mockPath, "channelTestMulitplePages");
            }};
            TestChannel.setUp();
            /** more info see: {@link RumbleSharedTests#infoItemsResultsTest} */
            someExpectedResults = new String[]{
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='The Dan Bongino Show', textualUploadDate='2021-03-25T11:34:06-04:00', viewCount=114099, duration=3518, uploaderUrl='https://rumble.com/c/Bongino', infoType=STREAM, serviceId=6, url='https://rumble.com/vf16e7-ep.-1485-bidens-latest-move-is-his-most-radical-yet-the-dan-bongino-show.html', name='Ep. 1485 Biden’s Latest Move Is His Most Radical Yet - The Dan Bongino Show', thumbnailUrl='https://i.rmbl.ws/s8/1/F/w/u/G/FwuGb.oq1b.2-small-Ep.-1485-Bidens-Latest-Move.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='The Dan Bongino Show', textualUploadDate='2021-03-24T12:19:40-04:00', viewCount=116424, duration=3588, uploaderUrl='https://rumble.com/c/Bongino', infoType=STREAM, serviceId=6, url='https://rumble.com/vezxir-title-ep.-1484-the-gun-grabbing-begins-the-dan-bongino-show.html', name='Ep. 1484 The Gun Grabbing Begins! - The Dan Bongino Show', thumbnailUrl='https://i.rmbl.ws/s8/1/Z/j/g/G/ZjgGb.oq1b.5-small-Title-Ep.-1484-The-Gun-Grab.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='The Dan Bongino Show', textualUploadDate='2021-03-23T12:19:32-04:00', viewCount=147034, duration=3670, uploaderUrl='https://rumble.com/c/Bongino', infoType=STREAM, serviceId=6, url='https://rumble.com/veyfx7-ep.-1483-tucker-takes-on-kristi-noem-in-must-see-video-the-dan-bongino-show.html', name='Ep. 1483 Tucker Takes On Kristi Noem In Must See Video - The Dan Bongino Show', thumbnailUrl='https://i.rmbl.ws/s8/1/B/m/1/F/Bm1Fb.oq1b.8-small-Ep.-1483-Tucker-Takes-On-Kr.jpg', uploaderVerified='false'}"
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
            TestChannel.setUp();
            /** more info see: {@link RumbleSharedTests#infoItemsResultsTest} */
            someExpectedResults = new String[]{
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Vlemx', textualUploadDate='2021-04-18T06:11:02-04:00', viewCount=8, duration=114, uploaderUrl='https://rumble.com/user/Vlemx', infoType=STREAM, serviceId=6, url='https://rumble.com/vfs2yl-weve-got-to-make-sure-that-they-know-we-mean-business.-rep.-maxine-waters.html', name='we’ve got to make sure that they know we mean business.” —Rep. Maxine Waters', thumbnailUrl='https://i.rmbl.ws/s8/6/9/Z/g/L/9ZgLb.oq1b.1.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Vlemx', textualUploadDate='2021-04-18T05:28:54-04:00', viewCount=9, duration=53, uploaderUrl='https://rumble.com/user/Vlemx', infoType=STREAM, serviceId=6, url='https://rumble.com/vfs22v-indigenous-americans-in-chicago-come-out-in-numbers-to-protest-the-death-of.html', name='Indigenous Americans In Chicago Come Out In Numbers To Protest The Death Of Adam Toledo', thumbnailUrl='https://i.rmbl.ws/s8/6/h/I/g/L/hIgLb.oq1b.1.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Vlemx', textualUploadDate='2021-04-18T00:02:15-04:00', viewCount=13, duration=65, uploaderUrl='https://rumble.com/user/Vlemx', infoType=STREAM, serviceId=6, url='https://rumble.com/vfrvln-brooklyn-center-mayor-absurdly-claims-police-do-not-needweapons-at-a-traffi.html', name='Brooklyn Center mayor absurdly claims police do not need weapons at a traffic stop.', thumbnailUrl='https://i.rmbl.ws/s8/6/7/E/e/L/7EeLb.oq1b.1.jpg', uploaderVerified='false'}"
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
            System.setProperty("downloader", "MOCK");
    //        System.setProperty("downloader", "RECORDING");
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
            assertTrue(avatarUrl.contains(testDataMap.get(KeysForTestDataMap.expectedAvatarUrl)), avatarUrl);
        }

        @Test
        public void testBannerUrl() throws Exception {

            final String bannerUrl = extractor.getBannerUrl();
            final JsonObject testData = (JsonObject) JsonParser.any().from(testDataMap.get(KeysForTestDataMap.expectedBannerlUrl));

            final Boolean hasBanner = testData.getBoolean("hasBanner");
            final String expectedBannerUrl = testData.getString("url");
            if (hasBanner) {
                assertIsSecureUrl(bannerUrl);
            }

            assertTrue(bannerUrl.contains(expectedBannerUrl), bannerUrl);
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
