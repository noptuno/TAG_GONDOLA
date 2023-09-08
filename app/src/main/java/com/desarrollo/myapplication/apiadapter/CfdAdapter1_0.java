package com.desarrollo.myapplication.apiadapter;

import com.desarrollo.myapplication.register.cfd.CFD;


/**
 * Created by elo on 14/9/17.
 */

public class CfdAdapter1_0 implements CfdAdapter {

    private CFD cfd;

    CfdAdapter1_0() {
        this.cfd = new CFD();
    }

    @Override
    public void cfdSetBacklight(boolean on) {
        cfd.setBacklight(on);
    }

    @Override
    public void cfdClear() {
        cfd.clearDisplay();
    }

    @Override
    public void cfdSetLine(int line, String text) {
        if (line == 1)
            cfd.setLine1(text);
        else
            cfd.setLine2(text);
    }
}
