package com.kenshoo.xlsx2csv;

import com.google.common.base.Charsets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.*;
import java.util.Iterator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by shachafa on 20/07/2017
 */
@RunWith(MockitoJUnitRunner.class)
public class XlsxToCsvConverterTest {

    private static final String ACTUAL_TRANSLATED_FILE = "actual_file_after_translation.csv";
    private static final String EXPECTED_CSV_FILE = "expected_csv_file.csv";
    private XlsxToCsvConverter xlsxToCsvConverter = new XlsxToCsvConverter.Builder().build();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testTranslateFile() throws Exception {
        translateFile("bulk_import_test_1.xlsx", "bulk_import_expected_test_1.csv");
    }

    @Test
    public void testTranslateEmptyFile() throws Exception {
        expectedException.expect(IOException.class);
        translateFile("bulk_import_test_2_empty.xlsx", "bulk_import_expected_test_2_empty.csv");
    }

    @Test
    public void testTranslateFileWithHiddenSheets() throws Exception {
        translateFile("bulk_import_test_3_with_hidden_sheets.xlsx", "bulk_import_expected_test_1.csv");
    }

    private void translateFile(String fileNameToTranslate, String expectedFileName) throws Exception{
        File expectedFile = new File(EXPECTED_CSV_FILE);
        expectedFile.deleteOnExit();
        File actualTranslatedFile = new File(ACTUAL_TRANSLATED_FILE);
        actualTranslatedFile.deleteOnExit();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(actualTranslatedFile);
            xlsxToCsvConverter.convert(ClassLoader.getSystemResourceAsStream(fileNameToTranslate), fileOutputStream);
            FileUtils.copyInputStreamToFile(ClassLoader.getSystemResourceAsStream(expectedFileName), expectedFile);
            compareCSVFiles(actualTranslatedFile.getPath(), expectedFile.getPath());
        } catch (Exception e) {
            throw new IOException("I/O exception - couldn't handle streams and files");
        }
    }

    private void compareCSVFiles(String actualPath, String expectedPath) {
        try (CSVParser parserTranslatedFile = createCsvParser(actualPath, ",")) {
            try (CSVParser parserExpectedFile = createCsvParser(expectedPath, ",")) {
                Iterator<CSVRecord> translatedIterator = parserTranslatedFile.iterator();
                Iterator<CSVRecord> expectedIterator = parserExpectedFile.iterator();
                while (translatedIterator.hasNext() && expectedIterator.hasNext()) {
                    CSVRecord translatedRecord = translatedIterator.next();
                    CSVRecord expectedRecord = expectedIterator.next();
                    compareCSVRecords(translatedRecord, expectedRecord);
                }
                if (translatedIterator.hasNext()) {
                    fail("Actual file is longer (has more lines) than expected file");
                }
                if (expectedIterator.hasNext()) {
                    fail("Expected file is longer (has more lines) than actual file");
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

    private CSVParser createCsvParser(String inputFileName, String delimiter) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(new FileInputStream(inputFileName)), Charsets.UTF_8));
        CSVFormat format = CSVFormat.newFormat(delimiter.charAt(0))
                .withSkipHeaderRecord()
                .withIgnoreEmptyLines()
                .withAllowMissingColumnNames()
                .withQuote('"')
                .withHeader();
        return new CSVParser(reader, format);
    }
}