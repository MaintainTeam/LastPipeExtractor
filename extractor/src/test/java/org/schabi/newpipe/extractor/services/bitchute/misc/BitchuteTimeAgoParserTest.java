package org.schabi.newpipe.extractor.services.bitchute.misc;

import org.junit.jupiter.api.Test;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitchuteTimeAgoParserTest {

    @Test
    public void extractUploadDate()  throws ExtractionException {
        BitchuteTimeAgoParser timeAgo = new BitchuteTimeAgoParser();

        // set to a known Date for testing
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2020-12-31T22:30:00+00:00");
        timeAgo.setOffsetDateTime(offsetDateTime);
        String pattern1 = "2 years, 9 months ago"; // whitespace is here 0xa0
        String pattern2 = "10 months, 3 weeks ago";
        String pattern3 = "8 day, 5 hours ago";
        String pattern4 = "2 hour ago";
        String pattern5 = "1 year, 3 months ago";
        String pattern6 = "2 years ago";

        OffsetDateTime pattern1OffsetDateTimeExpected = OffsetDateTime.parse("2018-03-30T22:00:00+00:00");
        OffsetDateTime pattern2OffsetDateTimeExpected = OffsetDateTime.parse("2020-02-08T22:00:00+00:00");
        OffsetDateTime pattern3OffsetDateTimeExpected = OffsetDateTime.parse("2020-12-23T17:00:00+00:00");
        OffsetDateTime pattern4OffsetDateTimeExpected = OffsetDateTime.parse("2020-12-31T20:30:00+00:00");
        OffsetDateTime pattern5OffsetDateTimeExpected = OffsetDateTime.parse("2019-09-30T22:00:00+00:00");
        OffsetDateTime pattern6OffsetDateTimeExpected = OffsetDateTime.parse("2018-12-30T22:00:00+00:00");

        OffsetDateTime pattern1OffsetDateTime = timeAgo.parse(pattern1).offsetDateTime();
        OffsetDateTime pattern2OffsetDateTime = timeAgo.parse(pattern2).offsetDateTime();
        OffsetDateTime pattern3OffsetDateTime = timeAgo.parse(pattern3).offsetDateTime();
        OffsetDateTime pattern4OffsetDateTime = timeAgo.parse(pattern4).offsetDateTime();
        OffsetDateTime pattern5OffsetDateTime = timeAgo.parse(pattern5).offsetDateTime();
        OffsetDateTime pattern6OffsetDateTime = timeAgo.parse(pattern6).offsetDateTime();

        assertEquals(pattern1OffsetDateTimeExpected, pattern1OffsetDateTime);
        assertEquals(pattern2OffsetDateTimeExpected, pattern2OffsetDateTime);
        assertEquals(pattern3OffsetDateTimeExpected, pattern3OffsetDateTime);
        assertEquals(pattern4OffsetDateTimeExpected, pattern4OffsetDateTime);
        assertEquals(pattern5OffsetDateTimeExpected, pattern5OffsetDateTime);
        assertEquals(pattern6OffsetDateTimeExpected, pattern6OffsetDateTime);
    }
}