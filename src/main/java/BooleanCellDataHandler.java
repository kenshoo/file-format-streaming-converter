import com.monitorjbl.xlsx.impl.StreamingCell;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: royh
 * Date: 06/04/2017
 * Time: 18:13
 */
@Component
public class BooleanCellDataHandler implements CellDataHandler {

    @Override
    public boolean isMatchingCellType(StreamingCell cell) {
        return cell.getCellType() == Cell.CELL_TYPE_BOOLEAN;
    }

    @Override
    public String handleCell(StreamingCell cell) {
        String rawValue = (String) cell.getRawContents();
       return rawValue.equals("1") ? "true" : "false";
    }


}
