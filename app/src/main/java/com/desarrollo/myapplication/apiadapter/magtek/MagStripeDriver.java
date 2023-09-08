package com.desarrollo.myapplication.apiadapter.magtek;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import com.magtek.mobile.android.libDynamag.MagTeklibDynamag;

public class MagStripeDriver
{
	public final String TAG = "MagStripeDriver";

	public interface MagStripeListener
	{
		public abstract void OnCardSwiped(String cardData);
		public abstract void OnDeviceDisconnected();
		public abstract void OnDeviceConnected();
	}

	protected static final int DEVICE_CONNECTED = 4;
	protected static final int DEVICE_DISCONNECTED = 5;
	protected static final int DEVICE_CARD_SWIPED = 3;

	private static final int RESULT_CODE_SUCCESS = 0x00;
	private static final int RESULT_CODE_SECURITY_VIOLATION = 0x07;
	
	private MagTeklibDynamag mMagStripe = null;
	MagStripeListener mListener = null;

	Context mContext;
	
	private Handler MagStripeHandler = new Handler(new Callback()
	{ 
		//Callback to handle message from Magstripe Class
		@Override
		public boolean handleMessage(Message msg) {
			return handleMagStripeMessage(msg);
		}

	});

	public MagStripeDriver(Context context) {
		mContext = context;
	}

	public void registerMagStripeListener(MagStripeListener listener) {
		mListener = listener;
	}

	private boolean handleMagStripeMessage(Message msg){

		if(msg != null)
		{
			switch(msg.what)
			{
				case DEVICE_CONNECTED :
				{
					Log.d(TAG, "DEVICE_CONNECTED ");

					if(mListener != null)
						mListener.OnDeviceConnected();
					break;
				}
				case DEVICE_DISCONNECTED :
				{
					Log.d(TAG, "DEVICE_DISCONNECTED ");

					if(mListener != null) mListener.OnDeviceDisconnected();
					break;
				}
				case DEVICE_CARD_SWIPED : 
				{
					if(mListener != null){ 
						try {
							mMagStripe.setCardData((String)msg.obj);
							if ((mMagStripe.getTrack2Masked().length() > 0)&& (!mMagStripe.getTrack2Masked().equalsIgnoreCase(";E?"))){
								mListener.OnCardSwiped(mMagStripe.getTrack1Masked());
							}
						} catch (Exception e) {
							// This is basically to catch the runtime exceptions
							Log.e("PPT Diagnistics", e.getMessage(), e);
						}
					}
					break;
				}
				default : 
				break;
			
			}
		}
		return true;

	}
	
	public void startDevice(){ //Start the Device.
		if(mMagStripe == null)
			mMagStripe = new MagTeklibDynamag(mContext, MagStripeHandler);

		if (!mMagStripe.isDeviceConnected())
			mMagStripe.openDevice();
	}

	public byte[] sendCommand(String command) {
		CharSequence csCommand = command;
		byte[] bResponse = mMagStripe.sendCommand(csCommand);
		return bResponse;
	}

	public byte[] sendCommandWithLength(String lCommand) {
		CharSequence csCommand = lCommand;
		byte[] bResponse = mMagStripe.sendCommand(csCommand);
		return bResponse;
	}

	public void stopDevice() {
		//Disable the Device
		mMagStripe.clearCardData();
		if(mMagStripe.isDeviceConnected())
		{
			mMagStripe.closeDevice();
		}
	}

	public void stopAllListener() {
		MagStripeHandler.removeCallbacksAndMessages(null);
	}

	public void sendKbModeCommand() {
		final String MSR_COMMAND_KB_TO_HID  = "01021001";
		final String MSR_COMMAND_RESET      = "02";

		if (mMagStripe.isDeviceConnected()) {
			Log.d(TAG, "Sending reset command");
			byte[] res = mMagStripe.sendCommandWithLength(MSR_COMMAND_KB_TO_HID);

			if (res[0] == RESULT_CODE_SECURITY_VIOLATION) {
				throw new RuntimeException("Security violation");
			} else if (res[0] != RESULT_CODE_SUCCESS) {
				throw new RuntimeException("Failed!");
			}

			mMagStripe.sendCommandWithLength(MSR_COMMAND_RESET);
		} else {
			Log.d(TAG, "Device not connected, cannot send reset command");
		}
	}

}
