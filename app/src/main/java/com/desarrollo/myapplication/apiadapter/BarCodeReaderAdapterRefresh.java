package com.desarrollo.myapplication.apiadapter;

import com.elo.device.DeviceManager;
import com.elo.device.enums.Status;
import com.elo.device.peripherals.BarCodeReader;

/**
 * Created by elo on 9/11/17.
 */

public class BarCodeReaderAdapterRefresh implements BarCodeReaderAdapter {

    BarCodeReader barCodeReader;

    BarCodeReaderAdapterRefresh(DeviceManager deviceManager) {
        this.barCodeReader = deviceManager.getBarCodeReader();
    }

    @Override
    public boolean isBarCodeReaderEnabled() {
        return barCodeReader.getStatus() == Status.ENABLED;
    }

    @Override
    public void setBarCodeReaderEnabled(boolean enabled) {
        barCodeReader.setEnabled(enabled);
    }

    @Override
    public boolean isBarCodeReaderKbdMode() {
        return true;    // Numa reader is always in keyboard mode
    }

    @Override
    public void setBarCodeReaderKbdMode() {
        // No-op
    }

    @Override
    public void setBarCodeReaderCallback(BarCodeReader.BarcodeReadCallback callback) {

    }

    @Override
    public byte[] sendCommand(byte[] cmd) {
        return new byte[0];
    }
}
