package com.desarrollo.myapplication.apiadapter;

import com.elo.device.DeviceManager;
import com.elo.device.peripherals.CashDrawer;

/**
 * Created by elo on 9/8/17.
 */

public class CashDrawerAdapterRefresh implements CashDrawerAdapter {

    private CashDrawer cashDrawer;

    /**
     *  Package-private constructor
     */
    CashDrawerAdapterRefresh(DeviceManager deviceManager) {
        this.cashDrawer = deviceManager.getCashDrawer();
    }

    @Override
    public boolean isCashDrawerOpen() {
        return cashDrawer.isOpen();
    }

    @Override
    public void openCashDrawer() {
        cashDrawer.open();
    }
}
