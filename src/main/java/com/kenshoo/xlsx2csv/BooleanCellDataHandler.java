package com.kenshoo.xlsx2csv;

import com.monitorjbl.xlsx.impl.StreamingCell;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Created with IntelliJ IDEA.
 * User: Shachaf ashkenazi
 * Date: 18/07/2017
 */
class BooleanCellDataHandler implements CellDataHandler {

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
