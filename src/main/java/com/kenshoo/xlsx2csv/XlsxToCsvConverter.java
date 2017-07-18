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
import java.util.Iterator;
import java.util.List;
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

    private String delimiter = ",";
    private CellHandler cellHandler = new CellHandler();

    /**
     * @param delimiter - a delimiter used to differentiate values in the translated file.
     */
    public XlsxToCsvConverter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * @param inputStream      - an xlsx file as source input stream.
     * @param fileOutputStream - the OutputStream to write the translated file to.
     * @throws IOException if an I/O error occurs.
     */
    public void convert(InputStream inputStream, FileOutputStream fileOutputStream) throws IOException {
        Iterator<Row> rowIterator = getRowIterator(inputStream);
        if (!rowIterator.hasNext()) {
            logger.error("File is empty, exiting converter");
        } else {
            List<String> headers = getRowAsList(rowIterator.next());
            int rowSize = headers.size();
            try (CSVPrinter csvPrinter = createCSVPrinter(headers.toArray(new String[headers.size()]), delimiter, fileOutputStream)) {
                writeRowsToFile(rowIterator, rowSize, csvPrinter);
            }
        }
    }

    private void writeRowsToFile(Iterator<Row> rowIterator, int rowSize, CSVPrinter csvPrinter) throws IOException {
        while (rowIterator.hasNext()) {
            Row next = rowIterator.next();
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

    private Iterator<Row> getRowIterator(InputStream inputStream) {
        Workbook workbook;
        Sheet sheet;
        Iterator<Row> rowIterator;
        workbook = StreamingReader.builder()
                .rowCacheSize(10)
                .bufferSize(1024)
                .open(inputStream);
        sheet = workbook.getSheetAt(0);
        rowIterator = sheet.rowIterator();
        return rowIterator;
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
        Cell cell = row.getCell(index);
        return cellHandler.getDataFromCell((StreamingCell) cell);
    }

    private CSVPrinter createCSVPrinter(String[] headers, String delimiter, FileOutputStream outputStream) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, Charsets.UTF_8));
        CSVFormat format = CSVFormat.newFormat(delimiter.charAt(0))
                .withHeader((String[]) headers)
                .withQuote(QUOTE)
                .withRecordSeparator(System.lineSeparator());
        return new CSVPrinter(writer, format);
    }
}
