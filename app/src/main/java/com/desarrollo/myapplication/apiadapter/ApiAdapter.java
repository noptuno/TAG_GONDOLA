package com.desarrollo.myapplication.apiadapter;

/**
 * Created by elo on 9/8/17.
 *
 * This is the main adapter into the Elo API.  It combines all the per-peripheral interfaces
 * into one, to be used by the main application.  It also contains a few calls that are not
 * specific to any other interface, but to the API as a whole
 */

public interface ApiAdapter extends CashDrawerAdapter, PrinterAdapter, CfdAdapter,
        BarCodeReaderAdapter, FtdiAdapter, MsrAdapter {

    // Getter for ActivityMonitor
    public ActivityMonitor getActivityMonitor();
}
