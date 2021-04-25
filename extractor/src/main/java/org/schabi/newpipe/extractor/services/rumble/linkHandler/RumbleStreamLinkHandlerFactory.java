package org.schabi.newpipe.extractor.services.rumble.linkHandler;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandlerFactory;

public class RumbleStreamLinkHandlerFactory extends LinkHandlerFactory {

    private static final RumbleStreamLinkHandlerFactory instance = new RumbleStreamLinkHandlerFactory();

    private RumbleStreamLinkHandlerFactory() {
    }

    public static RumbleStreamLinkHandlerFactory getInstance() {
        return instance;
    }

    private String assertsID(String id) throws ParsingException {
        return ""; // TODO
    }

    @Override
    public String getUrl(String id) {
        return ""; // TODO
    }

    public String getId(String urlString) throws ParsingException, IllegalArgumentException {
        return ""; // TODO
    }

    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {
        return false; // TODO
    }
}
