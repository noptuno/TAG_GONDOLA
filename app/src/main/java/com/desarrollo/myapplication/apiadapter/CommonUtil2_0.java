package com.desarrollo.myapplication.apiadapter;

import android.content.Context;

/**
 * Created by elo on 9/8/17.
 *
 * Utility classes common to multiple peripherals.  This should only be used when absolutely
 * necessary!  Don't dump methods in here because you can't figure out where else to put them.
 */

class CommonUtil2_0 {

    private Context context;

    private boolean isStarPrinterConnected;

    CommonUtil2_0(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    /*
     *  For 2.0, the cash drawer is controlled by the printer.  These methods assist the cash drawer
     *  handler to contact the printer.
     */
    boolean isStarPrinterConnected() {
        return isStarPrinterConnected;
    }

    void setStarPrinterConnected(boolean starPrinterConnected) {
        isStarPrinterConnected = starPrinterConnected;
    }
}
