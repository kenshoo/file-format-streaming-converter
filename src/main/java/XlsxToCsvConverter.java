import java.io.File;
import java.io.IOException;

/**
 * Created by shachafa on 19/06/2017
 */
public interface XlsxToCsvConverter {
    /**
     *
     * @param xlsxFormattedFilePath the path of the xlsx file
     * it will convert to csv file with comma as delimiter
     * @return the new csv file
     * @throws IOException
     */
    File convert(String xlsxFormattedFilePath) throws IOException;

    /**
     *
     * @param xlsxFormattedFilePath the path of the xlsx file
     * it will convert to csv file
     * @param delimiter the delimiter of the file
     * @return the new csv file
     * @throws IOException
     */
    File convert(String xlsxFormattedFilePath, String delimiter) throws IOException;
}
