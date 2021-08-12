package org.schabi.newpipe.extractor.services.youtube;

import org.junit.BeforeClass;
import org.junit.Test;
import org.schabi.newpipe.downloader.DownloaderFactory;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.channel.ChannelExtractor;
import org.schabi.newpipe.extractor.exceptions.AccountTerminatedException;
import org.schabi.newpipe.extractor.exceptions.ContentNotAvailableException;
import org.schabi.newpipe.extractor.exceptions.ContentNotSupportedException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.services.BaseChannelExtractorTest;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeChannelExtractor;

import java.io.IOException;
import java.util.Random;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.schabi.newpipe.extractor.ExtractorAsserts.assertIsSecureUrl;
import static org.schabi.newpipe.extractor.ServiceList.YouTube;
import static org.schabi.newpipe.extractor.services.DefaultTests.*;

/**
 * Test for {@link ChannelExtractor}
 */
public class YoutubeChannelExtractorTest {

    private static final String RESOURCE_PATH = DownloaderFactory.RESOURCE_PATH + "services/youtube/extractor/channel/";

    public static class NotAvailable {
        @BeforeClass
        public static void setUp() throws IOException {
            YoutubeParsingHelper.resetClientVersionAndKey();
            YoutubeParsingHelper.setNumberGenerator(new Random(1));
            NewPipe.init(new DownloaderFactory().getDownloader(RESOURCE_PATH + "notAvailable"));
        }

        @Test(expected = ContentNotAvailableException.class)
        public void deletedFetch() throws Exception {
            final ChannelExtractor extractor =
                    YouTube.getChannelExtractor("https://www.youtube.com/channel/UCAUc4iz6edWerIjlnL8OSSw");
            extractor.fetchPage();
        }

        @Test(expected = ContentNotAvailableException.class)
        public void nonExistentFetch() throws Exception {
            final ChannelExtractor extractor =
                    YouTube.getChannelExtractor("https://www.youtube.com/channel/DOESNT-EXIST");
            extractor.fetchPage();
        }

        @Test(expected = AccountTerminatedException.class)
        public void accountTerminatedTOSFetch() throws Exception {
            // "This account has been terminated for a violation of YouTube's Terms of Service."
            final ChannelExtractor extractor =
                    YouTube.getChannelExtractor("https://www.youtube.com/channel/UCTGjY2I-ZUGnwVoWAGRd7XQ");
            try {
                extractor.fetchPage();
            } catch (final AccountTerminatedException e) {
                assertEquals(e.getReason(), AccountTerminatedException.Reason.VIOLATION);
                throw e;
            }
        }

        @Test(expected = AccountTerminatedException.class)
        public void accountTerminatedCommunityFetch() throws Exception {
            // "This account has been terminated for violating YouTube's Community Guidelines."
            final ChannelExtractor extractor =
                    YouTube.getChannelExtractor("https://www.youtube.com/channel/UC0AuOxCr9TZ0TtEgL1zpIgA");
            try {
                extractor.fetchPage();
            } catch (final AccountTerminatedException e) {
                assertEquals(e.getReason(), AccountTerminatedException.Reason.VIOLATION);
                throw e;
            }
        }

        @Test(expected = AccountTerminatedException.class)
        public void accountTerminatedHateFetch() throws Exception {
            // "This account has been terminated due to multiple or severe violations
            // of YouTube's policy prohibiting hate speech."
            final ChannelExtractor extractor =
                    YouTube.getChannelExtractor("https://www.youtube.com/channel/UCPWXIOPK-9myzek6jHR5yrg");
            try {
                extractor.fetchPage();
            } catch (final AccountTerminatedException e) {
                assertEquals(e.getReason(), AccountTerminatedException.Reason.VIOLATION);
                throw e;
            }
        }

