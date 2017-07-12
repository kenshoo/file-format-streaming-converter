import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;
import java.util.Iterator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by shachafa on 19/02/2017
 */
public class XlsxToCsvConverterImplIntegrationTest {

    @Resource
    private XlsxToCsvConverter xlsxToCsvConverter;
    @Resource
    private CSVPrinterService csvPrinterService;

    @Test
    public void testXLSXTranslationToCSV() throws Exception {
        File translatedFile = xlsxToCsvConverter.convert(TestDataFileUtils.getFilePath(XlsxToCsvConverterImplIntegrationTest.class, "bulk_import_test_1.xlsx"));
        File expectedFile = new File(TestDataFileUtils.getFilePath(XlsxToCsvConverterImplIntegrationTest.class, "bulk_import_expected_test_1.csv"));
        compareCSVFiles(translatedFile, expectedFile);
    }

    private void compareCSVFiles(File actual, File expected) {
        try (CSVParser parserTranslatedFile = csvPrinterService.createCsvParser(actual.getPath(), ",")) {
            try (CSVParser parserExpectedFile = csvPrinterService.createCsvParser(expected.getPath(), ",")) {
                Iterator<CSVRecord> translatedIterator = parserTranslatedFile.iterator();
                Iterator<CSVRecord> expectedIterator = parserExpectedFile.iterator();
                while (translatedIterator.hasNext() && expectedIterator.hasNext()) {
                    CSVRecord translatedRecord = translatedIterator.next();
                    CSVRecord expectedRecord = expectedIterator.next();
                    compareCSVRecords(translatedRecord, expectedRecord);
                }
                if (translatedIterator.hasNext()) {
                    fail("Actual file is longer than expected file");
                }
                if (expectedIterator.hasNext()) {
                    fail("Expected file is longer than actual file");
                }
            }
        } catch (Exception e) {
            fail("Exception while iterating over files");
        }
    }

    private void compareCSVRecords(CSVRecord actual, CSVRecord expected) {
        Iterator<String> actualIterator = actual.iterator();
        Iterator<String> expectedIterator = expected.iterator();
        while (actualIterator.hasNext() && expectedIterator.hasNext()) {
            String nextExpected = expectedIterator.next();
            String nextActual = actualIterator.next();
            assertThat(nextActual, is(nextExpected));
        }
        if (actualIterator.hasNext() || expectedIterator.hasNext()) {
            fail("Records are not similar:\n" + actual + "\n" + expected);
        }
    }
}

