package org.schabi.newpipe.extractor.services.bitchute;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.utils.Utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.schabi.newpipe.extractor.NewPipe.getDownloader;
import static org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants.BITCHUTE_LOCALE;
import static org.schabi.newpipe.extractor.services.bitchute.BitchuteConstants.SEARCH_AUTH_URL;
import static org.schabi.newpipe.extractor.services.bitchute.BitchuteService.BITCHUTE_LINK;

public final class BitchuteParserHelper {

    private static final Map<String, String> VIDEO_ID_2_COMMENT_CF_AUTH = new HashMap<>();
    private static String cookies;
    private static String csrfToken;
    private static String searchAuthNonce;
    private static String searchAuthTimestamp;

    // the time interval the searchAuthTimestamp/Nonce value should be used (in seconds)
    // before renewing
    private static final int SEARCH_AUTH_DATA_TIMEOUT = 60 * 10;

    private BitchuteParserHelper() {
    }

    public static boolean isInitDone() {
        if (cookies == null || csrfToken == null || cookies.isEmpty() || csrfToken.isEmpty()) {
            return false;
        }
        return true;
    }

    public static void init() throws ReCaptchaException, IOException {
        final Response response = getDownloader().get(BITCHUTE_LINK);
        initCookies(response);
    }

    private static void initCookies(final Response response) {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, List<String>> entry : response.responseHeaders().entrySet()) {
            if (entry.getKey().equals("set-cookie")) {
                final List<String> values = entry.getValue();
                for (final String v : values) {
                    final String val = v.split(";", 2)[0];
                    sb.append(val).append(";");
                    if (val.contains("csrf")) {
                        csrfToken = val.split("=", 2)[1];
                    }
                }
                break;
            }
        }
        cookies = sb.toString();
    }

    public static Map<String, List<String>> getPostHeader(final int contentLength)
            throws IOException, ReCaptchaException {
        final Map<String, List<String>> headers = getBasicHeader();
        headers.put("Content-Type", Collections.singletonList("application/x-www-form-urlencoded"));
        headers.put("Content-Length", Collections.singletonList(String.valueOf(contentLength)));
        // DBG System.out.println("Headers: ");
        // DBG for (final Map.Entry m : headers.entrySet()) {
        // DBG     System.out.println(m.getKey() + ": " + m.getValue());
        // DBG }
        return headers;
    }

    public static Map<String, List<String>> getBasicHeader()
            throws IOException, ReCaptchaException {
        if (!isInitDone()) {
            init();
        }
        final Map<String, List<String>> headers = new HashMap<>();
        headers.put("Cookie", Collections.singletonList(cookies));
        headers.put("Referer", Collections.singletonList(BITCHUTE_LINK));
        return headers;
    }

    public static String getSubscriberCountForChannelID(final String channelID)
            throws IOException, ExtractionException {
        if (!isInitDone()) {
            init();
        }

        final byte[] data =
                String.format("csrfmiddlewaretoken=%s", csrfToken).getBytes(StandardCharsets.UTF_8);
        final Response response = getDownloader().post(
                String.format(BitchuteConstants.BASE_URL_CHANNEL + "/%s/counts/", channelID),
                getPostHeader(data.length),
                data
        );

        try {
            final JsonObject jsonObject = JsonParser.object().from(response.responseBody());
            return String.valueOf(jsonObject.getLong("subscriber_count"));
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse bitchute sub count json");
        }
    }

    public static VideoCount getVideoCountObjectForStreamID(final String streamID)
            throws IOException, ExtractionException {
        if (!isInitDone()) {
            init();
        }

        final byte[] data =
                String.format("csrfmiddlewaretoken=%s", csrfToken).getBytes(StandardCharsets.UTF_8);
        final Response response = getDownloader().post(
                String.format(BitchuteConstants.BASE_URL_VIDEO + "/%s/counts/", streamID),
                getPostHeader(data.length),
                data
        );

        try {
            final JsonObject jsonObject = JsonParser.object().from(response.responseBody());
            return (new VideoCount(jsonObject.getInt("like_count"),
                    jsonObject.getInt("dislike_count"), jsonObject.getInt("view_count")));
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse bitchute like/view count json");
        }

    }

    public static Document getExtendDocumentForUrl(final String pageUrl, final String offset)
            throws IOException, ExtractionException {
        if (!isInitDone()) {
            init();
        }

        final byte[] data = String.format("csrfmiddlewaretoken=%s&offset=%s", csrfToken, offset)
                .getBytes(StandardCharsets.UTF_8);
        final Response response = getDownloader().post(
                String.format("%s/extend/", pageUrl),
                getPostHeader(data.length),
                data
        );

        try {
            final JsonObject jsonObject = JsonParser.object().from(response.responseBody());
            return Jsoup.parse(jsonObject.getString("html")
                    .replace("\n", ""), pageUrl);
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse bitchute json key: 'html'");
        }
    }

    @Nonnull
    public static JsonArray getComments(@Nonnull final String id,
                                        @Nonnull final String url,
                                        final int commentCount)
            throws IOException, ExtractionException {
        final String cfAuth = getCfAuth(id);
        final JsonArray jsonArray;

        if (cfAuth != null) {
            jsonArray = getComments(cfAuth, commentCount);
        } else {
            final Downloader downloader = NewPipe.getDownloader();
            final Response response = downloader.get(url);
            final Pattern pattern = Pattern.compile("\\{cf_auth: '([^']+)'");
            final Matcher match = pattern.matcher(response.responseBody());

            if (match.find()) {
                jsonArray = getComments(match.group(1), commentCount);
            } else {
                // could not find anything so empty array
                jsonArray = new JsonArray();
            }
        }

        return jsonArray;
    }

    @Nonnull
    private static JsonArray getComments(@Nonnull final String cfAuth, final int commentCount)
            throws IOException, ExtractionException {
        // in case you get a JsonObject instead of JsonArray that might be because they changed
        // something with this parameter 'isNameValuesArrays=false'
        // -> basically ATM if you add this parameter to the url with the help of 'moreHeaders'
        //    parameter (regardless if set to false or true) you will get an JsonObject.
        //    ONLY dropping this parameter results in getting a JsonArray as we want.
        return (JsonArray) getJsonDataFromCommentEndpoints("/api/get_comments/", cfAuth,
                String.format(BITCHUTE_LOCALE, "&commentCount=%d", commentCount));
    }

    /*
    private static int getCommentsCount(final String cfAuth)
            throws IOException, ExtractionException {
        final String key = "commentCount";
        final JsonObject counts = (JsonObject)
                getJsonDataFromCommentEndpoints("/api/get_comment_count/", cfAuth, "");

        Objects.requireNonNull(counts.get(key));
        return counts.getInt(key);
    }
     */

    @Nonnull
    private static Object getJsonDataFromCommentEndpoints(
            @Nonnull final String apiEndpoint,
            @Nonnull final String cfAuth,
            @Nonnull final String moreHeaders) throws IOException, ExtractionException {
        if (!isInitDone()) {
            init();
        }

        final String dataWithPlaceholders = "cf_auth=%s" + moreHeaders;

        final String urlEncodeCfAuth = Utils.encodeUrlUtf8(cfAuth);
        final byte[] data = String.format(dataWithPlaceholders, urlEncodeCfAuth)
                .getBytes(StandardCharsets.UTF_8);

        final Response response = getDownloader().post(
                String.format(BITCHUTE_LOCALE, "%s%s", BitchuteConstants.COMMENTS_URL, apiEndpoint),
                getPostHeader(data.length),
                data
        );

        try {
            final Object jsonObject = JsonParser.any().from(response.responseBody());
            return Objects.requireNonNull(jsonObject);
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse bitchute comments results JsonObject");
        }
    }

    public static JsonObject getSearchResultForQuery(@Nonnull final String query,
                                                     @Nullable final String sortQuery,
                                                     final int pageNumber)
            throws IOException, ExtractionException {
        if (!isInitDone()) {
            init();
        }

        if (!isSearchInitDone()) {
            initSearch();
        }

        final String dataWithPlaceholders =
                "csrfmiddlewaretoken=%s&timestamp=%s&nonce=%s&query=%s%s&page=%d";

        final byte[] data = String.format(BITCHUTE_LOCALE, dataWithPlaceholders, csrfToken,
                        Utils.encodeUrlUtf8(searchAuthTimestamp),
                        searchAuthNonce, query,
                        Objects.requireNonNullElse(sortQuery, ""),
                        pageNumber)
                .getBytes(StandardCharsets.UTF_8);

        final Response response = getDownloader().post(
                String.format("%s/api/search/list/", BitchuteConstants.BASE_URL),
                getPostHeader(data.length),
                data
        );

        final JsonObject jsonObject;
        try {
            jsonObject = JsonParser.object().from(response.responseBody());
            final String keySuccess = "success";
            final String keyResults = "results";
            if (jsonObject.has(keySuccess) && jsonObject.getBoolean(keySuccess)
                    && jsonObject.has(keyResults)) {

                searchAuthTimestamp = jsonObject.getString("timestamp");
                searchAuthNonce = jsonObject.getString("nonce");

                return jsonObject;
            }
        } catch (final JsonParserException e) {
            throw new ParsingException("Could not parse bitchute search results JsonObject");
        }
        throw new ExtractionException(
                "Server response for bitchute search results was not successful: "
                        + jsonObject.getObject("error"));
    }

    private static void initSearch() throws ReCaptchaException, IOException {
        final Response response = getDownloader().get(SEARCH_AUTH_URL);
        initCookies(response);

        extractAndStoreSearchAuth(response.responseBody());
    }

    public static boolean extractAndStoreSearchAuth(@Nonnull final String body) {
        final Pattern pattern = Pattern.compile("searchAuth\\('([^']+)', '([^']+)'");

        final Matcher match = pattern.matcher(body);

        if (match.find()) {
            searchAuthTimestamp = match.group(1);
            searchAuthNonce = match.group(2);

            return true;
        }
        return false;
    }

    private static boolean isSearchInitDone() {
        if (searchAuthTimestamp == null || searchAuthNonce == null) {
            return false;
        } else {
            return isSearchAuthStillUsable(searchAuthTimestamp);
        }
    }

    // Check if the timestamp we currently have is not too old.
    private static boolean isSearchAuthStillUsable(@Nonnull final String timestamp) {
        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        final LocalDateTime authTimestamp = LocalDateTime.parse(timestamp,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"));

        final long diff =
                now.toEpochSecond(ZoneOffset.UTC) - authTimestamp.toEpochSecond(ZoneOffset.UTC);

        return diff < SEARCH_AUTH_DATA_TIMEOUT;
    }

    public static boolean extractAndStoreCfAuth(@Nonnull final String id,
                                                @Nonnull final String body) {
        final Pattern pattern = Pattern.compile("\\{cf_auth: '([^']+)'");
        final Matcher match = pattern.matcher(body);

        if (match.find()) {
            VIDEO_ID_2_COMMENT_CF_AUTH.put(id, match.group(1));
            return true;
        }
        return false;
    }

    @Nullable
    public static String getCfAuth(@Nonnull final String id) {
        return VIDEO_ID_2_COMMENT_CF_AUTH.get(id);
    }

    public static class VideoCount {
        private final long likeCount;
        private final long dislikeCount;
        private final long viewCount;

        public VideoCount(final long likeCount, final long dislikeCount, final long viewCount) {
            this.likeCount = likeCount;
            this.dislikeCount = dislikeCount;
            this.viewCount = viewCount;
        }

        public long getLikeCount() {
            return likeCount;
        }

        public long getDislikeCount() {
            return dislikeCount;
        }

        public long getViewCount() {
            return viewCount;
        }
    }
}
