package com.desarrollo.myapplication.apiadapter;

import android.graphics.Bitmap;
import android.util.Log;

import com.desarrollo.myapplication.apiadapter.rongta.RongtaPrinterHelper;
import com.desarrollo.myapplication.register.printer.SerialPort;
import com.elo.device.exceptions.UnsupportedPeripheralMethodException;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by elo on 14/9/17.
 */

public class PrinterAdapter1_0 implements PrinterAdapter {

    private SerialPort serialPort;
    private static final String SERIAL_OPEN_ERROR = "Failed to open printer serial port";
    public final int BARCODE_WIDTH = 2;
    public final int BARCODE_HEIGHT = 50;

    PrinterAdapter1_0() {
        try {
            serialPort = new SerialPort(new File("/dev/ttymxc1"), 9600, 0);
        } catch (IOException e) {
            // Do nothing in constructor.  If we failed to open the serial port,
            // throw an exception when a printer method is called
        }
    }

    @Override
    public boolean hasPaper() throws IOException {
        if (serialPort == null) {
            throw new IOException(SERIAL_OPEN_ERROR);
        }
        InputStream iStream = serialPort.getInputStream();

        byte[] command = {0x10, 0x04, 0x04};
        print(command);
        String result = Integer.toHexString(iStream.read());
        return result.contains("12");
    }

    @Override
    public void print(String stringData) throws IOException {
        print(stringData.getBytes());
    }

    @Override
    public void print(byte[] byteData) throws IOException {
        if (serialPort == null) {
            throw new IOException(SERIAL_OPEN_ERROR);
        }
        OutputStream oStream = serialPort.getOutputStream();
        oStream.write(byteData, 0, byteData.length);
        oStream.flush();
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
        byte[] printFormat = new byte[]{0x1B, 0x21, 0x03};
        byte[] command = RongtaPrinterHelper.generatePrintImageCommands(bmp);

        // send everything to printer
        print(printFormat);
        print(RongtaPrinterHelper.ESC_ALIGN_LEFT);
        print(command);
    }

    @Override
    public void printBarcode(String barCode, boolean withHRI, int widthInDots, int heightInDots) throws IOException {
        String cmd = RongtaPrinterHelper.generateBarcodeCommands(barCode, withHRI, widthInDots, heightInDots);
        print(cmd);
    }

    @Override
    public void printBarcode(String code) throws IOException {
        this.printBarcode(code, true, BARCODE_WIDTH, BARCODE_HEIGHT);
    }

    @Override
  public void setChineseMode(boolean enable) throws UnsupportedPeripheralMethodException {
    try {
      if (enable) {
        byte[] command = {0x1C, 0x26};
        print(command);
      } else {
        byte[] command = {0x1C, 0x2E};
        print(command);
      }
    } catch (IOException e) {
      Log.e("miamitestapp",e.toString());
    }
  }

    @Override
    public void setBaudRate(int baudRate) throws UnsupportedPeripheralMethodException {

    }
}
