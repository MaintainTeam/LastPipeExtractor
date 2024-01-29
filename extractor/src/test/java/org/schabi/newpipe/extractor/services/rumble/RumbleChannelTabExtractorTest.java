package org.schabi.newpipe.extractor.services.rumble;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabExtractor;
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabs;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.services.DefaultListExtractorTest;
import org.schabi.newpipe.extractor.services.rumble.extractors.RumbleChannelTabExtractor;

import java.io.IOException;

import static org.schabi.newpipe.extractor.ServiceList.Rumble;
import static org.schabi.newpipe.extractor.services.DefaultTests.defaultTestGetPageInNewExtractor;

class RumbleChannelTabExtractorTest {

    static class All extends DefaultListExtractorTest<ChannelTabExtractor> {
        private static RumbleChannelTabExtractor extractor;

        @BeforeAll
        static void setUp() throws IOException, ExtractionException {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = (RumbleChannelTabExtractor) Rumble
                    .getChannelTabExtractorFromId("c/Bongino", ChannelTabs.VIDEOS);
            extractor.fetchPage();
        }

        @Override public ChannelTabExtractor extractor() throws Exception { return extractor; }
        @Override public StreamingService expectedService() throws Exception { return Rumble; }
        @Override public String expectedName() throws Exception { return ChannelTabs.VIDEOS.getNameId().name(); }
        @Override public String expectedId() throws Exception { return "c/Bongino"; }
        @Override public String expectedUrlContains() throws Exception { return "https://rumble.com/c/Bongino"; }
        @Override public String expectedOriginalUrlContains() throws Exception { return "https://rumble.com/c/Bongino"; }
        @Override public InfoItem.InfoType expectedInfoItemType() { return InfoItem.InfoType.STREAM; }
        @Override public boolean expectedHasMoreItems() { return true; }

        @Test
        void testGetPageInNewExtractor() throws Exception {
            final ChannelTabExtractor newTabExtractor =
                    Rumble.getChannelTabExtractorFromId("c/Bongino", ChannelTabs.VIDEOS);
            defaultTestGetPageInNewExtractor(extractor, newTabExtractor);
        }
    }

    static class Videos extends DefaultListExtractorTest<ChannelTabExtractor> {
        private static RumbleChannelTabExtractor extractor;

        @BeforeAll
        static void setUp() throws IOException, ExtractionException {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = (RumbleChannelTabExtractor) Rumble
                    .getChannelTabExtractorFromId("c/Bongino/videos", ChannelTabs.VIDEOS);
            extractor.fetchPage();
        }

        @Override public ChannelTabExtractor extractor() throws Exception { return extractor; }
        @Override public StreamingService expectedService() throws Exception { return Rumble; }
        @Override public String expectedName() throws Exception { return ChannelTabs.VIDEOS.getNameId().name(); }
        @Override public String expectedId() throws Exception { return "c/Bongino/videos"; }
        @Override public String expectedUrlContains() throws Exception { return "https://rumble.com/c/Bongino/videos"; }
        @Override public String expectedOriginalUrlContains() throws Exception { return "https://rumble.com/c/Bongino/videos"; }
        @Override public InfoItem.InfoType expectedInfoItemType() { return InfoItem.InfoType.STREAM; }
        @Override public boolean expectedHasMoreItems() { return true; }

        @Test
        void testGetPageInNewExtractor() throws Exception {
            final ChannelTabExtractor newTabExtractor =
                    Rumble.getChannelTabExtractorFromId("c/Bongino/videos", ChannelTabs.VIDEOS);
            defaultTestGetPageInNewExtractor(extractor, newTabExtractor);
        }
    }

    static class Live extends DefaultListExtractorTest<ChannelTabExtractor> {
        private static RumbleChannelTabExtractor extractor;

        @BeforeAll
        static void setUp() throws IOException, ExtractionException {
            NewPipe.init(DownloaderTestImpl.getInstance());
            extractor = (RumbleChannelTabExtractor) Rumble
                    .getChannelTabExtractorFromId("c/Bongino/livestreams", ChannelTabs.LIVESTREAMS);
            extractor.fetchPage();
        }

        @Override public ChannelTabExtractor extractor() throws Exception { return extractor; }
        @Override public StreamingService expectedService() throws Exception { return Rumble; }
        @Override public String expectedName() throws Exception { return ChannelTabs.LIVESTREAMS.getNameId().name(); }
        @Override public String expectedId() throws Exception { return "c/Bongino/livestreams"; }
        @Override public String expectedUrlContains() throws Exception { return "https://rumble.com/c/Bongino/livestreams"; }
        @Override public String expectedOriginalUrlContains() throws Exception { return "https://rumble.com/c/Bongino/livestreams"; }
        @Override public InfoItem.InfoType expectedInfoItemType() { return InfoItem.InfoType.STREAM; }
        @Override public boolean expectedHasMoreItems() { return true; }

        @Test
        void testGetPageInNewExtractor() throws Exception {
            final ChannelTabExtractor newTabExtractor =
                    Rumble.getChannelTabExtractorFromId("c/Bongino/livestreams", ChannelTabs.LIVESTREAMS);
            defaultTestGetPageInNewExtractor(extractor, newTabExtractor);
        }
    }
}
