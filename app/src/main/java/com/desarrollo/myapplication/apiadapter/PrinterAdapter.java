package com.desarrollo.myapplication.apiadapter;

import android.graphics.Bitmap;

import com.elo.device.exceptions.UnsupportedPeripheralMethodException;

import java.io.IOException;

/**
 * Created by elo on 9/8/17.
 */

public interface PrinterAdapter {

    public boolean hasPaper() throws IOException;

    // Caller can use either of these methods.  If the wrong method is used for the underlying
    // hardware, the adapter should convert the data to the other type.  But this way, if the
    // native type is known ahead of time, we can avoid unnecessary costly conversion.
    public void print(String stringData) throws IOException;    // prints unformatted text
    public void print(byte[] byteData) throws IOException;      // prints unformatted text
    public void cutPaper() throws IOException;
    public void printDemo() throws IOException;                 // prints demo receipt with native formatting
    public void printImage(Bitmap bmp, int offsetX, int offsetY, int imageHeight, int imageWidth) throws IOException; //prints demo bitmap image
    //TODO: To implement 2D barcode support
    public void printBarcode(String code, boolean withHRI, int widthInDots, int heightInDots) throws IOException; //prints barcode
    public void printBarcode(String code) throws IOException; //prints barcode with default settings
    public void setChineseMode(boolean enable) throws UnsupportedPeripheralMethodException;
    public void setBaudRate(int baudRate) throws UnsupportedPeripheralMethodException;
}
