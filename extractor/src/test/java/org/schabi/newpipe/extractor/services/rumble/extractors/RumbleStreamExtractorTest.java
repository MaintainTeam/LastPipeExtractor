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
            final String avatarUrl = extractor().getUploaderAvatars().get(0).getUrl();
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
                    /* here is the speciality 'streamType=LIVE_STREAM' detection test on releated streams */
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='Right Side Broadcasting Network', textualUploadDate='null', viewCount=633000, duration=51896, uploaderUrl='https://rumble.com/user/RSBN', infoType=STREAM, serviceId=6, url='https://rumble.com/v46zwql-live-trump-to-deliver-remarks-in-atkinson-new-hampshire-11624.html', name='LIVE REPLAY: Trump to Deliver Remarks in Atkinson, New Hampshire - 1/16/24', thumbnails='[Image {url=https://ak2.rmbl.ws/s8/1/9/z/E/h/9zEhp.0kob.1-small-LIVE-Trump-to-Deliver-Remar.jpg, height=-1, width=-1, estimatedResolutionLevel=UNKNOWN}]', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=LIVE_STREAM, uploaderName='Right Side Broadcasting Network', textualUploadDate='null', viewCount=5276, duration=-1, uploaderUrl='https://rumble.com/user/RSBN', infoType=STREAM, serviceId=6, url='https://rumble.com/v47mb75-live-president-trump-to-give-remarks-in-concord-new-hampshire-11924.html', name='LIVE: President Trump to Give Remarks in Concord, New Hampshire - 1/19/24', thumbnails='[Image {url=https://ak2.rmbl.ws/s8/1/X/K/D/l/XKDlp.0kob-small-LIVE-President-Trump-to-Giv.jpg, height=-1, width=-1, estimatedResolutionLevel=UNKNOWN}]', uploaderVerified='false'}",
                    "StreamInfoItem{streamType=VIDEO_STREAM, uploaderName='The Breanna Morello Show', textualUploadDate='null', viewCount=60900, duration=3487, uploaderUrl='https://rumble.com/user/TheBreannaMorelloShow', infoType=STREAM, serviceId=6, url='https://rumble.com/v47zo38-the-breanna-morello-show.html', name='Muslim 'Grooming Gangs' are Raping Children in the U.K. - Tommy Robinson; China Allegedly Creates 100% Lethal COVID Strain - Naomi Wolf | The Breanna', thumbnails='[Image {url=https://ak2.rmbl.ws/s8/1/0/U/1/n/0U1np.0kob-small-The-Breanna-Morello-Show.jpg, height=-1, width=-1, estimatedResolutionLevel=UNKNOWN}]', uploaderVerified='false'}"
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
            url = "https://rumble.com/v3e90sa";
            expectedUrl = "https://rumble.com/v3e90sa";
            expectedName = "America 1st News & Politics Live TV | MAGA Media";
            expectedId = "v3e90sa";
            expectedDesc = "Patriot News Outlet Live | America 1st News & Politics";
            expectedCategory = "";
            expectedAgeLimit = 0;
            expectedViewCountAtLeast = 66;
            expectedUploaderName = "Patriot News Outlet Live";
            expectedUploadDate = "2023-09-02 23:01:40.000";
            expectedTextualUploadDate = "2023-09-02T23:01:40+00:00";
            expectedPrivacy = StreamExtractor.Privacy.PUBLIC;
            expectedUploaderUrl = "https://rumble.com/c/PatriotNews4u";
            expectedSupportInfo = "";
            expectedHasAudioStreams = false;
            expectedHasVideoStreams = true;
            expectedArtistProfilePictureInfix = ".rumble.com/live/channel_images/"; // TODO
            expectedLength = 0;
            System.setProperty("downloader", "MOCK");
            //System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH
                    + "/streamExtractorLiveStream"));

            extractor = (RumbleStreamExtractor) Rumble
                    .getStreamExtractor(url);
            extractor.fetchPage();
        }

        @Override
        public StreamType expectedStreamType() {
            return StreamType.LIVE_STREAM;
        }
        /**
         *  Test for {@link RumbleStreamRelatedInfoItemExtractor}
         */
        @Test
        public void rumbleStreamRelatedInfoItemsExtractorTest() throws ExtractionException {

        }
    }
}
