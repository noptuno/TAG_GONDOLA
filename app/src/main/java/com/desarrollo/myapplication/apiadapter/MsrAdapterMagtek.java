package com.desarrollo.myapplication.apiadapter;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.desarrollo.myapplication.apiadapter.magtek.MagStripeCardParser;
import com.desarrollo.myapplication.apiadapter.magtek.MagStripeDriver;
import com.desarrollo.myapplication.apiadapter.magtek.MagtekUsbUtil;


public class MsrAdapterMagtek implements MsrAdapter {

    public final String TAG = "MsrAdapterMagtek";

    private MagStripeDriver magStripeDriver;
    private MagtekUsbUtil magtekUsbUtil;

    private MsrCallback mCallback;
    private Context mContext;

    MsrAdapterMagtek(Context ctx) {
        Log.d(TAG, "MsrAdapterMagtek constructor");
        mContext = ctx;
        magStripeDriver = new MagStripeDriver(mContext);
        magtekUsbUtil = new MagtekUsbUtil(mContext);
    }

    MagStripeDriver.MagStripeListener listener = new MagStripeDriver.MagStripeListener() {
        //MageStripe Reader's Listener for notifying various events.

        @Override
        public void OnDeviceDisconnected() { //Fired when the Device has been Disconnected.
            Log.d(TAG, "OnDeviceDisconnected");
        }

        @Override
        public void OnDeviceConnected() { //Fired when the Device has been Connected.
            Log.d(TAG, "OnDeviceConnected");
        }

        @Override
        public void OnCardSwiped(String cardData) { //Fired when a card has been swiped on the device.
            Log.d(TAG, "OnCardSwiped");
            try {
                MagStripeCardParser mParser = new MagStripeCardParser(cardData); //Instance of card swipe reader
                if(mParser.isDataParse()){
                    if(mParser.hasTrack1()){
                        String accountNo = mParser.getAccountNumber();

                        if(mCallback != null) {
                            mCallback.onCardSwipe(accountNo);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "error parsing card data", e);
            }
        }
    };

    @Override
    public void startMSR(MsrCallback callback) {
        Log.d(TAG, "startMSR");
        mCallback = callback;
        magStripeDriver.startDevice(); //Start the MagStripe Reader
        magStripeDriver.registerMagStripeListener(listener);
    }

    @Override
    public void stopMSR() {
        Log.d(TAG, "stopMSR");

        try {
            magStripeDriver.stopAllListener();
            magStripeDriver.stopDevice(); //Stop the device as the use has been completed.
        }catch (Exception ex){

        }

        mCallback = null;
    }

    @Override
    public boolean isMsrInKbMode() {
        return magtekUsbUtil.isMsrInKbMode();
    }

    @Override
    public boolean isMsrInHidMode() {
        return magtekUsbUtil.isMsrInHidMode();
    }

    @Override
    public void setMsrKbMode() {
        if( ! magtekUsbUtil.isMsrInHidMode() ) {
            Log.d(TAG, "Device not found or already in keyboard mode");
            return;
        }

        try {
            magStripeDriver.sendKbModeCommand();     // we use magstrip library to issue command
        }
        catch (RuntimeException ex) {
            Log.e(TAG, "Failed to switch to keyboard mode:" + ex.getMessage());
            Toast.makeText(mContext,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setMsrHidMode() {
        if( ! magtekUsbUtil.isMsrInKbMode() ) {
            Log.d(TAG, "Device not found or already in HID mode");
            return;
        }

        magtekUsbUtil.setToHidMode();
    }

}
