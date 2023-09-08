package com.desarrollo.myapplication.apiadapter;


/**
 * Created by elo on 9/11/17.
 */

public interface CfdAdapter {
    public void cfdSetBacklight(boolean on);

    public void cfdClear();

    public void cfdSetLine(int line, String text);

}
