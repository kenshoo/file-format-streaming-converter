import com.monitorjbl.xlsx.impl.StreamingCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: royh
 * Date: 06/04/2017
 * Time: 18:13
 */
@Component
public class NumberCellDataHandler implements CellDataHandler {

    private static final Logger logger = LoggerFactory.getLogger(NumberCellDataHandler.class);
    private static final String DOT = ".";

    @Override
    public boolean isMatchingCellType(StreamingCell cell) {
        return cell.getCellType() == Cell.CELL_TYPE_NUMERIC && ((String)cell.getRawContents()).contains(DOT);
    }

    @Override
    public String handleCell(StreamingCell cell) {
        return NumberToTextConverter.toText(cell.getNumericCellValue());
    }


}
