package com.kenshoo.xlsx2csv;

import com.monitorjbl.xlsx.impl.StreamingCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Shachaf ashkenazi
 * Date: 18/07/2017
 */
class NumberCellDataHandler implements CellDataHandler {

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
