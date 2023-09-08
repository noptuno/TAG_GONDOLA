package com.desarrollo.myapplication.apiadapter;

import com.elo.device.DeviceManager;

/**
 * I-Series adapter for the Honeywell scanner.  Since the scanner on PayPoint 2.0 is USB connected
 * and doesn't use any special hardware features, the adapter is identical.
 *
 * Created by elo on 11/10/17.
 */

public class BarCodeReaderAdapterISeriesHoneywell extends BarCodeReaderAdapter2_0 {

    public BarCodeReaderAdapterISeriesHoneywell(DeviceManager deviceManager, CommonUtil2_0 commonUtil2_0, ActivityMonitor activityMonitor) {
        super(deviceManager, commonUtil2_0, activityMonitor);
    }
}
