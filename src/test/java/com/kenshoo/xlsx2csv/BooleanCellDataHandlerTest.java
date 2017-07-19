package com.kenshoo.xlsx2csv;

import com.monitorjbl.xlsx.impl.StreamingCell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by Shachaf Ashkenazi on 19/07/2017
 */
@RunWith(MockitoJUnitRunner.class)
public class BooleanCellDataHandlerTest {

    @Mock
    private StreamingCell streamingCell;
    @InjectMocks
    private BooleanCellDataHandler booleanCellDataHandler;

    @Test
    public void testHandleZeroAsFalse() {
        testBooleanCellDataHolder("0", "false");
    }

    @Test
    public void testHandleOneAsTrue() {
        testBooleanCellDataHolder("1", "true");
    }

    private void testBooleanCellDataHolder(String value, String expectedResult) {
        when(streamingCell.getRawContents()).thenReturn(value);
        assertThat(booleanCellDataHandler.handleCell(streamingCell), is(expectedResult));
    }
}