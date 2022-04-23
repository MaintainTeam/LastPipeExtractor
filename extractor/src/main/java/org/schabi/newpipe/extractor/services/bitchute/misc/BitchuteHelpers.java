package org.schabi.newpipe.extractor.services.bitchute.misc;

import com.grack.nanojson.JsonObject;

public final class BitchuteHelpers {

    private BitchuteHelpers() {
    }

    /**
     * sometimes nanojson threats the content as Integer and not as String if it is
     * internally declares as integer. This is a workaround to give always the result as String
     */
    public static String getIntAlwaysAsString(final JsonObject result, final String jsonKey) {
        String strNumber = result.getString(jsonKey);
        if (null == strNumber) {
            final int number = result.getInt(jsonKey);
            strNumber = String.valueOf(number);
        }
        return strNumber;
    }
}
