package com.desarrollo.myapplication.apiadapter;

import android.content.Context;

import com.desarrollo.myapplication.apiadapter.star.StarPrinterHelper;
import com.elo.device.DeviceManager;
import com.elo.device.peripherals.CashDrawer;

/**
 * Created by elo on 12/15/17.
 */

public class CashDrawerAdapterStar implements CashDrawerAdapter {

    protected CashDrawer cashDrawer;
    protected CommonUtil2_0 commonUtil;
    protected StarPrinterHelper starPrinterHelper;

    /**
     *  Package-private constructor
     */
    CashDrawerAdapterStar(DeviceManager deviceManager, CommonUtil2_0 commonUtil) {
        this.cashDrawer = deviceManager.getCashDrawer();
        this.commonUtil = commonUtil;

        // Get an instance of the printer helper so we can communicate to it
        Context context = commonUtil.getContext();
        String printerPortName = StarPrinterHelper.getPrinterPortName(context);
        if (printerPortName != null) {
            starPrinterHelper = StarPrinterHelper.getInstance(printerPortName, context);
        }
    }

    @Override
    public boolean isCashDrawerOpen() {
        return starPrinterHelper.getCashDrawerStatus();
    }

    @Override
    public void openCashDrawer() {
        if (!commonUtil.isStarPrinterConnected()) {
            throw new RuntimeException("Printer not connected, cannot perform operation");
        }
        starPrinterHelper.openDrawer(StarPrinterHelper.defaultSendCallback);
    }
}
