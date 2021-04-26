package org.schabi.newpipe.extractor.services.rumble.linkHandler;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandlerFactory;
import org.schabi.newpipe.extractor.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;

public class RumbleStreamLinkHandlerFactory extends LinkHandlerFactory {

    private static final RumbleStreamLinkHandlerFactory instance = new RumbleStreamLinkHandlerFactory();

    String BASE_URL = "https://rumble.com";

    private RumbleStreamLinkHandlerFactory() {
    }

    public static RumbleStreamLinkHandlerFactory getInstance() {
        return instance;
    }

    private String assertsID(String id) throws ParsingException {
        if (id == null || !id.matches("v[a-zA-Z0-9_-]{5}")) {
            throw new ParsingException("Given string is not a Rumble Video ID");
        }
        return id;
    }

    @Override
    public String getUrl(String id) {
        return BASE_URL + "/" + id;
    }

    public String getId(String urlString) throws ParsingException, IllegalArgumentException {
        URL url;
        try {
            url = Utils.stringToURL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The given URL is not valid");
        }

        String path = url.getPath();

        if (!path.isEmpty()) {
            //remove leading "/"
            path = path.substring(1);
        }

        try {
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