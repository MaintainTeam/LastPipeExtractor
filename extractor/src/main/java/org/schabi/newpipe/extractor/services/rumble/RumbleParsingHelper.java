package org.schabi.newpipe.extractor.services.rumble;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.utils.Utils;

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
}
