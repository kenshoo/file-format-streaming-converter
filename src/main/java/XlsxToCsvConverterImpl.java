import com.google.common.base.Charsets;
import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.impl.StreamingCell;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Created by shachafa on 19/06/2017
 */
public class XlsxToCsvConverterImpl implements XlsxToCsvConverter {

    private static final Logger logger = LoggerFactory.getLogger(XlsxToCsvConverterImpl.class);
    private static final String XLSX_EXTENSION = ".xlsx";
    private static final String CSV_EXTENSION = ".csv";
    private static final String COMMA_DELIMITER = ",";
    private final static char QUOTE = '"';

    @Resource
    private CellHandler cellHandler;

    @Override
    public File convert(String xlsxFormattedFilePath) throws IOException {
        return convert(xlsxFormattedFilePath, COMMA_DELIMITER);
    }

    @Override
    public File convert(String xlsxFormattedFilePath, String delimiter) throws IOException {
        File inputFile = new File(xlsxFormattedFilePath);
        String finalPath = replaceFileExtensions(xlsxFormattedFilePath, XLSX_EXTENSION, CSV_EXTENSION);
        parseExcel(inputFile, finalPath, delimiter);
        return new File(finalPath);
    }

    private void parseExcel(File inputFile, String outputFileName, String delimiter) throws IOException {
        try (InputStream inputStream = new FileInputStream(inputFile)) {
            Iterator<Row> rowIterator = getRowIterator(inputStream);
            if (!rowIterator.hasNext()) {
                logger.error("File is empty, exiting converter");
            } else {
                List<String> headers = getRowAsList(rowIterator.next());
                int rowSize = headers.size();
                try (CSVPrinter csvPrinter = createCSVPrinter(headers.toArray(new String[headers.size()]), delimiter, outputFileName, false)) {
                    writeRowsToFile(rowIterator, rowSize, csvPrinter);
                }
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

    private Iterator<Row> getRowIterator(InputStream is) {
        Workbook workbook;
        Sheet sheet;
        Iterator<Row> rowIterator;
        workbook = StreamingReader.builder()
                .rowCacheSize(10)
                .bufferSize(1024)
                .open(is);
        sheet = workbook.getSheetAt(0);
        rowIterator = sheet.rowIterator();
        return rowIterator;
    }

    private List<String> getRowAsList(  Row row, int rowSize) {
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


    private String replaceFileExtensions(String fileName, String from, String to) {
        int lastIndex = fileName.lastIndexOf(from);
        if (lastIndex < 0) {
            return fileName;
        }
        String tail = fileName.substring(lastIndex).replaceFirst(from, to);
        return fileName.substring(0, lastIndex) + tail;
    }

    private CSVPrinter createCSVPrinter(String[] headers, String delimiter, String outputFileName, boolean append) throws IOException {
        String[] fixedHeaders = getHeadersIfFileNotExist(headers, outputFileName, append);
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName, append), Charsets.UTF_8));
        CSVFormat format = CSVFormat.newFormat(delimiter.charAt(0))
                .withHeader((String[]) fixedHeaders)
                .withQuote(QUOTE)
                .withRecordSeparator(System.lineSeparator());
        return new CSVPrinter(writer, format);
    }

    private String[] getHeadersIfFileNotExist(String[] headers, String outputFileName, boolean append) {
        if (!append) {
            return headers;
        }
        File file = new File(outputFileName);
        return file.exists() ? null : headers;
    }
}
