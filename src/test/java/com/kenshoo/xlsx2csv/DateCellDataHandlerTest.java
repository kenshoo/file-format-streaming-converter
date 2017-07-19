package com.kenshoo.xlsx2csv;

import com.monitorjbl.xlsx.impl.StreamingCell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Date;
import java.time.LocalDate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by shachafa on 19/07/2017
 */
@RunWith(MockitoJUnitRunner.class)
public class DateCellDataHandlerTest {

    @Mock
    private StreamingCell streamingCell;

    @InjectMocks
    private DateCellDataHandler dateCellDataHandler;

    @Test
    public void testValidDate() {
        when(streamingCell.getDateCellValue()).thenReturn(Date.valueOf(LocalDate.of(2017, 7, 15)));
        testDateCellHandler("123", "2017-07-15 00:00:00");
    }

    private void testDateCellHandler(String value, String expectedResult) {
        when(streamingCell.getRawContents()).thenReturn(value);
        assertThat(dateCellDataHandler.handleCell(streamingCell), is(expectedResult));
    }

    @Test
    public void testDateCellContentIsNotANumber() {
        testDateCellHandler("not a number", "not a number");
    }

    @Test
    public void testContentIsANumberButFormatFails() {
        when(streamingCell.getDateCellValue()).thenThrow(NumberFormatException.class);
        testDateCellHandler("123", "123");
    }

}