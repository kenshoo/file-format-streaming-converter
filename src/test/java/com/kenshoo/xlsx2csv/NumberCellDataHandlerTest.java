package com.kenshoo.xlsx2csv;

import com.monitorjbl.xlsx.impl.StreamingCell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by shachafa on 19/07/2017
 */
@RunWith(MockitoJUnitRunner.class)
public class NumberCellDataHandlerTest {

    @Mock
    private StreamingCell streamingCell;

    @InjectMocks
    private NumberCellDataHandler numberCellDataHandler;

    @Test
    public void testValidDate() {
        when(streamingCell.getNumericCellValue()).thenReturn(13.6);
        assertThat(numberCellDataHandler.handleCell(streamingCell), is("13.6"));
    }
}