import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;

/**
 * Created by shachafa on 20/02/2017
 */

public interface CSVPrinterService {

    CSVPrinter createCSVPrinter(String[] headers, String delimiter, String outputFileName, boolean append) throws IOException;

    CSVParser createCsvParser(String inputFileName, String delimiter) throws IOException;

}
