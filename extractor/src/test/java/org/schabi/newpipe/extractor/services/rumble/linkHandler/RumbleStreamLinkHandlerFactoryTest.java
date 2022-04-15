package org.schabi.newpipe.extractor.services.rumble.linkHandler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ParsingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for {@link RumbleStreamLinkHandlerFactory}
 */
public class RumbleStreamLinkHandlerFactoryTest {
    private static RumbleStreamLinkHandlerFactory linkHandler;

    @BeforeAll
    public static void setUp() {
        linkHandler = RumbleStreamLinkHandlerFactory.getInstance();
        NewPipe.init(DownloaderTestImpl.getInstance());
    }

    @Test
    public void getId() throws Exception {
        String correctIdExpectSuccess = "vdofb7";
        String tooShortIdExpectError = "vdof";
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

        String[] invalidVideoUrls = {
                "https://pumble.com",
                "https://sumble.com/vdofb7",
                "https://rumble.com",
                "https://rumble.com/",
                "https://rumble.com/category/v23",
                "https://rumble.com/category/v23/"
        };

        for (String invalidVideoUrl : invalidVideoUrls) {
            assertThrows(ParsingException.class, () -> linkHandler.getId(invalidVideoUrl),
                    "This URL is invalid: " + invalidVideoUrl);
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
        String validShortVideoUrl = "https://rumble.com/vdofb7";
        String validLongVideoUrl = "https://rumble.com/vg1hkl-youtube-ceo-wins-major-award-and-you-wont-believe-for-what.html";
        String validWithCategoryVideoUrl = "https://rumble.com/sports/v1850-teeterboard-training-for-cirque-du-soleil.html";

        assertTrue(linkHandler.acceptUrl(validShortVideoUrl));
        assertTrue(linkHandler.acceptUrl(validLongVideoUrl));
        assertTrue(linkHandler.acceptUrl(validWithCategoryVideoUrl));
    }
}
