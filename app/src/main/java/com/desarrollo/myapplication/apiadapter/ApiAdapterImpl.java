package com.desarrollo.myapplication.apiadapter;

import android.graphics.Bitmap;

import com.elo.device.DeviceManager;
import com.elo.device.exceptions.UnsupportedPeripheralMethodException;
import com.elo.device.peripherals.BarCodeReader.BarcodeReadCallback;

import java.io.IOException;

/**
 * Created by elo on 9/8/17.
 *
 * The implementation of the API adapter.  It consists of delegates that invoke the per-peripheral
 * methods.
 *
 * Note that the constructor of this class is package-private.  This implementation should only be
 * created by the factory class.  The factory will set appropriate delegate classes; these setters
 * are also package-private.
 */

public class ApiAdapterImpl implements ApiAdapter {

    private DeviceManager deviceManager;

    // The Activity Monitor, if it's needed
    private ActivityMonitor activityMonitor;

    // Delegates for peripheral-specific adapters
    private CashDrawerAdapter cashDrawerAdapter;
    private PrinterAdapter printerAdapter;
    private CfdAdapter cfdAdapter;
    private BarCodeReaderAdapter barCodeReaderAdapter;
    private FtdiAdapter ftdiAdapter;
    private MsrAdapter msrAdapter;

    ApiAdapterImpl() {
    }

    public ActivityMonitor getActivityMonitor() {
        return activityMonitor;
    }

    void setActivityMonitor(ActivityMonitor activityMonitor) {
        this.activityMonitor = activityMonitor;
    }

    /*
     *  CashDrawer delegate and setter methods
     */
    void setCashDrawerAdapter(CashDrawerAdapter cashDrawerAdapter) {
        this.cashDrawerAdapter = cashDrawerAdapter;
    }

    @Override
    public boolean isCashDrawerOpen() {
        return cashDrawerAdapter.isCashDrawerOpen();
    }

    @Override
    public void openCashDrawer() {
        cashDrawerAdapter.openCashDrawer();
    }

    /*
     *  Printer delegate and setter methods
     */
    void setPrinterAdapter(PrinterAdapter printerAdapter) {
        this.printerAdapter = printerAdapter;
    }

    @Override
    public boolean hasPaper() throws IOException {
        return printerAdapter.hasPaper();
    }

    @Override
    public void print(String stringData) throws IOException {
        printerAdapter.print(stringData);
    }

    @Override
    public void print(byte[] byteData) throws IOException {
        printerAdapter.print(byteData);
    }

    @Override
    public void cutPaper() throws IOException {
        printerAdapter.cutPaper();
    }

    @Override
    public void printDemo() throws IOException {
        printerAdapter.printDemo();
    }

    @Override
    public void printImage(Bitmap bmp, int offsetX, int offsetY, int imageHeight, int imageWidth) throws IOException {
        printerAdapter.printImage(bmp, offsetX, offsetY, imageHeight, imageWidth);
    }

    @Override
    public void printBarcode(String code, boolean withHRI, int widthInDots, int heightInDots) throws IOException {
        printerAdapter.printBarcode(code, withHRI, widthInDots, heightInDots);
    }

    @Override
    public void printBarcode(String code) throws IOException {
        printerAdapter.printBarcode(code);
    }

    @Override
    public void setChineseMode(boolean enable) throws UnsupportedPeripheralMethodException {
        printerAdapter.setChineseMode(enable);
    }

    @Override
    public void setBaudRate(int baudRate) throws UnsupportedPeripheralMethodException {
        printerAdapter.setBaudRate(baudRate);
    }

    /*
     *  CFD delegate and setter methods
     */
    void setCfdAdapter(CfdAdapter cfdAdapter) {
        this.cfdAdapter = cfdAdapter;
    }

    @Override
    public void cfdSetBacklight(boolean on) {
        cfdAdapter.cfdSetBacklight(on);
    }

    @Override
    public void cfdClear() {
        cfdAdapter.cfdClear();
    }

    @Override
    public void cfdSetLine(int line, String text) {
        cfdAdapter.cfdSetLine(line, text);
    }

    /*
     *  Barcode Scanner delegate and setter methods
     */
    void setBarCodeReaderAdapter(BarCodeReaderAdapter barCodeReaderAdapter) {
        this.barCodeReaderAdapter = barCodeReaderAdapter;
    }

    @Override
    public boolean isBarCodeReaderEnabled() {
        return barCodeReaderAdapter.isBarCodeReaderEnabled();
    }

    @Override
    public void setBarCodeReaderEnabled(boolean enabled) {
        barCodeReaderAdapter.setBarCodeReaderEnabled(enabled);
    }

    @Override
    public boolean isBarCodeReaderKbdMode() {
        return barCodeReaderAdapter.isBarCodeReaderKbdMode();
    }

    @Override
    public void setBarCodeReaderKbdMode() {
        barCodeReaderAdapter.setBarCodeReaderKbdMode();
    }

    @Override
    public void setBarCodeReaderCallback(BarcodeReadCallback callback) {
        barCodeReaderAdapter.setBarCodeReaderCallback(callback);
    }

    @Override
    public byte[] sendCommand(byte[] cmd) {
        return barCodeReaderAdapter.sendCommand(cmd);
    }

    /*
     *  FTDI delegate and setter methods.
     *  TODO:  This is temporary, because the methods should do more high-level work
     */
    void setFtdiAdapter(FtdiAdapter ftdiAdapter) {
        this.ftdiAdapter = ftdiAdapter;
    }

    @Override
    public String[] getFtdiDevicesList() {
        return ftdiAdapter.getFtdiDevicesList();
    }

    @Override
    public int ftdiOpen(String path, int baudrate, int flags) {
        return ftdiAdapter.ftdiOpen(path, baudrate, flags);
    }

    @Override
    public void ftdiClose(int fd) {
        ftdiAdapter.ftdiClose(fd);
    }

    @Override
    public int ftdiRead(int fd, byte[] data) {
        return ftdiAdapter.ftdiRead(fd, data);
    }

    @Override
    public void ftdiWrite(int fd, byte[] data, int length) {
        ftdiAdapter.ftdiWrite(fd, data, length);
    }

    /*
     *  MSR delegate and setter methods
     */
    void setMSRAdapter(MsrAdapter msrAdapter) {
        this.msrAdapter = msrAdapter;
    }

    @Override
    public void startMSR(MsrCallback callback) {
        msrAdapter.startMSR(callback);
    }

    @Override
    public void stopMSR() {
        msrAdapter.stopMSR();
    }

    @Override
    public boolean isMsrInKbMode() {
        return msrAdapter.isMsrInKbMode();
    }

    @Override
    public boolean isMsrInHidMode() {
        return msrAdapter.isMsrInHidMode();
    }

    @Override
    public void setMsrKbMode() {
        msrAdapter.setMsrKbMode();
    }

    @Override
    public void setMsrHidMode() {
        msrAdapter.setMsrHidMode();
    }
}
