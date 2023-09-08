package com.desarrollo.myapplication.apiadapter;

import android.util.Log;

import java.io.File;
import java.util.Vector;


public class FtdiAdapter1_0 implements FtdiAdapter {
    private String TAG = "FtdiAdapter1_0";
    private static final String TTYUSB_DEV_PATH = "/dev/ttyUSB";
    private static final String DEV_FILE = "/dev";

    static {
        System.loadLibrary("Ftdi_1_0");
    }

    public static native int open(String path, int baudrate, int flag);

    public static native int write(int fd, byte[] buff, int size);

    public static native int read(int fd, byte[] buff, int size);

    public static native int close(int fd);

    FtdiAdapter1_0() {

    }

    @Override
    public String[] getFtdiDevicesList() {
        Vector<String> devices = new Vector<>();
        File dev = new File(DEV_FILE);
        File[] files = dev.listFiles();
        String devName;
        for (int i = 0; i < files.length; i++) {
            devName = files[i].getAbsolutePath();
            if (devName.startsWith(TTYUSB_DEV_PATH)) {
                Log.i(TAG, "ttyUSB_getDevicesList(), devices[" + i + "]: " + devName);
                devices.add(devName);
            }
        }
        return devices.toArray(new String[devices.size()]);
    }

    @Override
    public int ftdiOpen(String path, int baudrate, int flags) {
        return open(path,baudrate,flags);
    }

    @Override
    public void ftdiClose(int fd) {
        close(fd);
    }

    @Override
    public int ftdiRead(int fd, byte[] data) {
        return read(fd, data, data.length);
    }

    @Override
    public void ftdiWrite(int fd, byte[] data, int length) {
        int ret = write(fd, data, data.length);
        Log.i("FTDI", "write bytes : " + ret);
    }
}
