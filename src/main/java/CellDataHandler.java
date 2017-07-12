import com.monitorjbl.xlsx.impl.StreamingCell;

/**
 * Created with IntelliJ IDEA.
 * User: royh
 * Date: 06/04/2017
 * Time: 17:58
 */
public interface CellDataHandler {

    boolean isMatchingCellType(StreamingCell cell);

    String handleCell(StreamingCell cell);

}
