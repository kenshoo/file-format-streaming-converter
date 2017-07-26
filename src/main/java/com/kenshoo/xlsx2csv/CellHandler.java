package com.kenshoo.xlsx2csv;

import com.google.common.collect.Lists;
import com.monitorjbl.xlsx.impl.StreamingCell;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Shachaf ashkenazi
 * Date: 18/07/2017
 */
public class CellHandler {

    private List<CellDataHandler> cellDataHandlers = Lists.newArrayList();

    public CellHandler() {
        cellDataHandlers.add(new BooleanCellDataHandler());
        cellDataHandlers.add(new DateCellDataHandler());
        cellDataHandlers.add(new NumberCellDataHandler());
    }

    public String getDataFromCell(StreamingCell cell){
        return cell == null ? "" :
                cellDataHandlers.stream().filter(cellDataHandler -> cellDataHandler.isMatchingCellType(cell))
                        .findFirst()
                        .map(cellDataHandler -> cellDataHandler.handleCell(cell))
                        .orElse(getDefaultData(cell));
    }

    private String getDefaultData(StreamingCell cell) {
        return (String) cell.getRawContents();
    }

}