        @Test(expected = AccountTerminatedException.class)
        public void accountTerminatedBullyFetch() throws Exception {
            // "This account has been terminated due to multiple or severe violations
            // of YouTube's policy prohibiting content designed to harass, bully or threaten."
            final ChannelExtractor extractor =
                    YouTube.getChannelExtractor("https://youtube.com/channel/UCB1o7_gbFp2PLsamWxFenBg");
            try {
                extractor.fetchPage();
            } catch (final AccountTerminatedException e) {
                assertEquals(e.getReason(), AccountTerminatedException.Reason.VIOLATION);
                throw e;
            }
        }

        @Test(expected = AccountTerminatedException.class)
        public void accountTerminatedSpamFetch() throws Exception {
            // "This account has been terminated due to multiple or severe violations
            // of YouTube's policy against spam, deceptive practices and misleading content
            // or other Terms of Service violations."
            final ChannelExtractor extractor =
                    YouTube.getChannelExtractor("https://www.youtube.com/channel/UCoaO4U_p7G7AwalqSbGCZOA");
            try {
                extractor.fetchPage();
            } catch (final AccountTerminatedException e) {
                assertEquals(e.getReason(), AccountTerminatedException.Reason.VIOLATION);
                throw e;
            }
        }

        @Test(expected = AccountTerminatedException.class)
        public void accountTerminatedCopyrightFetch() throws Exception {
            // "This account has been terminated because we received multiple third-party claims
            // of copyright infringement regarding material that the user posted."
            final ChannelExtractor extractor =
                    YouTube.getChannelExtractor("https://www.youtube.com/channel/UCpExuV8qJMfCaSQNL1YG6bQ");
            try {
                extractor.fetchPage();
            } catch (final AccountTerminatedException e) {
                assertEquals(e.getReason(), AccountTerminatedException.Reason.VIOLATION);
                throw e;
            }
        }

    }

    public static class NotSupported {
        @BeforeClass
        public static void setUp() throws IOException {
            YoutubeParsingHelper.resetClientVersionAndKey();
            YoutubeParsingHelper.setNumberGenerator(new Random(1));
            NewPipe.init(new DownloaderFactory().getDownloader(RESOURCE_PATH + "notSupported"));
        }

        @Test(expected = ContentNotSupportedException.class)
        public void noVideoTab() throws Exception {
            final ChannelExtractor extractor = YouTube.getChannelExtractor("https://invidio.us/channel/UC-9-kyTW8ZkZNDHQJ6FgpwQ");
            extractor.fetchPage();
            extractor.getInitialPage();
        }
    }

    public static class Gronkh implements BaseChannelExtractorTest {
        private static YoutubeChannelExtractor extractor;

