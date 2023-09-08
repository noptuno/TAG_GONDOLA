package com.desarrollo.myapplication.apiadapter;

/**
 * Created by elo on 9/11/17.
 */

public interface FtdiAdapter {

    public String[] getFtdiDevicesList();

    public int ftdiOpen(String path, int baudrate, int flags);

    public void ftdiClose(int fd);

    public int ftdiRead(int fd, byte[] data);

    public void ftdiWrite(int fd, byte[] data, int length);
}
