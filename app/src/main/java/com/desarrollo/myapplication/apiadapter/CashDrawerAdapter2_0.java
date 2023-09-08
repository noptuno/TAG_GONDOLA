package com.desarrollo.myapplication.apiadapter;

import com.elo.device.DeviceManager;

/**
 * Created by elo on 9/8/17.
 */

public class CashDrawerAdapter2_0 extends CashDrawerAdapterStar {

    /**
     *  Package-private constructor
     */
    CashDrawerAdapter2_0(DeviceManager deviceManager, CommonUtil2_0 commonUtil) {
        super(deviceManager, commonUtil);
    }

    @Override
    public boolean isCashDrawerOpen() {
        return cashDrawer.isOpen();
    }
}
