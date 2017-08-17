package com.kenshoo.xlsx2csv;

import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.impl.StreamingCell;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Created with IntelliJ IDEA.
 * User: Shachaf ashkenazi
 * Date: 18/07/2017
 */
public class XlsxToCsvConverter {

    private static final Logger logger = LoggerFactory.getLogger(XlsxToCsvConverter.class);
    private final static char QUOTE = '"';
    private static final String COMMA = ",";
    private static final int DEFAULT_ROW_CACHE_SIZE = 10;
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private final String delimiter;
    private final int rowCacheSize;
    private final int bufferSize;

    private final CellHandler cellHandler = new CellHandler();

    /**
     * @param delimiter - a delimiter used to differentiate values in the translated file.
     * @param rowCacheSize - cache size of rows to save in memory (for streaming purposes).
     * @param bufferSize - buffer size to keep in memory (for streaming purposes).
     */
    private XlsxToCsvConverter(String delimiter, int rowCacheSize, int bufferSize) {
        this.delimiter = delimiter;
        this.rowCacheSize = rowCacheSize;
        this.bufferSize = bufferSize;
    }

    /**
     * @param inputStream      - an xlsx file as source input stream.
     * @param fileOutputStream - the OutputStream to write the translated file to.
     * @throws IOException if an I/O error occurs.
     * @throws NoSheetFoundException - if no sheets were found in the file at all.
     * @throws NoDataFoundException - if a non hidden sheet was found but it contained no data at all.
     */
    public void convert(InputStream inputStream, FileOutputStream fileOutputStream) throws Exception {
        final Optional<Iterator<Row>> optionalRowIterator = getRowIterator(inputStream);
        Iterator<Row> rowIterator = optionalRowIterator.orElseThrow(NoSheetFoundException::new);
        try {
            List<String> headers = getRowAsList(rowIterator.next());
            final int rowSize = headers.size();
            try (CSVPrinter csvPrinter = createCSVPrinter(headers.toArray(new String[headers.size()]), delimiter, fileOutputStream)) {
                writeRowsToFile(rowIterator, rowSize, csvPrinter);
            }
        } catch (NoSuchElementException e) {
            logger.error("File is empty, exiting converter");
            throw new NoDataFoundException();
        }
    }

    private void writeRowsToFile(Iterator<Row> rowIterator, int rowSize, CSVPrinter csvPrinter) throws IOException {
        while (rowIterator.hasNext()) {
            final Row next = rowIterator.next();
            List<String> rowAsList = getRowAsList(next, rowSize);
            if (isLastLine(rowIterator.hasNext()) && !hasDataInLine(rowAsList)) {
                continue;
            }
            csvPrinter.printRecord(rowAsList);
        }
    }

    private boolean hasDataInLine(List<String> rowAsList) {
        return rowAsList.stream().anyMatch(StringUtils::isNotEmpty);
    }

    private boolean isLastLine(boolean hasNext) {
        return !hasNext;
    }

    private Optional<Iterator<Row>> getRowIterator(InputStream inputStream) {
        final Workbook workbook = StreamingReader.builder()
                .rowCacheSize(rowCacheSize)
                .bufferSize(bufferSize)
                .open(inputStream);
        return getFirstVisibleSheet(workbook).map(Sheet::rowIterator);
    }

    private Optional<Sheet> getFirstVisibleSheet(Workbook workbook) {
        OptionalInt firstIndex = IntStream.range(0, workbook.getNumberOfSheets())
                .filter(i -> !isSheetHidden(i, workbook))
                .findFirst();
        return firstIndex.isPresent() ? Optional.of(workbook.getSheetAt(firstIndex.getAsInt())) : Optional.empty();
    }

    private boolean isSheetHidden(int sheetInd, Workbook workbook) {
        return workbook.isSheetHidden(sheetInd) || workbook.isSheetVeryHidden(sheetInd);
    }

    private List<String> getRowAsList(Row row, int rowSize) {
        return IntStream.range(0, rowSize)
                .mapToObj(index -> getCellValue(row, index))
                .collect(toList());
    }

    private List<String> getRowAsList(Row row) {
        return getRowAsList(row, row.getLastCellNum());
    }

    private String getCellValue(Row row, int index) {
        final Cell cell = row.getCell(index);
        return cellHandler.getDataFromCell((StreamingCell) cell);
    }

    private CSVPrinter createCSVPrinter(String[] headers, String delimiter, FileOutputStream outputStream) throws IOException {
        final Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, Charsets.UTF_8));
        final CSVFormat format = CSVFormat.newFormat(delimiter.charAt(0))
                .withHeader((String[]) headers)
                .withQuote(QUOTE)
                .withRecordSeparator(System.lineSeparator());
        return new CSVPrinter(writer, format);
    }

    public static class Builder {

        private String delimiter = COMMA;
        private int rowCacheSize = DEFAULT_ROW_CACHE_SIZE;
        private int bufferSize = DEFAULT_BUFFER_SIZE;

        public Builder aConverter() {
            return new Builder();
        }

        /**
         * @param delimiter - a delimiter used to differentiate values in the translated file.
         * @return - this Builder.
         */
        public Builder withDelimiter(String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        /**
         *
         * @param rowCacheSize - cache size of rows to save in memory (for streaming purposes).
         * @return - this Builder.
         */
        public Builder withRowCacheSize(int rowCacheSize) {
            this.rowCacheSize = rowCacheSize;
            return this;
        }

        /**
         * @param bufferSize - buffer size to keep in memory (for streaming purposes).
         * @return - this Builder.
         */
        public Builder withBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        /**
         * @return - new converter with default or user's parameters.
         */
        public XlsxToCsvConverter build() {
            return new XlsxToCsvConverter(delimiter, rowCacheSize, bufferSize);
        }
    }
}
