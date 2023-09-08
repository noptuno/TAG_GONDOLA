package com.desarrollo.myapplication.apiadapter.magtek;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

public class MagtekUsbUtil {

    private UsbManager    mUsbManager;
    private PendingIntent mPermissionIntent;
    private UsbDevice     mUsbDevice;
    private Context       mContext;

    public  static final int     MSR_VID     = 2049;
    public  static final int     MSR_PID_KB  = 1;
    public  static final int     MSR_PID_HID = 17;
    private static final String  ACTION_USB_PERMISSION = "com.elo.peripheral.msr.USB_PERMISSION";

    public String TAG = "MagtekUsbUtil";

    public MagtekUsbUtil(Context ctx) {
        mContext = ctx;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        setPermissionIntent();
    }

    private void setPermissionIntent() {
        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        mContext.registerReceiver(mUsbPermissionReceiver, filter);
    }

    public UsbDevice getUsbDevice(int vendorId, int deviceId) {
        UsbDevice device;
        UsbManager um = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = um.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            device = deviceIterator.next();
            if (device.getVendorId() == vendorId) {
                if (device.getProductId() == deviceId) {
                    Log.d(TAG, "Found a device with vendor id " + vendorId + " and Device id " + deviceId);
                    return device;
                }
            }
        }
        return null;
    }

    private final BroadcastReceiver mUsbPermissionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "mUsbPermissionReceiver intent = " + action);
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Log.d(TAG, "mUsbPermissionReceiver has device");
                            if (device.getProductId() == MSR_PID_KB && device.getVendorId() == MSR_VID) {

                                Log.d(TAG, "mUsbPermissionReceiver has device in KB mode");

                                mUsbDevice = device;
                                new Thread(runnableGotoAdvanceMode).start();
                            }

                            if (device.getProductId() == MSR_PID_HID && device.getVendorId() == MSR_VID) {
                                Log.d(TAG, "mUsbPermissionReceiver has device in HID mode");
                            }


                        } else {
                            Log.d(TAG, "mUsbPermissionReceiver no device");
                        }
                    } else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    private Runnable runnableGotoAdvanceMode = new Runnable() {

        @Override
        public void run() {
            if (mUsbDevice == null) return;
            Log.d(TAG, "GotoAdvanceMode device name = " + mUsbDevice.getDeviceName());
            UsbDeviceConnection usbC = mUsbManager.openDevice(mUsbDevice);
            if (usbC != null) {
                usbC.claimInterface(mUsbDevice.getInterface(0), true);
                //Fixed parameter, Don't Modify
                byte[] buffer;
                buffer = new byte[60];
                buffer[0] = 1;
                buffer[1] = 2;
                buffer[2] = 16;
                buffer[3] = 0;
                usbC.controlTransfer(33, 9, 768, 0, buffer, 0, 60, 0);
                usbC.controlTransfer(161, 1, 768, 0, buffer, 0, 60, 0);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e(TAG, "error executing delay", e);
                }
                buffer = new byte[60];
                buffer[0] = 2;
                usbC.controlTransfer(33, 9, 768, 0, buffer, 0, 60, 0);
                usbC.controlTransfer(161, 1, 768, 0, buffer, 0, 60, 0);
            }
            mUsbDevice = null;
        }
    };

    public boolean isMsrInKbMode() {
        UsbDevice device = getUsbDevice(MSR_VID, MSR_PID_KB);

        if(device != null)
            return true;

        return false;
    }

    public boolean isMsrInHidMode() {
        UsbDevice device = getUsbDevice(MSR_VID, MSR_PID_HID);

        if(device != null)
            return true;

        return false;
    }

    public void setToHidMode() {
        UsbDevice dev = getUsbDevice(MSR_VID, MSR_PID_KB);
        if(dev == null) {
            Log.d(TAG, "Device not found or already in HID mode");
            return;
        }

        mUsbManager.requestPermission(dev, mPermissionIntent);
    }

}
