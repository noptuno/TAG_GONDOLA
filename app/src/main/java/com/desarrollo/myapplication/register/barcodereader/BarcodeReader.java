package com.desarrollo.myapplication.register.barcodereader;

public class BarcodeReader {

    public native void setJNIBarCodeReader();

	static {
		System.loadLibrary("barcodereaderjni");
	}

    public void turnOnLaser()
	{
		setJNIBarCodeReader();
	}
}
