package com.kenshoo.xlsx2csv;

/**
 * Created by shachafa on 20/08/2017
 */
public class NoSheetFoundException extends Exception {

    public NoSheetFoundException() {
        super("Couldn't find a sheet in the xlsx file.");
    }

}
