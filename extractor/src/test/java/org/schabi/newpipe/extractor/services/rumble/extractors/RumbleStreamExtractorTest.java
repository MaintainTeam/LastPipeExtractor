package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderFactory;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.services.DefaultStreamExtractorTest;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;
import org.schabi.newpipe.extractor.stream.StreamType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.schabi.newpipe.downloader.DownloaderFactory.RESOURCE_PATH;
import static org.schabi.newpipe.extractor.ExtractorAsserts.assertIsSecureUrl;
import static org.schabi.newpipe.extractor.ServiceList.Rumble;

@SuppressWarnings({"checkstyle:LineLength", "checkstyle:InvalidJavadocPosition"})
public class RumbleStreamExtractorTest {

    /*
     * This stream test has one speciality:
     * - onw related Stream is the RSBN Live-Stream. So here we also testing
     *   if the detection of a live stream in the the related streams works
     *
     * -> Hopyfully RSBN has always a live strem in the related section
     *    We will see when we have to update this test case.
     */
    public static class NormalStreamExtractorTest extends DefaultStreamExtractorTest {

        protected static final String MOCK_PATH =
                RESOURCE_PATH + "/services/rumble/extractor/stream/";

        protected static RumbleStreamExtractor extractor;
        protected static StreamingService expectedService = Rumble;

        protected static String url = "https://rumble.com/v1a992g";
        protected static String expectedUrl = "https://rumble.com/v1a992g";
        protected static String expectedName = "The Truth Behind Arizona’s Paper Ballots; Jovan Pulitzer’s BOMBSHELL Paper Analysis Report 6/27/22";
        protected static String expectedId = "v1a992g";
        protected static String expectedDesc = "The evidence continues to reveal itself regarding the results of the 2020";
        protected static String expectedCategory = "";
        protected static int expectedAgeLimit = 0;
        protected static long expectedViewCountAtLeast = 46658;
        protected static String expectedUploaderName = "Right Side Broadcasting Network";
        protected static String expectedUploadDate = "2022-06-28 05:36:31.000";
        protected static String expectedTextualUploadDate = "2022-06-28T05:36:31+00:00";
        protected static StreamExtractor.Privacy expectedPrivacy = StreamExtractor.Privacy.PUBLIC;
        protected static String expectedUploaderUrl = "https://rumble.com/c/RSBN";
        protected static String expectedSupportInfo = "";
        protected static boolean expectedHasAudioStreams = true;
        protected static boolean expectedHasVideoStreams = true;
        protected static String expectedArtistProfilePictureInfix = ".rumble.com/live/channel_images/"; // TODO
        protected static long expectedLength = 12948;

        @BeforeAll
        public static void setUp() throws ExtractionException, IOException {
            System.setProperty("downloader", "MOCK");
            //System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/streamExtractor"));

            extractor = (RumbleStreamExtractor) Rumble
                    .getStreamExtractor(url);
            extractor.fetchPage();
        }

        @Test
        public void testAvatarThumbnailPicture() throws Exception {
            final String avatarUrl = extractor().getUploaderAvatarUrl();
            assertTrue(!avatarUrl.isEmpty() || avatarUrl == null);
        }

        @Override
        public StreamExtractor extractor() {
            return extractor;
        }

        @Override
        public StreamingService expectedService() {
            return expectedService;
        }

        @Override
        public String expectedName() {
            return expectedName;
        }

        @Override
        public String expectedId() {
            return expectedId;
        }

        @Override
        public String expectedUrlContains() {
            return expectedUrl;
        }

        @Override
        public String expectedOriginalUrlContains() {
            return expectedUrl;
        }

        @Override
        public StreamType expectedStreamType() {
            return StreamType.VIDEO_STREAM;
        }

        @Override
        public String expectedUploaderName() {
            return expectedUploaderName;
        }

        @Override
        public String expectedUploaderUrl() {
            return expectedUploaderUrl;
        }

        @Override
        public List<String> expectedDescriptionContains() {
            return Collections.singletonList(expectedDesc);
        }

        @Override
        public long expectedLength() {
            return expectedLength;
        }

        @Override
        public long expectedViewCountAtLeast() {
            return expectedViewCountAtLeast;
        }

        @Override
        public String expectedUploadDate() {
            return expectedUploadDate;
        }

        @Override
        public String expectedTextualUploadDate() {
            return expectedTextualUploadDate;
        }

        @Override
        public long expectedLikeCountAtLeast() {
            return Long.MIN_VALUE;
        }

        @Override
        public long expectedDislikeCountAtLeast() {
            return Long.MIN_VALUE;
        }

        @Override
        public boolean expectedHasVideoStreams() {
            return expectedHasVideoStreams;
        }

        @Override
        public boolean expectedHasRelatedItems() {
            return true;
        }

        @Override
        public boolean expectedHasSubtitles() {
            return false;
        }

        @Override
        public boolean expectedHasFrames() {
            return false;
        }

        @Override
        public String expectedCategory() {
            return expectedCategory;
        }

        @Override
        public int expectedAgeLimit() {
            return expectedAgeLimit;
        }

