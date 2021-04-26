package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.junit.BeforeClass;

import java.io.IOException;

import static org.schabi.newpipe.downloader.DownloaderFactory.RESOURCE_PATH;
import static org.schabi.newpipe.extractor.ServiceList.Rumble;

import org.junit.Test;
import org.schabi.newpipe.downloader.DownloaderFactory;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.services.DefaultSearchExtractorTest;

import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;

import javax.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.schabi.newpipe.extractor.ExtractorAsserts.assertEmptyErrors;
import static org.schabi.newpipe.extractor.services.DefaultTests.assertNoDuplicatedItems;
import static org.schabi.newpipe.extractor.services.rumble.linkHandler.RumbleSearchQueryHandlerFactory.CHANNELS;
import static org.schabi.newpipe.extractor.services.rumble.linkHandler.RumbleSearchQueryHandlerFactory.VIDEOS;
import static org.schabi.newpipe.extractor.utils.Utils.UTF_8;

public class RumbleSearchExtractorTest {

    private static final String MOCK_PATH =
            RESOURCE_PATH + "/services/rumble/extractor/search/";

    public static class MultiplePagesResults extends AbstractSearchBaseTest {

        @BeforeClass
        public static void setUp() throws Exception {
            QUERY = "paul";
            expectedSearchBaseUrl = "rumble.com/search/video?q=";
            expectedSearchOriginalBaseUrl = "rumble.com/search/video?q=";

            System.setProperty("downloader", "MOCK");
//            System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/MultiplePagesResults"));
            extractor = (RumbleSearchExtractor)Rumble.getSearchExtractor(QUERY);
            extractor.fetchPage();
        }

        // override tests
        @Override public InfoItem.InfoType expectedInfoItemType() { return InfoItem.InfoType.STREAM; }
    }

    public static class onlyOnePageResults extends AbstractSearchBaseTest {

        @BeforeClass
        public static void setUp() throws Exception {
            QUERY = "desantis bomb";
            expectedSearchBaseUrl = "rumble.com/search/video?q=";
            expectedSearchOriginalBaseUrl = "rumble.com/search/video?q=";

            System.setProperty("downloader", "MOCK");
//            System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/OnlyOnePageResults"));
            extractor = (RumbleSearchExtractor)Rumble.getSearchExtractor(QUERY);
            extractor.fetchPage();
        }

        // override tests
        @Test
        public void testMoreRelatedItems() throws Exception {
            final ListExtractor.InfoItemsPage<InfoItem> initialPage = extractor().getInitialPage();
            assertFalse("More items available when it shouldn't", initialPage.hasNextPage());
        }
    }

    public static class NoResultsAtAll extends AbstractSearchBaseTest {

        @BeforeClass
        public static void setUp() throws Exception {
            QUERY = "p3423n dkje ";
            expectedSearchBaseUrl = "rumble.com/search/video?q=";
            expectedSearchOriginalBaseUrl = "rumble.com/search/video?q=";

            System.setProperty("downloader", "MOCK");
//            System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/NoResultsAtAll"));
            extractor = (RumbleSearchExtractor)Rumble.getSearchExtractor(QUERY);
            extractor.fetchPage();
        }

        // override tests
        @Test
        public void testRelatedItems() throws Exception {
            final ListExtractor.InfoItemsPage initialPage = extractor.getInitialPage();
            assertFalse("Does have more items, when it should not", initialPage.hasNextPage());
            final List items = initialPage.getItems();
            assertTrue("Page items list is empty", items.isEmpty());
            assertEmptyErrors("Page have errors", initialPage.getErrors());
        }
        @Test
        public void testMoreRelatedItems() throws Exception {
            final ListExtractor.InfoItemsPage<InfoItem> initialPage = extractor().getInitialPage();
            assertFalse("More items available when it shouldn't", initialPage.hasNextPage());
        }
    }

    public static class Channel extends AbstractSearchBaseTest {
        private static SearchExtractor extractor;

        @BeforeClass
        public static void setUp() throws Exception {
            QUERY = "test";
            expectedSearchBaseUrl = "rumble.com/search/channel?q=";
            expectedSearchOriginalBaseUrl = "rumble.com/search/channel?q=";

            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/channel"));
            extractor = Rumble.getSearchExtractor(QUERY, singletonList(CHANNELS), "");
            extractor.fetchPage();
        }

        // override tests
        @Override public InfoItem.InfoType expectedInfoItemType() { return InfoItem.InfoType.CHANNEL; }
    }

    public static class PagingTest {
        @Test
        public void duplicatedItemsCheck() throws Exception {
            System.setProperty("downloader", "MOCK");
//            System.setProperty("downloader", "RECORDING");
            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "/paging"));
            final SearchExtractor extractor = Rumble.getSearchExtractor("cirque du soleil", singletonList(VIDEOS), "");
            extractor.fetchPage();

            final ListExtractor.InfoItemsPage<InfoItem> page1 = extractor.getInitialPage();
            final ListExtractor.InfoItemsPage<InfoItem> page2 = extractor.getPage(page1.getNextPage());

            assertNoDuplicatedItems(Rumble, page1, page2);
        }
    }

    public static class ChannelVerified extends AbstractSearchBaseTest {

        @BeforeClass
        public static void setUp() throws Exception {
            QUERY = "Mark Dice";
            expectedSearchBaseUrl = "rumble.com/search/channel?q=";
            expectedSearchOriginalBaseUrl = "rumble.com/search/channel?q=";

            NewPipe.init(new DownloaderFactory().getDownloader(MOCK_PATH + "verified"));
            extractor = Rumble.getSearchExtractor(QUERY, singletonList(CHANNELS), "");
            extractor.fetchPage();
        }

        @Test
        public void testAtLeastOneVerified() throws IOException, ExtractionException {
            final List<InfoItem> items = extractor.getInitialPage().getItems();
            boolean verified = false;
            for (InfoItem item : items) {
                if (((ChannelInfoItem) item).isVerified()) {
                    verified = true;
                    break;
                }
            }

            assertTrue(verified);
        }

        // override tests
        @Override public InfoItem.InfoType expectedInfoItemType() { return InfoItem.InfoType.CHANNEL; }

    }

    private static abstract class AbstractSearchBaseTest extends DefaultSearchExtractorTest {

        protected static SearchExtractor extractor;
        protected static String QUERY;
        protected static String expectedSearchBaseUrl;
        protected static String expectedSearchOriginalBaseUrl;

        @Override public SearchExtractor extractor() { return extractor; }
        @Override public StreamingService expectedService() { return Rumble; }
        @Override public String expectedName() { return QUERY; }
        @Override public String expectedId() { return QUERY; }
        @Override public String expectedUrlContains() throws UnsupportedEncodingException {
            return  expectedSearchBaseUrl + URLEncoder.encode(QUERY, UTF_8);
        }
        @Override public String expectedOriginalUrlContains() throws UnsupportedEncodingException {
            return expectedSearchOriginalBaseUrl  + URLEncoder.encode(QUERY, UTF_8);
        }
        @Override public String expectedSearchString() { return QUERY; }
        @Nullable @Override public String expectedSearchSuggestion() { return null; }
    }
}
