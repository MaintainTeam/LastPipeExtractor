package org.schabi.newpipe.extractor.services.rumble;

import org.jsoup.nodes.Document;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.utils.Utils;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.schabi.newpipe.extractor.ServiceList.Rumble;

public class RumbleParsingHelper {

    private RumbleParsingHelper() {
    }

    public static int parseDurationString(final String input) throws ParsingException {
        // input has the form of 18m10s or 1d10h20m4s etc
        return parseDurationString(input, "(d|h|m|s)");
    }

    public static int parseDurationString(final String input, String split)
            throws ParsingException, NumberFormatException {

        String[] splitInput = input.split(split);
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

    /**
     *
     * @param shouldThrowOnError if true a ParsingException is thrown if function failed for whatever reason
     * @param msg in case of Exception the error message that is passed
     * @param function the function that extract the desired string
     * @return the extracted string or null if shouldThrowOnError is set to false
     * @throws ParsingException
     */
    public static String extractSafely(boolean shouldThrowOnError, String msg , ExtractFunction function) throws ParsingException {
        String retValue = null;
        try {
            retValue = function.run();
        } catch (Exception e) {
            if (shouldThrowOnError) {
                throw new ParsingException(msg + ": " + e);
            }
        }
        return retValue;
    }

    /**
     * interface for {@link #extractSafely} extractor function
     */
    public interface ExtractFunction {
        String run();
    }

    /**
     *  TODO implement a faster/easier way to achive same goals
     * @param classStr
     * @return null if there was a letter and not a image, xor url with the uploader thumbnail
     * @throws Exception
     */
    public static String totalMessMethodToGetUploaderThumbnailUrl(String classStr, Document doc) throws Exception {

        // special case there is only a letter and no image as user thumbnail
        if (classStr.contains("user-image--letter")) {
            // assume uploader name will do the job
            return null;
        }

        // extract checksum
        Pattern matchChecksum = Pattern.compile("([a-fA-F0-9]{32})");
        Matcher match2 = matchChecksum.matcher(classStr);
        if (match2.find()) {
            String chkSum = match2.group(1);

            // extract uploader thumbnail url
            String matchThat = doc.toString();
            int pos = matchThat.indexOf(chkSum);
            String preciselyMatchHere = matchThat.substring(pos);

            Pattern channelUrl = Pattern.compile("\\W+background-image:\\W+url(?:\\()([^)]*)(?:\\));");
            Matcher match = channelUrl.matcher(preciselyMatchHere);
            if (match.find()) {
                String thumbnailUrl = match.group(1);
                return thumbnailUrl;
            }
        }

        throw new Exception(classStr);
    }

    public static String moreTotalMessMethodToGenerateUploaderUrl(String classStr, Document doc, String uploaderName) throws Exception {

        String thumbnailUrl = totalMessMethodToGetUploaderThumbnailUrl(classStr, doc);
        if (thumbnailUrl == null) {
            String uploaderUrl = Rumble.getBaseUrl() + "/user/" + uploaderName;
            return uploaderUrl;
        }

        // Again another special case here
        URL url = Utils.stringToURL(thumbnailUrl);
        if (!url.getAuthority().contains("rmbl.ws")) {
            // there is no img hosted on rumble so we can't rely on it to extract the Channel.
            // So we try to use the name here too.
            String uploaderUrl = Rumble.getBaseUrl() + "/user/"  + uploaderName;
            return uploaderUrl;
        }

        // extract uploader name
        String path = thumbnailUrl.substring(thumbnailUrl.lastIndexOf("/") + 1);
        String[] splitPath = path.split("-", 0);
        String theUploader = splitPath[1];

        // the uploaderUrl
        String uploaderUrl = Rumble.getBaseUrl() + "/user/"  + theUploader;
        return uploaderUrl;
    }
}
