package com.desarrollo.myapplication.apiadapter;

import com.elo.device.peripherals.BarCodeReader.BarcodeReadCallback;

/**
 * Created by elo on 9/11/17.
 */

public interface BarCodeReaderAdapter {
    public boolean isBarCodeReaderEnabled();

    public void setBarCodeReaderEnabled(boolean enabled);

    public boolean isBarCodeReaderKbdMode();

    public void setBarCodeReaderKbdMode();

    public void setBarCodeReaderCallback(BarcodeReadCallback callback);

    public byte[] sendCommand(byte[] cmd);
}
