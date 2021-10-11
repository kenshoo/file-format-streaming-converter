package com.kenshoo.xlsx2csv;

import com.monitorjbl.xlsx.impl.StreamingCell;

/**
 * Created with IntelliJ IDEA.
 * User: Shachaf ashkenazi
 * Date: 18/07/2017
 */
public interface CellDataHandler {

    guruboolean isMatchingCellType(StreamingCell cell);

    String handleCell(StreamingCell cell);

}
