package org.schabi.newpipe.extractor.services.bitchute.linkHandler;

import org.junit.BeforeClass;
import org.junit.Test;
import org.schabi.newpipe.downloader.DownloaderTestImpl;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ParsingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link BitchuteChannelLinkHandlerFactory}
 */
public class BitchuteChannelLinkHandlerFactoryTest {

    private static BitchuteChannelLinkHandlerFactory linkHandler;


    @BeforeClass
    public static void setUp() {
        linkHandler = BitchuteChannelLinkHandlerFactory.getInstance();
        NewPipe.init(DownloaderTestImpl.getInstance());
    }

    @Test
    public void acceptUrlTest() throws ParsingException {
        String acceptedChannelUrls[] = {
                "https://www.bitchute.com/channel/u3QMwGD7bSW6",
                "https://www.bitchute.com/channel/missoncommander/"
        };

        for (String acceptedChannelUrl : acceptedChannelUrls) {
            assertTrue(linkHandler.acceptUrl(acceptedChannelUrl));
        }

        String baseVideoUrls[] = { "https://www.bitchute.com/video/", "https://www.bitchute.com/embed/" };
        String videoIds[] = {"m4iEhq4L1qU", "8gwdyYJ8BUk" };

        // do not accept URLs which are not channels
        for (String baseUrl : baseVideoUrls) {
            for (String videoId : videoIds) {
                String thisIsNoChannelUrl = baseUrl + videoId;
                assertFalse(linkHandler.acceptUrl(thisIsNoChannelUrl));
            }
        }
    }

    @Test
    public void getId() throws ParsingException {
        String acceptedChannelUrls[] = {
                "https://www.bitchute.com/channel/u3QMwGD7bSW6",
                "https://www.bitchute.com/channel/missoncommander"
        };
        String expectedChannelIds[] = {
                "u3QMwGD7bSW6",
                "missoncommander"
        };

        for (int i = 0; i < acceptedChannelUrls.length; i++) {
            String acceptedChannelUrl = acceptedChannelUrls[i];
            String expectedChannelId = expectedChannelIds[i];

            assertEquals(expectedChannelId,
                    linkHandler.fromUrl(acceptedChannelUrl).getId());
        }
    }

    @Test
    public void getUrl() throws ParsingException {
        String expectedChannelUrls[] = {
                "https://www.bitchute.com/channel/u3QMwGD7bSW6",
                "https://www.bitchute.com/channel/missoncommander"
        };
        String acceptedChannelIds[] = {
                "u3QMwGD7bSW6",
                "missoncommander"
        };

        for (int i = 0; i < expectedChannelUrls.length; i++) {
            String expectedChannelUrl = expectedChannelUrls[i];
            String acceptedChannelId = acceptedChannelIds[i];
            String badChannelId = "channel/"+ acceptedChannelId;

            ParsingException what = assertThrows(ParsingException.class, () -> linkHandler.fromId(badChannelId).getUrl());
            assertTrue(what instanceof ParsingException);

            assertEquals(expectedChannelUrl,
                    linkHandler.fromId(acceptedChannelId).getUrl());
        }
    }
}
