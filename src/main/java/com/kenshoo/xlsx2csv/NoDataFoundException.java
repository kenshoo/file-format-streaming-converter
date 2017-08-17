package com.kenshoo.xlsx2csv;

/**
 * Created by shachafa on 20/08/2017
 */
public class NoDataFoundException extends Exception {

    public NoDataFoundException() {
        super("Couldn't find any data in the first non-hidden sheet in file.");
    }

}
