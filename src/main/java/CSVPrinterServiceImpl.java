import com.google.common.base.Charsets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by shachafa on 20/02/2017
 */

@Service("csvPrinterService")
public class CSVPrinterServiceImpl implements CSVPrinterService {
    private final static char QUOTE = '"';

    @Override
    public CSVPrinter createCSVPrinter(String[] headers, String delimiter, String outputFileName, boolean append) throws IOException {
        String[] fixedHeaders = getHeadersIfFileNotExist(headers, outputFileName, append);
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName, append), Charsets.UTF_8));

        CSVFormat format = CSVFormat.newFormat(delimiter.charAt(0))
                .withHeader((String[]) fixedHeaders)
                .withQuote(QUOTE)
                .withRecordSeparator(System.lineSeparator());
        return new CSVPrinter(writer, format);
    }

    @Override
    public CSVParser createCsvParser(String inputFileName, String delimiter) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(new FileInputStream(inputFileName)), Charsets.UTF_8));
        CSVFormat format = CSVFormat.newFormat(delimiter.charAt(0))
                .withSkipHeaderRecord()
                .withIgnoreEmptyLines()
                .withAllowMissingColumnNames()
                .withQuote(QUOTE)
                .withHeader();
        return new CSVParser(reader, format);
    }

    private String[] getHeadersIfFileNotExist(String[] headers, String outputFileName, boolean append) {
        if (!append) {
            return headers;
        }
        File file = new File(outputFileName);
        return file.exists() ? null : headers;
    }
}
