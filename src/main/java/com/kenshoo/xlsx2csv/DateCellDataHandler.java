package com.kenshoo.xlsx2csv;

import com.monitorjbl.xlsx.impl.StreamingCell;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Shachaf ashkenazi
 * Date: 18/07/2017
 */
public class DateCellDataHandler implements CellDataHandler {

    private static final Logger logger = LoggerFactory.getLogger(DateCellDataHandler.class);

    @Override
    public boolean isMatchingCellType(StreamingCell cell) {
        return DateUtil.isADateFormat(cell.getNumericFormatIndex(), cell.getNumericFormat());
    }

    @Override
    public String handleCell(StreamingCell cell) {
        String rawContents = (String) cell.getRawContents();
        if (NumberUtils.isNumber(rawContents)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date dateCellValue = cell.getDateCellValue();
                return dateFormat.format(dateCellValue);
            } catch (Exception e) {
                logger.warn("Tried to convert date " + rawContents + "but failed. skipping conversion");
            }
        }
        return rawContents;
    }


}
