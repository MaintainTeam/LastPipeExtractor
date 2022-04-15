package org.schabi.newpipe.extractor.services.rumble.linkHandler;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandlerFactory;
import org.schabi.newpipe.extractor.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;

public class RumbleStreamLinkHandlerFactory extends LinkHandlerFactory {

    private static final RumbleStreamLinkHandlerFactory instance = new RumbleStreamLinkHandlerFactory();

    String BASE_URL = "https://rumble.com";
    private String patternMatchId = "^v[a-zA-Z0-9]{4,6}-?";

    private RumbleStreamLinkHandlerFactory() {
    }

    public static RumbleStreamLinkHandlerFactory getInstance() {
        return instance;
    }

    private String assertsID(String id) throws ParsingException {
        if (id == null || !id.matches(patternMatchId)) {
            throw new ParsingException("Given string is not a Rumble Video ID");
        }
        return id;
    }

    @Override
    public String getUrl(String id) throws ParsingException {
        return BASE_URL + "/" + assertsID(id);
    }

    @Override
    public String getId(String urlString) throws ParsingException {
        URL url;
        try {
            url = Utils.stringToURL(urlString);
            if (!url.getAuthority().equals(Utils.stringToURL(BASE_URL).getAuthority())
                    || !url.getProtocol().equals(Utils.stringToURL(BASE_URL).getProtocol())) {
                throw new MalformedURLException();
            }
        } catch (MalformedURLException e) {
            throw new ParsingException("The given URL is not valid: " + urlString);
        }

        String path = url.getPath();

        try {
            path = path.substring(path.lastIndexOf("/") + 1);

            String[] splitPath = path.split("-", 0);
            path = splitPath[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParsingException("Error getting ID");
        }

        return assertsID(path);
    }

    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {
        try {
            getId(url);
            return true;
        } catch (ParsingException e) {
            return false;
        }
    }
}