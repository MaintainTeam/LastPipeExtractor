package org.schabi.newpipe.extractor.services.rumble.linkHandler;

import org.junit.BeforeClass;
import org.junit.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ParsingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link RumbleStreamLinkHandlerFactory}
 */
public class RumbleStreamLinkHandlerFactoryTest {
    private static RumbleStreamLinkHandlerFactory linkHandler;

    @BeforeClass
    public static void setUp() {
        linkHandler = RumbleStreamLinkHandlerFactory.getInstance();
        NewPipe.init(DownloaderTestImpl.getInstance());
    }

    @Test
    public void getId() throws Exception {
        String correctIdExpectSuccess = "vdofb7";
        String tooShortIdExpectError = "vdofb";
        String tooLongIdExpectError = "vdofb34";
        String noIdExpectError = "";


        String baseUrls[] = { "https://rumble.com/" };

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

        /** {@value tooLongIdExpectError} */
        for (String baseUrl : baseUrls) {
            String testUrl = baseUrl + tooLongIdExpectError;

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
        String inputVideoUrl = "https://rumble.com/vdofb7";
        //String inputEmbedUrl = "https://www.rumble.com/embed/8gwdyYJ8BUk/";
        String inputId = "vdofb7";

        String expectedUrl = "https://rumble.com/vdofb7";

        assertEquals(expectedUrl,
                linkHandler.fromId(inputId).getUrl());
        assertEquals(expectedUrl,
                linkHandler.fromUrl(inputVideoUrl).getUrl());
        //assertEquals(expectedUrl,
        //        linkHandler.fromUrl(inputEmbedUrl).getUrl());
    }

    @Test
    public void testAcceptUrl() throws ParsingException {
        String inputVideoUrl = "https://rumble.com/vdofb7";
        //String inputEmbedUrl = "https://www.rumble.com/embed/8gwdyYJ8BUk/";

        assertTrue(linkHandler.acceptUrl(inputVideoUrl));
        //assertTrue(linkHandler.acceptUrl(inputEmbedUrl));
    }
}
