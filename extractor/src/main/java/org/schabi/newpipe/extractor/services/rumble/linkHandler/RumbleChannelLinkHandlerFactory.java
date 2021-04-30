package org.schabi.newpipe.extractor.services.rumble.linkHandler;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;
import org.schabi.newpipe.extractor.utils.Utils;

import java.net.URL;
import java.util.List;

public class RumbleChannelLinkHandlerFactory extends ListLinkHandlerFactory {

    private static final RumbleChannelLinkHandlerFactory instance = new RumbleChannelLinkHandlerFactory();


    public static RumbleChannelLinkHandlerFactory getInstance() {
        return instance;
    }

    /**
     * Returns URL to channel from an ID
     *
     * @param id Channel ID including e.g. 'c/id' or 'user/id'
     * @param contentFilters
     * @param searchFilter
     * @return URL to channel
     */
    @Override
    public String getUrl(String id, List<String> contentFilters, String searchFilter) {
        return "https://rumble.com/" + id;
    }
    
    @Override
    public String getId(String url) throws ParsingException {
        try {
            final URL urlObj = Utils.stringToURL(url);
            String path = urlObj.getPath();

            if (!Utils.isHTTP(urlObj) ) { // TODO check if it is a rumble URL
                throw new ParsingException("the URL given is not a Rumble-URL");
            }

            // remove leading "/"
            path = path.substring(1);
            String[] splitPath = path.split("/");

            if (!path.startsWith("c/") && !path.startsWith("user/")) {
                throw new ParsingException("the URL given is neither a channel nor an user");
            }

            final String id = splitPath[1];

            if (id == null || !id.matches("[A-Za-z0-9_-]+")) {
                throw new ParsingException("The given id is not a Rumble-Channel-ID");
            }

            return splitPath[0] + "/" + id;
        } catch (final Exception exception) {
            throw new ParsingException("Error could not parse url :" + exception.getMessage(), exception);
        }
    }

    @Override
    public boolean onAcceptUrl(String url) {
        try {
            getId(url);
        } catch (ParsingException e) {
            return false;
        }
        return true;
    }
}
