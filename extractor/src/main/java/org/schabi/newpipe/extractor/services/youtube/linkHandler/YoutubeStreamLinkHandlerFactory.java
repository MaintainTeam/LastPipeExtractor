package org.schabi.newpipe.extractor.services.youtube.linkHandler;

import org.schabi.newpipe.extractor.exceptions.FoundAdException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
import org.schabi.newpipe.extractor.utils.Utils;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by Christian Schabesberger on 02.02.16.
 *
 * Copyright (C) Christian Schabesberger 2018 <chris.schabesberger@mailbox.org>
 * YoutubeStreamLinkHandlerFactory.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

public class YoutubeStreamLinkHandlerFactory extends LinkHandlerFactory {

    private static final Pattern YOUTUBE_VIDEO_ID_REGEX_PATTERN = Pattern.compile("^([a-zA-Z0-9_-]{11})");
    private static final YoutubeStreamLinkHandlerFactory instance = new YoutubeStreamLinkHandlerFactory();
    private static final List<String> SUBPATHS = Arrays.asList("embed/", "shorts/", "watch/", "v/", "w/");

    private YoutubeStreamLinkHandlerFactory() {
    }

    public static YoutubeStreamLinkHandlerFactory getInstance() {
        return instance;
    }

    @Nullable
    private static String extractId(@Nullable final String id) {
        if (id != null) {
            final Matcher m = YOUTUBE_VIDEO_ID_REGEX_PATTERN.matcher(id);
            return m.find() ? m.group(1) : null;
        }
        return null;
    }

    private static String assertIsId(@Nullable final String id) throws ParsingException {
        final String extractedId = extractId(id);
        if (extractedId != null) {
            return extractedId;
        } else {
            throw new ParsingException("The given string is not a Youtube-Video-ID");
        }
    }

    @Override
    public String getUrl(String id) {
        return "https://www.youtube.com/watch?v=" + id;
    }

