package com.desarrollo.myapplication.apiadapter;

import com.elo.device.DeviceManager;
import com.elo.device.peripherals.CFD;

/**
 * Created by elo on 9/11/17.
 *
 * CFD Adapter, common to both Paypoint Refresh and Paypoint 2.0
 */

public class CfdAdapterRefreshOr2_0 implements CfdAdapter {

    CFD cfd;

    CfdAdapterRefreshOr2_0(DeviceManager deviceManager) {
        this.cfd = deviceManager.getCfd();
    }

    @Override
    public void cfdSetBacklight(boolean on) {
        cfd.setBacklight(on);
    }

    @Override
    public void cfdClear() {
        cfd.clear();
    }

    @Override
    public void cfdSetLine(int line, String text) {
        cfd.setLine(line, text);
    }
}
