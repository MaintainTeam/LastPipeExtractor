package org.schabi.newpipe.extractor.services.bitchute.linkHandler;

import org.junit.BeforeClass;
import org.junit.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ParsingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link BitchuteStreamLinkHandlerFactory}
 */
public class BitchuteStreamLinkHandlerFactoryTest {
    private static BitchuteStreamLinkHandlerFactory linkHandler;

    @BeforeClass
    public static void setUp() {
        linkHandler = BitchuteStreamLinkHandlerFactory.getInstance();
        NewPipe.init(DownloaderTestImpl.getInstance());
    }

    @Test
    public void getId() throws Exception {
        String correctIdExpectSuccess = "m4iEhq4L1qU";
        String tooShortIdExpectError = "uHV_f80jP2";
        String noIdExpectError = "";

        String baseUrls[] = { "https://www.bitchute.com/video/", "https://www.bitchute.com/embed/" };

        /** {@value correctIdExpectSuccess} */
        for (String baseUrl : baseUrls) {
            String testUrl = baseUrl + correctIdExpectSuccess;
            assertEquals(correctIdExpectSuccess, 
                linkHandler.fromUrl(testUrl).getId());

        }

        /** {@value tooShortIdExpectError} */
        for (String baseUrl : baseUrls) {
            String testUrl = baseUrl + tooShortIdExpectError;

            ParsingException what = assertThrows(ParsingException.class, () -> linkHandler.fromUrl(testUrl).getId());
            assertTrue(what instanceof ParsingException);
        }

        /** {@value noIdExpectError} */
        for (String baseUrl : baseUrls) {
            String testUrl = baseUrl + noIdExpectError;

            ParsingException what = assertThrows(ParsingException.class, () -> linkHandler.fromUrl(testUrl).getId());
            assertTrue(what instanceof ParsingException);
        }
    }

    @Test
    public void getUrl() throws Exception {
        String inputVideoUrl = "https://www.bitchute.com/video/8gwdyYJ8BUk/";
        String inputEmbedUrl = "https://www.bitchute.com/embed/8gwdyYJ8BUk/";
        String inputId = "8gwdyYJ8BUk";

        String expectedUrl = "https://www.bitchute.com/video/8gwdyYJ8BUk";

        assertEquals(expectedUrl,
                linkHandler.fromId(inputId).getUrl());
        assertEquals(expectedUrl,
                linkHandler.fromUrl(inputVideoUrl).getUrl());
        assertEquals(expectedUrl,
                linkHandler.fromUrl(inputEmbedUrl).getUrl());
    }

    @Test
    public void testAcceptUrl() throws ParsingException {
        String inputVideoUrl = "https://www.bitchute.com/video/8gwdyYJ8BUk/";
        String inputEmbedUrl = "https://www.bitchute.com/embed/8gwdyYJ8BUk/";

        assertTrue(linkHandler.acceptUrl(inputVideoUrl));
        assertTrue(linkHandler.acceptUrl(inputEmbedUrl));
    }
}