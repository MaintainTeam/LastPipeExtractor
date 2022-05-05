package org.schabi.newpipe.extractor.services.rumble.linkHandler;



import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.schabi.newpipe.extractor.ServiceList.Rumble;
import static org.schabi.newpipe.extractor.services.rumble.linkHandler.RumbleSearchQueryHandlerFactory.CHANNELS;
import static org.schabi.newpipe.extractor.services.rumble.linkHandler.RumbleSearchQueryHandlerFactory.VIDEOS;
import static org.schabi.newpipe.extractor.utils.Utils.EMPTY_STRING;

public class RumbleSearchQueryHandlerFactoryTest {

    @Test
    public void testRegularValues() throws Exception {

        assertEquals("https://rumble.com/search/video?q=Mark+Dice",
                Rumble.getSearchQHFactory().fromQuery("Mark Dice").getUrl());
        assertEquals("https://rumble.com/search/video?q=paul",
                Rumble.getSearchQHFactory().fromQuery("paul").getUrl());
    }

    @Test
    public void testGetContentFilter() throws Exception {
        assertEquals(VIDEOS, Rumble.getSearchQHFactory()
                .fromQuery(EMPTY_STRING, asList(new String[]{VIDEOS}), EMPTY_STRING)
                .getContentFilters().get(0));
        assertEquals(CHANNELS, Rumble.getSearchQHFactory()
                .fromQuery("asdf", asList(new String[]{CHANNELS}), EMPTY_STRING)
                .getContentFilters().get(0));
    }

    @Test
    public void testWithContentfilter() throws Exception {
        assertEquals("https://rumble.com/search/channel?q=%C3%B6%C3%A4%C3%BC",
                Rumble.getSearchQHFactory()
                .fromQuery("öäü", asList(new String[]{CHANNELS}), EMPTY_STRING).getUrl());
        assertEquals("https://rumble.com/search/video?q=%C3%B6%C3%A4%C3%BC",
                Rumble.getSearchQHFactory()
                .fromQuery("öäü", asList(new String[]{VIDEOS}), EMPTY_STRING).getUrl());
    }

    @Test
    public void testGetAvailableContentFilter() {
        final String[] contentFilter = Rumble.getSearchQHFactory().getAvailableContentFilter();
        assertEquals(3, contentFilter.length);
        assertEquals("all", contentFilter[0]);
        assertEquals("videos", contentFilter[1]);
        assertEquals("channels", contentFilter[2]);
    }

    @Test
    public void testGetAvailableSortFilter() {
        final String[] contentFilter = Rumble.getSearchQHFactory().getAvailableSortFilter();
        assertEquals(0, contentFilter.length);
    }
}