        @BeforeClass
        public static void setUp() throws Exception {
            YoutubeParsingHelper.resetClientVersionAndKey();
            YoutubeParsingHelper.setNumberGenerator(new Random(1));
            NewPipe.init(new DownloaderFactory().getDownloader(RESOURCE_PATH + "gronkh"));
            extractor = (YoutubeChannelExtractor) YouTube
                    .getChannelExtractor("http://www.youtube.com/user/Gronkh");
            extractor.fetchPage();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // Extractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId() {
            assertEquals(YouTube.getServiceId(), extractor.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            assertEquals("Gronkh", extractor.getName());
        }

        @Test
        public void testId() throws Exception {
            assertEquals("UCYJ61XIK64sp6ZFFS8sctxw", extractor.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UCYJ61XIK64sp6ZFFS8sctxw", extractor.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals("http://www.youtube.com/user/Gronkh", extractor.getOriginalUrl());
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

         /*//////////////////////////////////////////////////////////////////////////
         // ChannelExtractor
         //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription() throws Exception {
            assertThat(extractor.getDescription(), containsString("Ungebremster Spieltrieb seit 1896."));
        }

        @Test
        public void testAvatarUrl() throws Exception {
            String avatarUrl = extractor.getAvatarUrl();
            assertIsSecureUrl(avatarUrl);
            assertTrue(avatarUrl, avatarUrl.contains("yt3"));
        }

        @Test
        public void testBannerUrl() throws Exception {
            String bannerUrl = extractor.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl, bannerUrl.contains("yt3"));
        }

        @Test
        public void testFeedUrl() throws Exception {
            assertEquals("https://www.youtube.com/feeds/videos.xml?channel_id=UCYJ61XIK64sp6ZFFS8sctxw", extractor.getFeedUrl());
        }

        @Test
        public void testSubscriberCount() throws Exception {
            assertTrue("Wrong subscriber count", extractor.getSubscriberCount() >= 0);
            assertTrue("Subscriber count too small", extractor.getSubscriberCount() >= 4e6);
        }

        @Override
        public void testVerified() throws Exception {
            assertTrue(extractor.isVerified());
        }

    }

    // Youtube RED/Premium ad blocking test
    public static class VSauce implements BaseChannelExtractorTest {
        private static YoutubeChannelExtractor extractor;

        @BeforeClass
        public static void setUp() throws Exception {
            YoutubeParsingHelper.resetClientVersionAndKey();
            YoutubeParsingHelper.setNumberGenerator(new Random(1));
            NewPipe.init(new DownloaderFactory().getDownloader(RESOURCE_PATH + "VSauce"));
            extractor = (YoutubeChannelExtractor) YouTube
                    .getChannelExtractor("https://www.youtube.com/user/Vsauce");
            extractor.fetchPage();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // Extractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId() {
            assertEquals(YouTube.getServiceId(), extractor.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            assertEquals("Vsauce", extractor.getName());
        }

        @Test
        public void testId() throws Exception {
            assertEquals("UC6nSFpj9HTCZ5t-N3Rm3-HA", extractor.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UC6nSFpj9HTCZ5t-N3Rm3-HA", extractor.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/user/Vsauce", extractor.getOriginalUrl());
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

         /*//////////////////////////////////////////////////////////////////////////
         // ChannelExtractor
         //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription() throws Exception {
            assertTrue("What it actually was: " + extractor.getDescription(),
                    extractor.getDescription().contains("Our World is Amazing. \n\nQuestions? Ideas? Tweet me:"));
        }

        @Test
        public void testAvatarUrl() throws Exception {
            String avatarUrl = extractor.getAvatarUrl();
            assertIsSecureUrl(avatarUrl);
            assertTrue(avatarUrl, avatarUrl.contains("yt3"));
        }

        @Test
        public void testBannerUrl() throws Exception {
            String bannerUrl = extractor.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl, bannerUrl.contains("yt3"));
        }

        @Test
        public void testFeedUrl() throws Exception {
            assertEquals("https://www.youtube.com/feeds/videos.xml?channel_id=UC6nSFpj9HTCZ5t-N3Rm3-HA", extractor.getFeedUrl());
        }

        @Test
        public void testSubscriberCount() throws Exception {
            assertTrue("Wrong subscriber count", extractor.getSubscriberCount() >= 0);
            assertTrue("Subscriber count too small", extractor.getSubscriberCount() >= 10e6);
        }

        @Override
        public void testVerified() throws Exception {
            assertTrue(extractor.isVerified());
        }

    }

    public static class Kurzgesagt implements BaseChannelExtractorTest {
        private static YoutubeChannelExtractor extractor;

