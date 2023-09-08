package com.desarrollo.myapplication.apiadapter;

import android.graphics.Bitmap;

import com.elo.device.exceptions.UnsupportedPeripheralMethodException;
import com.elo.device.peripherals.BarCodeReader;

import java.io.IOException;

/**
 * This is a Null API adapter. Use it when no real adapter is found
 */

public class NullApiAdapter implements ApiAdapter {

    @Override
    public ActivityMonitor getActivityMonitor() {
        return null;
    }

    @Override
    public boolean isBarCodeReaderEnabled() {
        return false;
    }

    @Override
    public void setBarCodeReaderEnabled(boolean enabled) {

    }

    @Override
    public boolean isBarCodeReaderKbdMode() {
        return false;
    }

    @Override
    public void setBarCodeReaderKbdMode() {

    }

    @Override
    public void setBarCodeReaderCallback(BarCodeReader.BarcodeReadCallback callback) {

    }

    @Override
    public byte[] sendCommand(byte[] cmd) {
        return new byte[0];
    }

    @Override
    public boolean isCashDrawerOpen() {
        return false;
    }

    @Override
    public void openCashDrawer() {

    }

    @Override
    public void cfdSetBacklight(boolean on) {

    }

    @Override
    public void cfdClear() {

    }

    @Override
    public void cfdSetLine(int line, String text) {

    }

    @Override
    public String[] getFtdiDevicesList() {
        return new String[0];
    }

    @Override
    public int ftdiOpen(String path, int baudrate, int flags) {
        return 0;
    }

    @Override
    public void ftdiClose(int fd) {

    }

    @Override
    public int ftdiRead(int fd, byte[] data) {
        return 0;
    }

    @Override
    public void ftdiWrite(int fd, byte[] data, int length) {

    }

    @Override
    public boolean hasPaper() throws IOException {
        return false;
    }

    @Override
    public void print(String stringData) throws IOException {

    }

    @Override
    public void print(byte[] byteData) throws IOException {

    }

    @Override
    public void cutPaper() throws IOException {

    }

    @Override
    public void printDemo() throws IOException {

    }

    @Override
    public void printImage(Bitmap bmp, int offsetX, int offsetY, int imageHeight, int imageWidth) throws IOException {

    }

    @Override
    public void printBarcode(String code, boolean withHRI, int widthInDots, int heightInDots) throws IOException {

    }

    @Override
    public void printBarcode(String code) throws IOException {

    }

    @Override
    public void setChineseMode(boolean enable) throws UnsupportedPeripheralMethodException {
        throw new UnsupportedPeripheralMethodException("Printer not found", "setChineseMode", "NullPrinter");
    }

    @Override
    public void setBaudRate(int baudRate) throws UnsupportedPeripheralMethodException {

    }

    @Override
    public void startMSR(MsrCallback callback) {

    }

    @Override
    public void stopMSR() {

    }

    @Override
    public boolean isMsrInKbMode() {
        return false;
    }

    @Override
    public boolean isMsrInHidMode() {
        return false;
    }

    @Override
    public void setMsrKbMode() {

    }

    @Override
    public void setMsrHidMode() {

    }
}
