package com.desarrollo.myapplication.apiadapter;

import com.elo.device.peripherals.BarCodeReader;

/**
 * Created by elo on 14/9/17.
 */

public class BarCodeReaderAdapterISeriesNuma implements BarCodeReaderAdapter {

    @Override
    public boolean isBarCodeReaderEnabled() {
        // On I-Series, there's no on-off control of the scanner
        return true;
    }

    @Override
    public void setBarCodeReaderEnabled(boolean enabled) {
    }

    @Override
    public boolean isBarCodeReaderKbdMode() {
        // Numa device is always in keyboard mode
        return true;
    }

    @Override
    public void setBarCodeReaderKbdMode() {
        // Numa device is always in keyboard mode
    }

    @Override
    public void setBarCodeReaderCallback(BarCodeReader.BarcodeReadCallback callback) {

    }

    @Override
    public byte[] sendCommand(byte[] cmd) {
        return new byte[0];
    }
}