        @Override
        public StreamExtractor.Privacy expectedPrivacy() {
            return expectedPrivacy;
        }

        @Override
        public String expectedSupportInfo() {
            return expectedSupportInfo;
        }

        @Override
        public boolean expectedHasAudioStreams() {
            return expectedHasAudioStreams;
        }

        /**
         *  Test for {@link RumbleStreamRelatedInfoItemExtractor}
         */
        @Test
        public void rumbleStreamRelatedInfoItemsExtractorTest()
                throws ExtractionException, IOException {
            final StreamInfoItemsCollector page = extractor.getRelatedItems();

            /** more info see: {@link RumbleSharedTests#infoItemsResultsTest} */
            final String[] someExpectedResults = {
                    /* here is the speciality LIVE_STREAM detection on releated streams */
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Right Side Broadcasting Network', textualUploadDate='null', viewCount=300000, duration=80633, uploaderUrl='https://rumble.com/user/RSBN', infoType=STREAM, serviceId=6, url='https://rumble.com/v23xrx6-live-day-three-118th-congress-vote-on-speaker-of-the-house-152023.html', name='LIVE: Day Three - 118th Congress Vote on Speaker of the House - 1/5/2023', thumbnailUrl='https://sp.rmbl.ws/s8/1/A/A/J/M/AAJMh.0kob-small-LIVE-Day-Three-118th-Congre.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Power Slap', textualUploadDate='null', viewCount=218, duration=122, uploaderUrl='https://rumble.com/user/powerslap', infoType=STREAM, serviceId=6, url='https://rumble.com/v24rlnu-power-slap-presents-the-art-of-the-slap.html', name='Power Slap Presents: The Art of the Slap', thumbnailUrl='https://sp.rmbl.ws/s8/1/Q/j/3/R/Qj3Rh.0kob-small-Power-Slap-Presents-The-Art.jpg', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='UnchartedX', textualUploadDate='null', viewCount=9240, duration=5627, uploaderUrl='https://rumble.com/user/UnchartedX', infoType=STREAM, serviceId=6, url='https://rumble.com/v23t5ud-lost-ancient-technology-the-colossal-statues-of-ancient-egypt.html', name='Lost Ancient Technology: The Colossal Statues of Ancient Egypt', thumbnailUrl='https://sp.rmbl.ws/s8/1/v/3/U/L/v3ULh.0kob-small-Lost-Ancient-Technology-The.jpg', uploaderVerified='false'}"
            };

            RumbleSharedTests.infoItemsResultsTest(extractor.getService(),
                    page.getItems(),
                    page.getErrors(),
                    someExpectedResults
            );
        }

        // as we fake the audio stream with with video streams for background functionality
        // we have to override this test as we don't want to check if the audio stream has
        // a correct format id - because it can not have a correct id.
        @Override
        public void testAudioStreams() throws Exception {
            final List<AudioStream> audioStreams = extractor().getAudioStreams();
            assertNotNull(audioStreams);

            if (expectedHasAudioStreams()) {
                assertFalse(audioStreams.isEmpty());

                for (final AudioStream stream : audioStreams) {
                    assertIsSecureUrl(stream.getUrl());
                }
            } else {
                assertTrue(audioStreams.isEmpty());
            }
        }
    }

    public static class LiveStreamExtractorTest extends NormalStreamExtractorTest {


        @BeforeAll
        public static void setUp() throws ExtractionException, IOException {
            url = "https://rumble.com/vg7h6j";
            expectedUrl = "https://rumble.com/vg7h6j";
            expectedName = "EP 2422-9AM UKRAINIANS USED BY FBI TO SPY ON AMERICANS?  FISA Warrants Issued MONTHS Before Jan 6th";
            expectedId = "vg7h6j";
            expectedDesc = "EP 2422-9AM UKRAINIAN ASSETS USED BY FBI TO SPY ON AMERICANS - FISA Warrants Issued MONTHS Before Jan 6th";
            expectedCategory = "";
            expectedAgeLimit = 0;
            expectedViewCountAtLeast = 92;
            expectedUploaderName = "The Pete Santilli Show - LIVE Show";
            expectedUploadDate = "2021-04-28 12:07:24.000";
            expectedTextualUploadDate = "2021-04-28T12:07:24+00:00";
            expectedPrivacy = StreamExtractor.Privacy.PUBLIC;
            expectedUploaderUrl = "https://rumble.com/c/PeteLive";
            expectedSupportInfo = "";
            expectedHasAudioStreams = false;
            expectedHasVideoStreams = true;
            expectedArtistProfilePictureInfix = ".rumble.com/live/channel_images/"; // TODO
            expectedLength = 59;
            System.setProperty("downloader", "MOCK");
            //System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH
                    + "/streamExtractorLiveStream"));

            extractor = (RumbleStreamExtractor) Rumble
                    .getStreamExtractor(url);
            extractor.fetchPage();
        }

        /**
         *  Test for {@link RumbleStreamRelatedInfoItemExtractor}
         */
        @Test
        public void rumbleStreamRelatedInfoItemsExtractorTest() throws ExtractionException {

        }
    }
}
