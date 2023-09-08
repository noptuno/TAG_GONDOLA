package com.desarrollo.myapplication.apiadapter;

import com.elo.device.DeviceManager;
import com.elo.device.peripherals.FTDI;

/**
 * Created by elo on 9/11/17.
 */

public class FtdiAdapterCommon implements FtdiAdapter {

    private FTDI ftdi;

    FtdiAdapterCommon(DeviceManager deviceManager) {
        ftdi = deviceManager.getFTDI();
    }

    @Override
    public String[] getFtdiDevicesList() {
        return ftdi.getDevicesList();
    }

    @Override
    public int ftdiOpen(String path, int baudrate, int flags) {
        return ftdi.open(path, baudrate, flags);
    }

    @Override
    public void ftdiClose(int fd) {
        ftdi.close(fd);
    }

    @Override
    public int ftdiRead(int fd, byte[] data) {
        return ftdi.read(fd, data);
    }

    @Override
    public void ftdiWrite(int fd, byte[] data, int length) {
        ftdi.write(fd, data, length);
    }
}
