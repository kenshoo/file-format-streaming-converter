import com.monitorjbl.xlsx.impl.StreamingCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: royh
 * Date: 06/04/2017
 * Time: 17:45
 */
@Service
public class CellHandler {

    @Autowired
    private List<CellDataHandler> cellDataHandlers;


    public String getDataFromCell(StreamingCell cell){
        return cell == null ? "" :
                cellDataHandlers.stream().filter(cellDataHandler -> cellDataHandler.isMatchingCellType(cell))
                        .findFirst()
                        .map(cellDataHandler -> cellDataHandler.handleCell(cell)).orElse(getDefaultData(cell));
    }

    private String getDefaultData(StreamingCell cell) {
        return (String) cell.getRawContents();
    }

}