    @Override
    public String getId(String urlString) throws ParsingException, IllegalArgumentException {
        try {
            URI uri = new URI(urlString);
            String scheme = uri.getScheme();

            if (scheme != null && (scheme.equals("vnd.youtube") || scheme.equals("vnd.youtube.launch"))) {
                String schemeSpecificPart = uri.getSchemeSpecificPart();
                if (schemeSpecificPart.startsWith("//")) {
                    final String extractedId = extractId(schemeSpecificPart.substring(2));
                    if (extractedId != null) {
                        return extractedId;
                    }

                    urlString = "https:" + schemeSpecificPart;
                } else {
                    return assertIsId(schemeSpecificPart);
                }
            }
        } catch (URISyntaxException ignored) {
        }

        URL url;
        try {
            url = Utils.stringToURL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The given URL is not valid");
        }

        String host = url.getHost();
        String path = url.getPath();
        // remove leading "/" of URL-path if URL-path is given
        if (!path.isEmpty()) {
            path = path.substring(1);
        }

        if (!Utils.isHTTP(url) || !(YoutubeParsingHelper.isYoutubeURL(url) ||
                YoutubeParsingHelper.isYoutubeServiceURL(url) || YoutubeParsingHelper.isHooktubeURL(url) ||
                YoutubeParsingHelper.isInvidioURL(url) || YoutubeParsingHelper.isY2ubeURL(url))) {
            if (host.equalsIgnoreCase("googleads.g.doubleclick.net")) {
                throw new FoundAdException("Error found ad: " + urlString);
            }

            throw new ParsingException("The url is not a Youtube-URL");
        }

        if (YoutubePlaylistLinkHandlerFactory.getInstance().acceptUrl(urlString)) {
            throw new ParsingException("Error no suitable url: " + urlString);
        }

        // using uppercase instead of lowercase, because toLowercase replaces some unicode characters
        // with their lowercase ASCII equivalent. Using toLowercase could result in faultily matching unicode urls.
        switch (host.toUpperCase()) {
            case "WWW.YOUTUBE-NOCOOKIE.COM": {
                if (path.startsWith("embed/")) {
                    String id = path.substring(6); // embed/

                    return assertIsId(id);
                }

                break;
            }

            case "YOUTUBE.COM":
            case "WWW.YOUTUBE.COM":
            case "M.YOUTUBE.COM":
            case "MUSIC.YOUTUBE.COM": {
                if (path.equals("attribution_link")) {
                    String uQueryValue = Utils.getQueryValue(url, "u");

                    URL decodedURL;
                    try {
                        decodedURL = Utils.stringToURL("http://www.youtube.com" + uQueryValue);
                    } catch (MalformedURLException e) {
                        throw new ParsingException("Error no suitable url: " + urlString);
                    }

                    String viewQueryValue = Utils.getQueryValue(decodedURL, "v");
                    return assertIsId(viewQueryValue);
                }

                String maybeId = getIdFromSubpathsInPath(path);
                if (maybeId != null) return maybeId;

                String viewQueryValue = Utils.getQueryValue(url, "v");
                return assertIsId(viewQueryValue);
            }

            case "Y2U.BE":
            case "YOUTU.BE": {
                String viewQueryValue = Utils.getQueryValue(url, "v");
                if (viewQueryValue != null) {
                    return assertIsId(viewQueryValue);
                }

                return assertIsId(path);
            }

            case "HOOKTUBE.COM":
            case "INVIDIO.US":
            case "DEV.INVIDIO.US":
            case "WWW.INVIDIO.US":
            case "REDIRECT.INVIDIOUS.IO":
            case "INVIDIOUS.SNOPYTA.ORG":
            case "YEWTU.BE":
            case "TUBE.CONNECT.CAFE":
            case "TUBUS.EDUVID.ORG":
            case "INVIDIOUS.KAVIN.ROCKS":
            case "INVIDIOUS-US.KAVIN.ROCKS":
            case "PIPED.KAVIN.ROCKS":
            case "INVIDIOUS.SITE":
            case "VID.MINT.LGBT":
            case "INVIDIOU.SITE":
            case "INVIDIOUS.FDN.FR":
            case "INVIDIOUS.048596.XYZ":
            case "INVIDIOUS.ZEE.LI":
            case "VID.PUFFYAN.US":
            case "YTPRIVATE.COM":
            case "INVIDIOUS.NAMAZSO.EU":
            case "INVIDIOUS.SILKKY.CLOUD":
            case "INVIDIOUS.EXONIP.DE":
            case "INV.RIVERSIDE.ROCKS":
            case "INVIDIOUS.BLAMEFRAN.NET":
            case "INVIDIOUS.MOOMOO.ME":
            case "YTB.TROM.TF":
            case "YT.CYBERHOST.UK":
            case "Y.COM.CM": { // code-block for hooktube.com and Invidious instances
                if (path.equals("watch")) {
                    String viewQueryValue = Utils.getQueryValue(url, "v");
                    if (viewQueryValue != null) {
                        return assertIsId(viewQueryValue);
                    }
                }
                String maybeId = getIdFromSubpathsInPath(path);
                if (maybeId != null) return maybeId;

                String viewQueryValue = Utils.getQueryValue(url, "v");
                if (viewQueryValue != null) {
                    return assertIsId(viewQueryValue);
                }

                return assertIsId(path);
            }
        }

        throw new ParsingException("Error no suitable url: " + urlString);
    }

    @Override
    public boolean onAcceptUrl(final String url) throws FoundAdException {
        try {
            getId(url);
            return true;
        } catch (FoundAdException fe) {
            throw fe;
        } catch (ParsingException e) {
            return false;
        }
    }

    private String getIdFromSubpathsInPath(String path) throws ParsingException {
        for (final String subpath : SUBPATHS) {
            if (path.startsWith(subpath)) {
                String id = path.substring(subpath.length());
                return assertIsId(id);
            }
        }
        return null;
    }
}
