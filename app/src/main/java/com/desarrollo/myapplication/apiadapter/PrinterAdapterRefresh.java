package com.desarrollo.myapplication.apiadapter;

import android.graphics.Bitmap;

import com.desarrollo.myapplication.apiadapter.rongta.RongtaPrinterHelper;
import com.elo.device.DeviceManager;
import com.elo.device.exceptions.UnsupportedPeripheralMethodException;
import com.elo.device.peripherals.Printer;

import java.io.IOException;

/**
 * Created by elo on 9/8/17.
 */

public class PrinterAdapterRefresh implements PrinterAdapter {
    private static final String TAG = "PrinterAdapterRefresh";

    private Printer printer;
    public final int BARCODE_WIDTH = 2;
    public final int BARCODE_HEIGHT = 50;

    /**
     *  Package-private constructor
     */
    PrinterAdapterRefresh(DeviceManager deviceManager) {
        this.printer = deviceManager.getPrinter();
    }

    @Override
    public boolean hasPaper() throws IOException {
        return printer.hasPaper();
    }

    @Override
    public void print(String stringData) throws IOException {
        printer.print(stringData);
    }

    @Override
    public void print(byte[] byteData) throws IOException {
        print(new String(byteData));
    }

    @Override
    public void cutPaper() throws IOException {

    }

    @Override
    public void printDemo() throws IOException {
        print(RongtaPrinterHelper.getTestdata());
        print(RongtaPrinterHelper.ESC_ALIGN_LEFT);
    }

    @Override
    public void printImage(Bitmap bmp, int offsetX, int offsetY, int imageHeight, int imageWidth) throws IOException {
        printer.printImage(bmp, offsetX, offsetY, imageHeight, imageWidth);
    }

    @Override
    public void printBarcode(String code, boolean withHRI, int widthInDots, int heightInDots) throws IOException {
        printer.printBarcode(code, withHRI, widthInDots, heightInDots);
    }

    @Override
    public void printBarcode(String code) throws IOException {
        this.printBarcode(code, true, BARCODE_WIDTH, BARCODE_HEIGHT);
    }

    @Override
    public void setChineseMode(boolean enable) throws UnsupportedPeripheralMethodException {
        printer.setChineseMode(enable);
    }

    @Override
    public void setBaudRate(int baudRate) throws UnsupportedPeripheralMethodException {
        printer.setBaudRate(baudRate);
    }
}