        @BeforeClass
        public static void setUp() throws Exception {
            YoutubeParsingHelper.resetClientVersionAndKey();
            YoutubeParsingHelper.setNumberGenerator(new Random(1));
            NewPipe.init(new DownloaderFactory().getDownloader(RESOURCE_PATH + "kurzgesagt"));
            extractor = (YoutubeChannelExtractor) YouTube
                    .getChannelExtractor("https://www.youtube.com/channel/UCsXVk37bltHxD1rDPwtNM8Q");
            extractor.fetchPage();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // Extractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId() {
            assertEquals(YouTube.getServiceId(), extractor.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            String name = extractor.getName();
            assertTrue(name, name.startsWith("Kurzgesagt"));
        }

        @Test
        public void testId() throws Exception {
            assertEquals("UCsXVk37bltHxD1rDPwtNM8Q", extractor.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UCsXVk37bltHxD1rDPwtNM8Q", extractor.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UCsXVk37bltHxD1rDPwtNM8Q", extractor.getOriginalUrl());
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

         /*//////////////////////////////////////////////////////////////////////////
         // ChannelExtractor
         //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription() throws Exception {
            final String description = extractor.getDescription();
            assertTrue(description, description.contains("small team who want to make science look beautiful"));
            //TODO: Description get cuts out, because the og:description is optimized and don't have all the content
            //assertTrue(description, description.contains("Currently we make one animation video per month"));
        }

        @Test
        public void testAvatarUrl() throws Exception {
            String avatarUrl = extractor.getAvatarUrl();
            assertIsSecureUrl(avatarUrl);
            assertTrue(avatarUrl, avatarUrl.contains("yt3"));
        }

        @Test
        public void testBannerUrl() throws Exception {
            String bannerUrl = extractor.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl, bannerUrl.contains("yt3"));
        }

        @Test
        public void testFeedUrl() throws Exception {
            assertEquals("https://www.youtube.com/feeds/videos.xml?channel_id=UCsXVk37bltHxD1rDPwtNM8Q", extractor.getFeedUrl());
        }

        @Test
        public void testSubscriberCount() throws Exception {
            assertTrue("Wrong subscriber count", extractor.getSubscriberCount() >= 5e6);
        }

        @Override
        public void testVerified() throws Exception {
            assertTrue(extractor.isVerified());
        }
    }

    public static class KurzgesagtAdditional {

        private static YoutubeChannelExtractor extractor;

        @BeforeClass
        public static void setUp() throws Exception {
            // Test is not deterministic, mocks can't be used
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = (YoutubeChannelExtractor) YouTube
                    .getChannelExtractor("https://www.youtube.com/channel/UCsXVk37bltHxD1rDPwtNM8Q");
            extractor.fetchPage();
        }

        @Test
        public void testGetPageInNewExtractor() throws Exception {
            final ChannelExtractor newExtractor = YouTube.getChannelExtractor(extractor.getUrl());
            defaultTestGetPageInNewExtractor(extractor, newExtractor);
        }
    }

    public static class CaptainDisillusion implements BaseChannelExtractorTest {
        private static YoutubeChannelExtractor extractor;

        @BeforeClass
        public static void setUp() throws Exception {
            YoutubeParsingHelper.resetClientVersionAndKey();
            YoutubeParsingHelper.setNumberGenerator(new Random(1));
            NewPipe.init(new DownloaderFactory().getDownloader(RESOURCE_PATH + "captainDisillusion"));
            extractor = (YoutubeChannelExtractor) YouTube
                    .getChannelExtractor("https://www.youtube.com/user/CaptainDisillusion/videos");
            extractor.fetchPage();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // Extractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId() {
            assertEquals(YouTube.getServiceId(), extractor.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            assertEquals("Captain Disillusion", extractor.getName());
        }

        @Test
        public void testId() throws Exception {
            assertEquals("UCEOXxzW2vU0P-0THehuIIeg", extractor.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UCEOXxzW2vU0P-0THehuIIeg", extractor.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/user/CaptainDisillusion/videos", extractor.getOriginalUrl());
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

         /*//////////////////////////////////////////////////////////////////////////
         // ChannelExtractor
         //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription() throws Exception {
            final String description = extractor.getDescription();
            assertTrue(description, description.contains("In a world where"));
        }

        @Test
        public void testAvatarUrl() throws Exception {
            String avatarUrl = extractor.getAvatarUrl();
            assertIsSecureUrl(avatarUrl);
            assertTrue(avatarUrl, avatarUrl.contains("yt3"));
        }

        @Test
        public void testBannerUrl() throws Exception {
            String bannerUrl = extractor.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl, bannerUrl.contains("yt3"));
        }

