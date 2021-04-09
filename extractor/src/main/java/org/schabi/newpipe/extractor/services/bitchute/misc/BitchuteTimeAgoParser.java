package org.schabi.newpipe.extractor.services.bitchute.misc;

import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.localization.DateWrapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BitchuteTimeAgoParser {

    private OffsetDateTime now;
    private Pattern patternYear = Pattern.compile("(\\d+)\\Wyear(?:s?)");
    private Pattern patternMonth = Pattern.compile("(\\d+)\\Wmonth(?:s?)");
    private Pattern patternWeek = Pattern.compile("(\\d+)\\Wweek(?:s?)");
    private Pattern patternDay = Pattern.compile("(\\d+)\\Wday(?:s?)");
    private Pattern patternHour = Pattern.compile("(\\d+)\\Whour(?:s?)");

    public BitchuteTimeAgoParser() {
        now = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
        if (null != offsetDateTime) {
            now = offsetDateTime;
        }
    }

    public DateWrapper parse(String textualDate) throws ExtractionException {
        OffsetDateTime offsetDateTime = now;
        Boolean isApproximation = false;

        // bitchute have the weired behavior to call name
        String weiredPattern = "an hour ago";
        if (weiredPattern.equals(textualDate)) {
            // ready to go back
            getResultFor(offsetDateTime, 1, ChronoUnit.HOURS);
            return new DateWrapper(offsetDateTime, false);
        } else { // pattern matching

            Matcher match = patternYear.matcher(textualDate);
            if (match.find()) {
                int years = Integer.parseInt(match.group(1));
                offsetDateTime = getResultFor(offsetDateTime, years, ChronoUnit.YEARS);
                isApproximation = true;
            }

            match = patternMonth.matcher(textualDate);
            if (match.find()) {
                int months = Integer.parseInt(match.group(1));
                offsetDateTime = getResultFor(offsetDateTime, months, ChronoUnit.MONTHS);
                isApproximation = true;
            }

            match = patternWeek.matcher(textualDate);
            if (match.find()) {
                int weeks = Integer.parseInt(match.group(1));
                offsetDateTime = getResultFor(offsetDateTime, weeks, ChronoUnit.WEEKS);
                isApproximation = true;
            }

            match = patternDay.matcher(textualDate);
            if (match.find()) {
                int days = Integer.parseInt(match.group(1));
                offsetDateTime = getResultFor(offsetDateTime, days, ChronoUnit.DAYS);
                isApproximation = true;
            }

            match = patternHour.matcher(textualDate);
            if (match.find()) {
                int hours = Integer.parseInt(match.group(1));
                offsetDateTime = getResultFor(offsetDateTime, hours, ChronoUnit.HOURS);
            }
        }

        if (offsetDateTime.isEqual(now)) {
            throw new ExtractionException("could not read the date: " + textualDate);
        }

        return new DateWrapper(offsetDateTime, isApproximation);
    }

    /**
     * copied from {@link org.schabi.newpipe.extractor.localization.TimeAgoParser#getResultFor(int, ChronoUnit)} ()}
     * but adjusted to set the OffSetDateTime via param and also return the modified OffsetDateTime
     */
    private OffsetDateTime getResultFor(OffsetDateTime offsetDateTime, int timeAgoAmount, ChronoUnit chronoUnit) {
        boolean isApproximation = false;

        switch (chronoUnit) {
            case SECONDS:
            case MINUTES:
            case HOURS:
                offsetDateTime = offsetDateTime.minus(timeAgoAmount, chronoUnit);
                break;

            case DAYS:
            case WEEKS:
            case MONTHS:
                offsetDateTime = offsetDateTime.minus(timeAgoAmount, chronoUnit);
                isApproximation = true;
                break;

            case YEARS:
                // minusDays is needed to prevent `PrettyTime` from showing '12 months ago'.
                offsetDateTime = offsetDateTime.minusYears(timeAgoAmount).minusDays(1);
                isApproximation = true;
                break;
        }

        if (isApproximation) {
            offsetDateTime = offsetDateTime.truncatedTo(ChronoUnit.HOURS);
        }

        return offsetDateTime;
    }
}
