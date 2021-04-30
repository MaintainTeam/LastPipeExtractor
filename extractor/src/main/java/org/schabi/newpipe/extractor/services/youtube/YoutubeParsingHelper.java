package org.schabi.newpipe.extractor.services.youtube;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonBuilder;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.grack.nanojson.JsonWriter;

import org.schabi.newpipe.extractor.MetaInfo;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ContentNotAvailableException;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.localization.Localization;
import org.schabi.newpipe.extractor.stream.Description;
import org.schabi.newpipe.extractor.utils.JsonUtils;
import org.schabi.newpipe.extractor.utils.Parser;
import org.schabi.newpipe.extractor.utils.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.schabi.newpipe.extractor.NewPipe.getDownloader;
import static org.schabi.newpipe.extractor.utils.Utils.EMPTY_STRING;
import static org.schabi.newpipe.extractor.utils.Utils.HTTP;
import static org.schabi.newpipe.extractor.utils.Utils.HTTPS;
import static org.schabi.newpipe.extractor.utils.Utils.UTF_8;
import static org.schabi.newpipe.extractor.utils.Utils.isNullOrEmpty;
import static org.schabi.newpipe.extractor.utils.Utils.join;

/*
 * Created by Christian Schabesberger on 02.03.16.
 *
 * Copyright (C) Christian Schabesberger 2016 <chris.schabesberger@mailbox.org>
 * YoutubeParsingHelper.java is part of NewPipe.
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

public class YoutubeParsingHelper {

    private YoutubeParsingHelper() {
    }

    private static final String HARDCODED_CLIENT_VERSION = "2.20200214.04.00";
    private static String clientVersion;

    private static String key;

    private static final String[] HARDCODED_YOUTUBE_MUSIC_KEYS = {"AIzaSyC9XL3ZjWddXya6X74dJoCTL-WEYFDNX30", "67", "0.1"};
    private static String[] youtubeMusicKeys;

    private static Random numberGenerator = new Random();

    /**
     * <code>PENDING+</code> means that the user did not yet submit their choices.
     * Therefore, YouTube & Google should not track the user, because they did not give consent.
     * The three digits at the end can be random, but are required.
     */
    private static final String CONSENT_COOKIE_VALUE = "PENDING+";
    /**
     * Youtube <code>CONSENT</code> cookie. Should prevent redirect to consent.youtube.com
     */
    private static final String CONSENT_COOKIE = "CONSENT=" + CONSENT_COOKIE_VALUE;

    private static final String FEED_BASE_CHANNEL_ID = "https://www.youtube.com/feeds/videos.xml?channel_id=";
    private static final String FEED_BASE_USER = "https://www.youtube.com/feeds/videos.xml?user=";

    private static boolean isGoogleURL(String url) {
        url = extractCachedUrlIfNeeded(url);
        try {
            final URL u = new URL(url);
            final String host = u.getHost();
            return host.startsWith("google.") || host.startsWith("m.google.")
                    || host.startsWith("www.google.");
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean isYoutubeURL(final URL url) {
        final String host = url.getHost();
        return host.equalsIgnoreCase("youtube.com") || host.equalsIgnoreCase("www.youtube.com")
                || host.equalsIgnoreCase("m.youtube.com") || host.equalsIgnoreCase("music.youtube.com");
    }

    public static boolean isYoutubeServiceURL(final URL url) {
        final String host = url.getHost();
        return host.equalsIgnoreCase("www.youtube-nocookie.com") || host.equalsIgnoreCase("youtu.be");
    }

    public static boolean isHooktubeURL(final URL url) {
        final String host = url.getHost();
        return host.equalsIgnoreCase("hooktube.com");
    }

    public static boolean isInvidioURL(final URL url) {
        final String host = url.getHost();
        return host.equalsIgnoreCase("invidio.us")
                || host.equalsIgnoreCase("dev.invidio.us")
                || host.equalsIgnoreCase("www.invidio.us")
                || host.equalsIgnoreCase("redirect.invidious.io")
                || host.equalsIgnoreCase("invidious.snopyta.org")
                || host.equalsIgnoreCase("yewtu.be")
                || host.equalsIgnoreCase("tube.connect.cafe")
                || host.equalsIgnoreCase("invidious.zapashcanon.fr")
                || host.equalsIgnoreCase("invidious.kavin.rocks")
                || host.equalsIgnoreCase("invidious.tube")
                || host.equalsIgnoreCase("invidious.site")
                || host.equalsIgnoreCase("invidious.xyz")
                || host.equalsIgnoreCase("vid.mint.lgbt")
                || host.equalsIgnoreCase("invidiou.site")
                || host.equalsIgnoreCase("invidious.fdn.fr")
                || host.equalsIgnoreCase("invidious.048596.xyz")
                || host.equalsIgnoreCase("invidious.zee.li")
                || host.equalsIgnoreCase("vid.puffyan.us")
                || host.equalsIgnoreCase("ytprivate.com");
    }

    /**
     * Parses the duration string of the video expecting ":" or "." as separators
     *
     * @return the duration in seconds
     * @throws ParsingException when more than 3 separators are found
     */
    public static int parseDurationString(final String input)
            throws ParsingException, NumberFormatException {
        // If time separator : is not detected, try . instead
        final String[] splitInput = input.contains(":")
                ? input.split(":")
                : input.split("\\.");

        String days = "0";
        String hours = "0";
        String minutes = "0";
        final String seconds;

        switch (splitInput.length) {
            case 4:
                days = splitInput[0];
                hours = splitInput[1];
                minutes = splitInput[2];
                seconds = splitInput[3];
                break;
            case 3:
                hours = splitInput[0];
                minutes = splitInput[1];
                seconds = splitInput[2];
                break;
            case 2:
                minutes = splitInput[0];
                seconds = splitInput[1];
                break;
            case 1:
                seconds = splitInput[0];
                break;
            default:
                throw new ParsingException("Error duration string with unknown format: " + input);
        }

        return ((Integer.parseInt(Utils.removeNonDigitCharacters(days)) * 24
                + Integer.parseInt(Utils.removeNonDigitCharacters(hours))) * 60
                + Integer.parseInt(Utils.removeNonDigitCharacters(minutes))) * 60
                + Integer.parseInt(Utils.removeNonDigitCharacters(seconds));
    }

    public static String getFeedUrlFrom(final String channelIdOrUser) {
        if (channelIdOrUser.startsWith("user/")) {
            return FEED_BASE_USER + channelIdOrUser.replace("user/", "");
        } else if (channelIdOrUser.startsWith("channel/")) {
            return FEED_BASE_CHANNEL_ID + channelIdOrUser.replace("channel/", "");
        } else {
            return FEED_BASE_CHANNEL_ID + channelIdOrUser;
        }
    }

    public static OffsetDateTime parseDateFrom(final String textualUploadDate) throws ParsingException {
        try {
            return OffsetDateTime.parse(textualUploadDate);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(textualUploadDate).atStartOfDay().atOffset(ZoneOffset.UTC);
            } catch (DateTimeParseException e1) {
                throw new ParsingException("Could not parse date: \"" + textualUploadDate + "\"", e1);
            }
        }
    }

    /**
     * Checks if the given playlist id is a YouTube Mix (auto-generated playlist)
     * Ids from a YouTube Mix start with "RD"
     *
     * @param playlistId
     * @return Whether given id belongs to a YouTube Mix
     */
    public static boolean isYoutubeMixId(final String playlistId) {
        return playlistId.startsWith("RD") && !isYoutubeMusicMixId(playlistId);
    }

    /**
     * Checks if the given playlist id is a YouTube Music Mix (auto-generated playlist)
     * Ids from a YouTube Music Mix start with "RDAMVM" or "RDCLAK"
     *
     * @param playlistId
     * @return Whether given id belongs to a YouTube Music Mix
     */
    public static boolean isYoutubeMusicMixId(final String playlistId) {
        return playlistId.startsWith("RDAMVM") || playlistId.startsWith("RDCLAK");
    }

    /**
     * Checks if the given playlist id is a YouTube Channel Mix (auto-generated playlist)
     * Ids from a YouTube channel Mix start with "RDCM"
     *
     * @return Whether given id belongs to a YouTube Channel Mix
     */
    public static boolean isYoutubeChannelMixId(final String playlistId) {
        return playlistId.startsWith("RDCM");
    }

    /**
     * Extracts the video id from the playlist id for Mixes.
     *
     * @throws ParsingException If the playlistId is a Channel Mix or not a mix.
     */
    public static String extractVideoIdFromMixId(final String playlistId) throws ParsingException {
        if (playlistId.startsWith("RDMM")) { // My Mix
            return playlistId.substring(4);

        } else if (isYoutubeMusicMixId(playlistId)) { // starts with "RDAMVM" or "RDCLAK"
            return playlistId.substring(6);

        } else if (isYoutubeChannelMixId(playlistId)) { // starts with "RMCM"
            // Channel mix are build with RMCM{channelId}, so videoId can't be determined
            throw new ParsingException("Video id could not be determined from mix id: " + playlistId);

        } else if (isYoutubeMixId(playlistId)) { // normal mix, starts with "RD"
            return playlistId.substring(2);

        } else { // not a mix
            throw new ParsingException("Video id could not be determined from mix id: " + playlistId);
        }
    }

    public static JsonObject getInitialData(final String html) throws ParsingException {
        try {
            try {
                final String initialData = Parser.matchGroup1("window\\[\"ytInitialData\"\\]\\s*=\\s*(\\{.*?\\});", html);
                return JsonParser.object().from(initialData);
            } catch (Parser.RegexException e) {
                final String initialData = Parser.matchGroup1("var\\s*ytInitialData\\s*=\\s*(\\{.*?\\});", html);
                return JsonParser.object().from(initialData);
            }
        } catch (JsonParserException | Parser.RegexException e) {
            throw new ParsingException("Could not get ytInitialData", e);
        }
    }

    public static boolean isHardcodedClientVersionValid() throws IOException, ExtractionException {
        final String url = "https://www.youtube.com/results?search_query=test&pbj=1";

        final Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-YouTube-Client-Name", Collections.singletonList("1"));
        headers.put("X-YouTube-Client-Version", Collections.singletonList(HARDCODED_CLIENT_VERSION));
        final String response = getDownloader().get(url, headers).responseBody();

        return response.length() > 50; // ensure to have a valid response
    }

    private static void extractClientVersionAndKey() throws IOException, ExtractionException {
        final String url = "https://www.youtube.com/results?search_query=test";
        final String html = getDownloader().get(url).responseBody();
        final JsonObject initialData = getInitialData(html);
        final JsonArray serviceTrackingParams = initialData.getObject("responseContext").getArray("serviceTrackingParams");
        String shortClientVersion = null;

        // try to get version from initial data first
        for (final Object service : serviceTrackingParams) {
            final JsonObject s = (JsonObject) service;
            if (s.getString("service").equals("CSI")) {
                final JsonArray params = s.getArray("params");
                for (final Object param : params) {
                    final JsonObject p = (JsonObject) param;
                    final String key = p.getString("key");
                    if (key != null && key.equals("cver")) {
                        clientVersion = p.getString("value");
                    }
                }
            } else if (s.getString("service").equals("ECATCHER")) {
                // fallback to get a shortened client version which does not contain the last two digits
                final JsonArray params = s.getArray("params");
                for (final Object param : params) {
                    final JsonObject p = (JsonObject) param;
                    final String key = p.getString("key");
                    if (key != null && key.equals("client.version")) {
                        shortClientVersion = p.getString("value");
                    }
                }
            }
        }

        String contextClientVersion;
        final String[] patterns = {
                "INNERTUBE_CONTEXT_CLIENT_VERSION\":\"([0-9\\.]+?)\"",
                "innertube_context_client_version\":\"([0-9\\.]+?)\"",
                "client.version=([0-9\\.]+)"
        };
        for (final String pattern : patterns) {
            try {
                contextClientVersion = Parser.matchGroup1(pattern, html);
                if (!isNullOrEmpty(contextClientVersion)) {
                    clientVersion = contextClientVersion;
                    break;
                }
            } catch (Parser.RegexException ignored) {
            }
        }

        if (!isNullOrEmpty(clientVersion) && !isNullOrEmpty(shortClientVersion)) {
            clientVersion = shortClientVersion;
        }

        try {
            key = Parser.matchGroup1("INNERTUBE_API_KEY\":\"([0-9a-zA-Z_-]+?)\"", html);
        } catch (Parser.RegexException e) {
            try {
                key = Parser.matchGroup1("innertubeApiKey\":\"([0-9a-zA-Z_-]+?)\"", html);
            } catch (Parser.RegexException ignored) {
            }
        }
    }

    /**
     * Get the client version
     */
    public static String getClientVersion() throws IOException, ExtractionException {
        if (!isNullOrEmpty(clientVersion)) return clientVersion;
        if (isHardcodedClientVersionValid()) return clientVersion = HARDCODED_CLIENT_VERSION;

        extractClientVersionAndKey();
        if (isNullOrEmpty(key)) throw new ParsingException("Could not extract client version");
        return clientVersion;
    }

    /**
     * Get the key
     */
    public static String getKey() throws IOException, ExtractionException {
        if (!isNullOrEmpty(key)) return key;

        extractClientVersionAndKey();
        if (isNullOrEmpty(key)) throw new ParsingException("Could not extract key");
        return key;
    }

    /**
     * <p>
     * <b>Only use in tests.</b>
     * </p>
     *
     * <p>
     * Quick-and-dirty solution to reset global state in between test classes.
     * </p>
     * <p>
     * This is needed for the mocks because in order to reach that state a network request has to
     * be made. If the global state is not reset and the RecordingDownloader is used,
     * then only the first test class has that request recorded. Meaning running the other
     * tests with mocks will fail, because the mock is missing.
     * </p>
     */
    public static void resetClientVersionAndKey() {
        clientVersion = null;
        key = null;
    }

    /**
     * <p>
     * <b>Only use in tests.</b>
     * </p>
     */
    public static void setNumberGenerator(Random random) {
        numberGenerator = random;
    }

    public static boolean areHardcodedYoutubeMusicKeysValid() throws IOException, ReCaptchaException {
        final String url = "https://music.youtube.com/youtubei/v1/search?alt=json&key=" + HARDCODED_YOUTUBE_MUSIC_KEYS[0];

        // @formatter:off
        byte[] json = JsonWriter.string()
            .object()
                .object("context")
                    .object("client")
                        .value("clientName", "WEB_REMIX")
                        .value("clientVersion", HARDCODED_YOUTUBE_MUSIC_KEYS[2])
                        .value("hl", "en")
                        .value("gl", "GB")
                        .array("experimentIds").end()
                        .value("experimentsToken", "")
                        .value("utcOffsetMinutes", 0)
                        .object("locationInfo").end()
                        .object("musicAppInfo").end()
                    .end()
                    .object("capabilities").end()
                    .object("request")
                        .array("internalExperimentFlags").end()
                        .object("sessionIndex").end()
                    .end()
                    .object("activePlayers").end()
                    .object("user")
                        .value("enableSafetyMode", false)
                    .end()
                .end()
                .value("query", "test")
                .value("params", "Eg-KAQwIARAAGAAgACgAMABqChAEEAUQAxAKEAk%3D")
            .end().done().getBytes(UTF_8);
        // @formatter:on

        final Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-YouTube-Client-Name", Collections.singletonList(HARDCODED_YOUTUBE_MUSIC_KEYS[1]));
        headers.put("X-YouTube-Client-Version", Collections.singletonList(HARDCODED_YOUTUBE_MUSIC_KEYS[2]));
        headers.put("Origin", Collections.singletonList("https://music.youtube.com"));
        headers.put("Referer", Collections.singletonList("music.youtube.com"));
        headers.put("Content-Type", Collections.singletonList("application/json"));
        addCookieHeader(headers);

        final String response = getDownloader().post(url, headers, json).responseBody();

        return response.length() > 50; // ensure to have a valid response
    }

    public static String[] getYoutubeMusicKeys() throws IOException, ReCaptchaException, Parser.RegexException {
        if (youtubeMusicKeys != null && youtubeMusicKeys.length == 3) return youtubeMusicKeys;
        if (areHardcodedYoutubeMusicKeysValid()) return youtubeMusicKeys = HARDCODED_YOUTUBE_MUSIC_KEYS;

        final String url = "https://music.youtube.com/";
        final String html = getDownloader().get(url).responseBody();

        String key;
        try {
            key = Parser.matchGroup1("INNERTUBE_API_KEY\":\"([0-9a-zA-Z_-]+?)\"", html);
        } catch (Parser.RegexException e) {
            key = Parser.matchGroup1("innertube_api_key\":\"([0-9a-zA-Z_-]+?)\"", html);
        }

        final String clientName = Parser.matchGroup1("INNERTUBE_CONTEXT_CLIENT_NAME\":([0-9]+?),", html);

        String clientVersion;
        try {
            clientVersion = Parser.matchGroup1("INNERTUBE_CONTEXT_CLIENT_VERSION\":\"([0-9\\.]+?)\"", html);
        } catch (Parser.RegexException e) {
            try {
                clientVersion = Parser.matchGroup1("INNERTUBE_CLIENT_VERSION\":\"([0-9\\.]+?)\"", html);
            } catch (Parser.RegexException ee) {
                clientVersion = Parser.matchGroup1("innertube_context_client_version\":\"([0-9\\.]+?)\"", html);
            }
        }

        return youtubeMusicKeys = new String[]{key, clientName, clientVersion};
    }



    @Nullable
    public static String getUrlFromNavigationEndpoint(JsonObject navigationEndpoint) throws ParsingException {
        if (navigationEndpoint.has("urlEndpoint")) {
            String internUrl = navigationEndpoint.getObject("urlEndpoint").getString("url");
            if (internUrl.startsWith("https://www.youtube.com/redirect?")) {
                // remove https://www.youtube.com part to fall in the next if block
                internUrl = internUrl.substring(23);
            }

            if (internUrl.startsWith("/redirect?")) {
                // q parameter can be the first parameter
                internUrl = internUrl.substring(10);
                String[] params = internUrl.split("&");
                for (String param : params) {
                    if (param.split("=")[0].equals("q")) {
                        String url;
                        try {
                            url = URLDecoder.decode(param.split("=")[1], UTF_8);
                        } catch (UnsupportedEncodingException e) {
                            return null;
                        }
                        return url;
                    }
                }
            } else if (internUrl.startsWith("http")) {
                return internUrl;
            } else if (internUrl.startsWith("/channel") || internUrl.startsWith("/user") || internUrl.startsWith("/watch")) {
                return "https://www.youtube.com" + internUrl;
            }
        } else if (navigationEndpoint.has("browseEndpoint")) {
            final JsonObject browseEndpoint = navigationEndpoint.getObject("browseEndpoint");
            final String canonicalBaseUrl = browseEndpoint.getString("canonicalBaseUrl");
            final String browseId = browseEndpoint.getString("browseId");

            // All channel ids are prefixed with UC
            if (browseId != null && browseId.startsWith("UC")) {
                return "https://www.youtube.com/channel/" + browseId;
            }

            if (!isNullOrEmpty(canonicalBaseUrl)) {
                return "https://www.youtube.com" + canonicalBaseUrl;
            }

            throw new ParsingException("canonicalBaseUrl is null and browseId is not a channel (\"" + browseEndpoint + "\")");
        } else if (navigationEndpoint.has("watchEndpoint")) {
            StringBuilder url = new StringBuilder();
            url.append("https://www.youtube.com/watch?v=").append(navigationEndpoint.getObject("watchEndpoint").getString("videoId"));
            if (navigationEndpoint.getObject("watchEndpoint").has("playlistId")) {
                url.append("&list=").append(navigationEndpoint.getObject("watchEndpoint")
                        .getString("playlistId"));
            }
            if (navigationEndpoint.getObject("watchEndpoint").has("startTimeSeconds")) {
                url.append("&amp;t=").append(navigationEndpoint.getObject("watchEndpoint")
                        .getInt("startTimeSeconds"));
            }
            return url.toString();
        } else if (navigationEndpoint.has("watchPlaylistEndpoint")) {
            return "https://www.youtube.com/playlist?list=" +
                    navigationEndpoint.getObject("watchPlaylistEndpoint").getString("playlistId");
        }
        return null;
    }

    /**
     * Get the text from a JSON object that has either a simpleText or a runs array.
     *
     * @param textObject JSON object to get the text from
     * @param html       whether to return HTML, by parsing the navigationEndpoint
     * @return text in the JSON object or {@code null}
     */
    @Nullable
    public static String getTextFromObject(JsonObject textObject, boolean html) throws ParsingException {
        if (isNullOrEmpty(textObject)) return null;

        if (textObject.has("simpleText")) return textObject.getString("simpleText");

        if (textObject.getArray("runs").isEmpty()) return null;

        final StringBuilder textBuilder = new StringBuilder();
        for (final Object textPart : textObject.getArray("runs")) {
            String text = ((JsonObject) textPart).getString("text");
            if (html && ((JsonObject) textPart).has("navigationEndpoint")) {
                String url = getUrlFromNavigationEndpoint(((JsonObject) textPart).getObject("navigationEndpoint"));
                if (!isNullOrEmpty(url)) {
                    textBuilder.append("<a href=\"").append(url).append("\">").append(text).append("</a>");
                    continue;
                }
            }
            textBuilder.append(text);
        }

        String text = textBuilder.toString();

        if (html) {
            text = text.replaceAll("\\n", "<br>");
            text = text.replaceAll("  ", " &nbsp;");
        }

        return text;
    }

    @Nullable
    public static String getTextFromObject(JsonObject textObject) throws ParsingException {
        return getTextFromObject(textObject, false);
    }

    @Nullable
    public static String getTextAtKey(final JsonObject jsonObject, final String key)
            throws ParsingException {
        if (jsonObject.isString(key)) {
            return jsonObject.getString(key);
        } else {
            return getTextFromObject(jsonObject.getObject(key));
        }
    }

    public static String fixThumbnailUrl(String thumbnailUrl) {
        if (thumbnailUrl.startsWith("//")) {
            thumbnailUrl = thumbnailUrl.substring(2);
        }

        if (thumbnailUrl.startsWith(HTTP)) {
            thumbnailUrl = Utils.replaceHttpWithHttps(thumbnailUrl);
        } else if (!thumbnailUrl.startsWith(HTTPS)) {
            thumbnailUrl = "https://" + thumbnailUrl;
        }

        return thumbnailUrl;
    }

    public static String getValidJsonResponseBody(final Response response)
            throws ParsingException, MalformedURLException {
        if (response.responseCode() == 404) {
            throw new ContentNotAvailableException("Not found"
                    + " (\"" + response.responseCode() + " " + response.responseMessage() + "\")");
        }

        final String responseBody = response.responseBody();
        if (responseBody.length() < 50) { // ensure to have a valid response
            throw new ParsingException("JSON response is too short");
        }

        // Check if the request was redirected to the error page.
        final URL latestUrl = new URL(response.latestUrl());
        if (latestUrl.getHost().equalsIgnoreCase("www.youtube.com")) {
            final String path = latestUrl.getPath();
            if (path.equalsIgnoreCase("/oops") || path.equalsIgnoreCase("/error")) {
                throw new ContentNotAvailableException("Content unavailable");
            }
        }

        final String responseContentType = response.getHeader("Content-Type");
        if (responseContentType != null
                && responseContentType.toLowerCase().contains("text/html")) {
            throw new ParsingException("Got HTML document, expected JSON response"
                    + " (latest url was: \"" + response.latestUrl() + "\")");
        }

        return responseBody;
    }

    public static Response getResponse(final String url, final Localization localization)
            throws IOException, ExtractionException {
        final Map<String, List<String>> headers = new HashMap<>();
        addYouTubeHeaders(headers);

        final Response response = getDownloader().get(url, headers, localization);
        getValidJsonResponseBody(response);

        return response;
    }

    public static JsonArray getJsonResponse(final String url, final Localization localization)
            throws IOException, ExtractionException {
        Map<String, List<String>> headers = new HashMap<>();
        addYouTubeHeaders(headers);

        final Response response = getDownloader().get(url, headers, localization);

        return JsonUtils.toJsonArray(getValidJsonResponseBody(response));
    }

    public static JsonArray getJsonResponse(final Page page, final Localization localization)
            throws IOException, ExtractionException {
        final Map<String, List<String>> headers = new HashMap<>();
        addYouTubeHeaders(headers);

        final Response response = getDownloader().get(page.getUrl(), headers, localization);

        return JsonUtils.toJsonArray(getValidJsonResponseBody(response));
    }

    public static JsonBuilder<JsonObject> prepareJsonBuilder()
            throws IOException, ExtractionException {
        // @formatter:off
        return JsonObject.builder()
                .object("context")
                    .object("client")
                        .value("clientName", "1")
                        .value("clientVersion", getClientVersion())
                    .end()
                .end();
        // @formatter:on
    }

    /**
     * Add required headers and cookies to an existing headers Map.
     * @see #addClientInfoHeaders(Map)
     * @see #addCookieHeader(Map)
     */
    public static void addYouTubeHeaders(final Map<String, List<String>> headers)
            throws IOException, ExtractionException {
        addClientInfoHeaders(headers);
        addCookieHeader(headers);
    }

    /**
     * Add the <code>X-YouTube-Client-Name</code> and <code>X-YouTube-Client-Version</code> headers.
     * @param headers The headers which should be completed
     */
    public static void addClientInfoHeaders(final Map<String, List<String>> headers)
            throws IOException, ExtractionException {
        if (headers.get("X-YouTube-Client-Name") == null) {
            headers.put("X-YouTube-Client-Name", Collections.singletonList("1"));
        }
        if (headers.get("X-YouTube-Client-Version") == null) {
            headers.put("X-YouTube-Client-Version", Collections.singletonList(getClientVersion()));
        }
    }

    /**
     * Add the <code>CONSENT</code> cookie to prevent redirect to <code>consent.youtube.com</code>
     * @see #CONSENT_COOKIE
     * @param headers the headers which should be completed
     */
    public static void addCookieHeader(final Map<String, List<String>> headers) {
        if (headers.get("Cookie") == null) {
            headers.put("Cookie", Arrays.asList(generateConsentCookie()));
        } else {
            headers.get("Cookie").add(generateConsentCookie());
        }
    }

    public static String generateConsentCookie() {
        return CONSENT_COOKIE + 100 + numberGenerator.nextInt(900);
    }

    public static String extractCookieValue(final String cookieName, final Response response) {
        final List<String> cookies = response.responseHeaders().get("set-cookie");
        int startIndex;
        String result = "";
        for (final String cookie : cookies) {
            startIndex = cookie.indexOf(cookieName);
            if (startIndex != -1) {
                result = cookie.substring(startIndex + cookieName.length() + "=".length(),
                        cookie.indexOf(";", startIndex));
            }
        }
        return result;
    }

    /**
     * Shared alert detection function, multiple endpoints return the error similarly structured.
     * <p>
     * Will check if the object has an alert of the type "ERROR".
     * </p>
     *
     * @param initialData the object which will be checked if an alert is present
     * @throws ContentNotAvailableException if an alert is detected
     */
    public static void defaultAlertsCheck(final JsonObject initialData) throws ParsingException {
        final JsonArray alerts = initialData.getArray("alerts");
        if (!isNullOrEmpty(alerts)) {
            final JsonObject alertRenderer = alerts.getObject(0).getObject("alertRenderer");
            final String alertText = getTextFromObject(alertRenderer.getObject("text"));
            final String alertType = alertRenderer.getString("type", EMPTY_STRING);
            if (alertType.equalsIgnoreCase("ERROR")) {
                throw new ContentNotAvailableException("Got error: \"" + alertText + "\"");
            }
        }
    }

    @Nonnull
    public static List<MetaInfo> getMetaInfo(final JsonArray contents) throws ParsingException {
        final List<MetaInfo> metaInfo = new ArrayList<>();
        for (final Object content : contents) {
            final JsonObject resultObject = (JsonObject) content;
            if (resultObject.has("itemSectionRenderer")) {
                for (final Object sectionContentObject :
                        resultObject.getObject("itemSectionRenderer").getArray("contents")) {

                    final JsonObject sectionContent = (JsonObject) sectionContentObject;
                    if (sectionContent.has("infoPanelContentRenderer")) {
                        metaInfo.add(getInfoPanelContent(sectionContent.getObject("infoPanelContentRenderer")));
                    }
                    if (sectionContent.has("clarificationRenderer")) {
                        metaInfo.add(getClarificationRendererContent(sectionContent.getObject("clarificationRenderer")
                        ));
                    }

                }
            }
        }
        return metaInfo;
    }

    @Nonnull
    private static MetaInfo getInfoPanelContent(final JsonObject infoPanelContentRenderer)
            throws ParsingException {
        final MetaInfo metaInfo = new MetaInfo();
        final StringBuilder sb = new StringBuilder();
        for (final Object paragraph : infoPanelContentRenderer.getArray("paragraphs")) {
            if (sb.length() != 0) {
                sb.append("<br>");
            }
            sb.append(YoutubeParsingHelper.getTextFromObject((JsonObject) paragraph));
        }
        metaInfo.setContent(new Description(sb.toString(), Description.HTML));
        if (infoPanelContentRenderer.has("sourceEndpoint")) {
            final String metaInfoLinkUrl = YoutubeParsingHelper.getUrlFromNavigationEndpoint(
                    infoPanelContentRenderer.getObject("sourceEndpoint"));
            try {
                metaInfo.addUrl(new URL(Objects.requireNonNull(extractCachedUrlIfNeeded(metaInfoLinkUrl))));
            } catch (final NullPointerException | MalformedURLException e) {
                throw new ParsingException("Could not get metadata info URL", e);
            }

            final String metaInfoLinkText = YoutubeParsingHelper.getTextFromObject(
                    infoPanelContentRenderer.getObject("inlineSource"));
            if (isNullOrEmpty(metaInfoLinkText)) {
                throw new ParsingException("Could not get metadata info link text.");
            }
            metaInfo.addUrlText(metaInfoLinkText);
        }

        return metaInfo;
    }

    @Nonnull
    private static MetaInfo getClarificationRendererContent(final JsonObject clarificationRenderer)
            throws ParsingException {
        final MetaInfo metaInfo = new MetaInfo();

        final String title = YoutubeParsingHelper.getTextFromObject(clarificationRenderer.getObject("contentTitle"));
        final String text = YoutubeParsingHelper.getTextFromObject(clarificationRenderer.getObject("text"));
        if (title == null || text == null) {
            throw new ParsingException("Could not extract clarification renderer content");
        }
        metaInfo.setTitle(title);
        metaInfo.setContent(new Description(text, Description.PLAIN_TEXT));

        if (clarificationRenderer.has("actionButton")) {
            final JsonObject actionButton = clarificationRenderer.getObject("actionButton")
                    .getObject("buttonRenderer");
            try {
                final String url = YoutubeParsingHelper.getUrlFromNavigationEndpoint(actionButton.getObject("command"));
                metaInfo.addUrl(new URL(Objects.requireNonNull(extractCachedUrlIfNeeded(url))));
            } catch (final NullPointerException | MalformedURLException e) {
                throw new ParsingException("Could not get metadata info URL", e);
            }

            final String metaInfoLinkText = YoutubeParsingHelper.getTextFromObject(
                    actionButton.getObject("text"));
            if (isNullOrEmpty(metaInfoLinkText)) {
                throw new ParsingException("Could not get metadata info link text.");
            }
            metaInfo.addUrlText(metaInfoLinkText);
        }

        if (clarificationRenderer.has("secondaryEndpoint") && clarificationRenderer.has("secondarySource")) {
            final String url = getUrlFromNavigationEndpoint(clarificationRenderer.getObject("secondaryEndpoint"));
            // ignore Google URLs, because those point to a Google search about "Covid-19"
            if (url != null && !isGoogleURL(url)) {
                try {
                    metaInfo.addUrl(new URL(url));
                    final String description = getTextFromObject(clarificationRenderer.getObject("secondarySource"));
                    metaInfo.addUrlText(description == null ? url : description);
                } catch (MalformedURLException e) {
                    throw new ParsingException("Could not get metadata info secondary URL", e);
                }
            }
        }

        return metaInfo;
    }

    /**
     * Sometimes, YouTube provides URLs which use Google's cache. They look like
     * {@code https://webcache.googleusercontent.com/search?q=cache:CACHED_URL}
     *
     * @param url the URL which might refer to the Google's webcache
     * @return the URL which is referring to the original site
     */
    public static String extractCachedUrlIfNeeded(final String url) {
        if (url == null) {
            return null;
        }
        if (url.contains("webcache.googleusercontent.com")) {
            return url.split("cache:")[1];
        }
        return url;
    }

    public static boolean isVerified(final JsonArray badges) {
        if (Utils.isNullOrEmpty(badges)) {
            return false;
        }

        for (Object badge : badges) {
            final String style = ((JsonObject) badge).getObject("metadataBadgeRenderer")
                    .getString("style");
            if (style != null && (style.equals("BADGE_STYLE_TYPE_VERIFIED")
                    || style.equals("BADGE_STYLE_TYPE_VERIFIED_ARTIST"))) {
                return true;
            }
        }

        return false;
    }

    public static String unescapeDocument(final String doc) {
        return doc
                .replaceAll("\\\\x22", "\"")
                .replaceAll("\\\\x7b", "{")
                .replaceAll("\\\\x7d", "}")
                .replaceAll("\\\\x5b", "[")
                .replaceAll("\\\\x5d", "]");
    }

}
