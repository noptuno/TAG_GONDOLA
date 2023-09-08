package com.desarrollo.myapplication.apiadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.desarrollo.myapplication.apiadapter.star.PrintContentStar;
import com.desarrollo.myapplication.apiadapter.star.StarPrinterCallback;
import com.desarrollo.myapplication.apiadapter.star.StarPrinterHelper;
import com.elo.device.DeviceManager;
import com.elo.device.exceptions.UnsupportedPeripheralMethodException;
import com.elo.device.peripherals.Printer;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by elo on 9/8/17.
 */

public class PrinterAdapter2_0 implements PrinterAdapter, Observer {
    private static final String TAG = "PrinterAdapter2_0";

    private Printer printer;
    private CommonUtil2_0 commonUtil;
    private ActivityMonitor activityMonitor;
    private StarPrinterCallback starPrinterCallback;
    private StarPrinterHelper starPrinterHelper;
    private boolean starPrinterConnected;
    private boolean starPrinterWaitingToConnect;

    /**
     *  Package-private constructor
     */
    PrinterAdapter2_0(DeviceManager deviceManager, CommonUtil2_0 commonUtil, ActivityMonitor activityMonitor) {
        this.printer = deviceManager.getPrinter();
        this.commonUtil = commonUtil;
        this.activityMonitor = activityMonitor;
        init();
    }

    @Override
    public boolean hasPaper() throws IOException {
        if (starPrinterConnected)
            return starPrinterHelper.getPrinterPaperStatus();
        else
            return false;
    }

    @Override
    public void print(String stringData) throws IOException {
        if (!starPrinterConnected) {
            throw new RuntimeException("Printer not connected, cannot perform operation");
        }
        starPrinterHelper.print(PrintContentStar.createTextReceiptData(stringData), StarPrinterHelper.defaultSendCallback);
    }

    @Override
    public void print(byte[] byteData) throws IOException {
        print(byteData.toString());
    }

    @Override
    public void cutPaper() {
        if (!starPrinterConnected) {
            throw new RuntimeException("Printer not connected, cannot perform operation");
        }
        starPrinterHelper.cutPaper(StarPrinterHelper.defaultSendCallback);
    }

    @Override
    public void printDemo() throws IOException {
        if (!starPrinterConnected) {
            throw new RuntimeException("Printer not connected, cannot perform operation");
        }
        starPrinterHelper.print(PrintContentStar.getTestPage(), StarPrinterHelper.defaultSendCallback);
    }


    @Override
    public void setChineseMode(boolean enable) throws UnsupportedPeripheralMethodException {

    }

    @Override
    public void setBaudRate(int baudRate) throws UnsupportedPeripheralMethodException {
    }

    @Override
    public void printImage(Bitmap bmp, int offsetX, int offsetY, int imageHeight, int imageWidth) {
        starPrinterHelper.printImage(bmp, offsetX, offsetY, imageHeight, imageWidth, StarPrinterHelper.defaultSendCallback);
    }

    @Override
    public void printBarcode(String code, boolean withHRI, int widthInDots, int heightInDots) throws IOException {
        starPrinterHelper.printBarCode(code, withHRI, widthInDots, heightInDots, StarPrinterHelper.defaultSendCallback);
    }

    @Override
    public void printBarcode(String code) throws IOException {
        this.printBarcode(code, true, starPrinterHelper.BARCODE_WIDTH, starPrinterHelper.BARCODE_HEIGHT);
    }


    /**
     * We've been notified of an Activity lifecycle update.  This should only be called by an Observable
     *
     * @param observable  Observable doing the notification
     * @param o  String corresponding to event
     */
    @Override
    public void update(Observable observable, Object o) {
        // If we weren't able to find a printer, don't even bother
        if (starPrinterHelper == null) return;

        String event = (String) o;
        if (ActivityMonitor.EVENT_ON_START.equals(event)) {
            // If the printer gets disconnected then reconnected too quickly, the internal state
            // of the STAR driver gets out of sync and can lead to crashes.  Make sure the
            // disconnect fully finishes before attempting to reconnect
            if (!starPrinterConnected) {
                Log.d(TAG, "Connecting to the printer");
                starPrinterHelper.connect(starPrinterCallback);
            } else {
                Log.d(TAG, "Waiting on printer disconnect, will connect when ready");
                starPrinterWaitingToConnect = true;
            }
        } else if (ActivityMonitor.EVENT_ON_STOP.equals(event)) {
            Log.d(TAG, "Disconnecting from the printer", new Throwable());
            starPrinterHelper.disconnect(starPrinterCallback);
        }
    }

    private void init() {
        // Set the printer connect notification callback
        starPrinterCallback = new StarPrinterCallback() {
            @Override
            public void onDisconnected() {
                super.onDisconnected();
                starPrinterConnected = false;
                commonUtil.setStarPrinterConnected(false);
                if (starPrinterWaitingToConnect) {
                    Log.d(TAG, "Received disconnect, reconnecting to printer");
                    starPrinterWaitingToConnect = false;
                    starPrinterHelper.connect(starPrinterCallback);
                }
            }
            @Override
            public void onConnected(ConnectResult connectResult) {
                super.onConnected(connectResult);
                starPrinterConnected = true;
                commonUtil.setStarPrinterConnected(true);
            }
            @Override
            public void onPrinterImpossible() {
                super.onPrinterImpossible();
                Log.e(TAG, "STAR printer is being impossible today");
                starPrinterConnected = false;
                commonUtil.setStarPrinterConnected(false);
            }
        };

        // Initialize a STAR printer, if present in the system
        Context context = commonUtil.getContext();
        String printerPortName = StarPrinterHelper.getPrinterPortName(context);
        if (printerPortName != null) {
            starPrinterHelper = StarPrinterHelper.getInstance(printerPortName, context);
        }

        // Observe the MainActivity.  We want to play nice by connecting when the activity
        // starts, and disconnecting when it stops.  This makes it so that when our activity
        // is in the background, other Activities can connect to the printer
        activityMonitor.addObserver(this);
    }
}