        @Test
        public void testFeedUrl() throws Exception {
            assertEquals("https://www.youtube.com/feeds/videos.xml?channel_id=UCEOXxzW2vU0P-0THehuIIeg", extractor.getFeedUrl());
        }

        @Test
        public void testSubscriberCount() throws Exception {
            assertTrue("Wrong subscriber count", extractor.getSubscriberCount() >= 5e5);
        }

        @Override
        public void testVerified() throws Exception {
            assertTrue(extractor.isVerified());
        }
    }

    public static class RandomChannel implements BaseChannelExtractorTest {
        private static YoutubeChannelExtractor extractor;

        @BeforeClass
        public static void setUp() throws Exception {
            YoutubeParsingHelper.resetClientVersionAndKey();
            YoutubeParsingHelper.setNumberGenerator(new Random(1));
            NewPipe.init(new DownloaderFactory().getDownloader(RESOURCE_PATH + "random"));
            extractor = (YoutubeChannelExtractor) YouTube
                    .getChannelExtractor("https://www.youtube.com/channel/UCUaQMQS9lY5lit3vurpXQ6w");
            extractor.fetchPage();
        }

        /*//////////////////////////////////////////////////////////////////////////
        // Extractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testServiceId() {
            assertEquals(YouTube.getServiceId(), extractor.getServiceId());
        }

        @Test
        public void testName() throws Exception {
            assertEquals("random channel", extractor.getName());
        }

        @Test
        public void testId() throws Exception {
            assertEquals("UCUaQMQS9lY5lit3vurpXQ6w", extractor.getId());
        }

        @Test
        public void testUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UCUaQMQS9lY5lit3vurpXQ6w", extractor.getUrl());
        }

        @Test
        public void testOriginalUrl() throws ParsingException {
            assertEquals("https://www.youtube.com/channel/UCUaQMQS9lY5lit3vurpXQ6w", extractor.getOriginalUrl());
        }

        /*//////////////////////////////////////////////////////////////////////////
        // ListExtractor
        //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testRelatedItems() throws Exception {
            defaultTestRelatedItems(extractor);
        }

        @Test
        public void testMoreRelatedItems() {
            try {
                defaultTestMoreItems(extractor);
            } catch (final Throwable ignored) {
                return;
            }

            fail("This channel doesn't have more items, it should throw an error");
        }

         /*//////////////////////////////////////////////////////////////////////////
         // ChannelExtractor
         //////////////////////////////////////////////////////////////////////////*/

        @Test
        public void testDescription() throws Exception {
            final String description = extractor.getDescription();
            assertTrue(description, description.contains("Hey there iu will upoload a load of pranks onto this channel"));
        }

        @Test
        public void testAvatarUrl() throws Exception {
            String avatarUrl = extractor.getAvatarUrl();
            assertIsSecureUrl(avatarUrl);
            assertTrue(avatarUrl, avatarUrl.contains("yt3"));
        }

        @Test
        public void testBannerUrl() throws Exception {
            String bannerUrl = extractor.getBannerUrl();
            assertIsSecureUrl(bannerUrl);
            assertTrue(bannerUrl, bannerUrl.contains("yt3"));
        }

        @Test
        public void testFeedUrl() throws Exception {
            assertEquals("https://www.youtube.com/feeds/videos.xml?channel_id=UCUaQMQS9lY5lit3vurpXQ6w", extractor.getFeedUrl());
        }

        @Test
        public void testSubscriberCount() throws Exception {
            long subscribers = extractor.getSubscriberCount();
            assertTrue("Wrong subscriber count: " + subscribers, subscribers >= 50);
        }

        @Override
        public void testVerified() throws Exception {
            assertFalse(extractor.isVerified());
        }
    }
}
