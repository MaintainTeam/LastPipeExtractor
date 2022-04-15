package org.schabi.newpipe.extractor.services.rumble.extractors;

import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.schabi.newpipe.extractor.services.DefaultTests.defaultTestListOfItems;

public class RumbleSharedTests {
    /**
     *
     * @param service the service id
     * @param items the List of items that extend InfoItem
     * @param errors
     * @param someExpectedResults Get it from the failing test output and verify it with the actual
     *                            webpage and update someExpectedResults. To fail simply put here null
     * @param <B>
     * @throws ExtractionException
     * @throws IOException
     */
    public static <B extends InfoItem> void infoItemsResultsTest(
            StreamingService service,
            List<B> items, List<Throwable> errors,
            String[] someExpectedResults) throws ExtractionException, IOException {

        List<B> actualResultsList = items;
        defaultTestListOfItems(service, actualResultsList, errors);

        assertTrue(null != someExpectedResults, "Misconfigured test please provide someExpectedResults[] array");

        String listOfResultsInCaseOfError = "List of actual Results:\n" +
                                            "=======================\n";

        // test if someExpectedResults are matching actualResultsList entries
        List<Integer> foundEntries = new LinkedList<>();
        for (InfoItem actualResult : actualResultsList) {
            listOfResultsInCaseOfError += actualResult + "\n";
            for (int i = 0; i < someExpectedResults.length; i++) {
                String expectedResult = someExpectedResults[i];
                if (expectedResult.equals(actualResult.toString())) {
                    foundEntries.add(i);
                }
            }
        }

        // verify if all someExpectedResults were found
        for (int i = 0; i < someExpectedResults.length; i++) {
            assertTrue(foundEntries.contains(i), "Not found: " + someExpectedResults[i] + "\n\n but we found: " + listOfResultsInCaseOfError);
        }
    }
}
