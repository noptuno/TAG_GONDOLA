package com.desarrollo.myapplication.register.cfd;

public class CFD {
    public native void setJNICfdClearDisplay();
    public native void setJNICfdSetBacklight(int value);
    public native void setJNICfdText(int lineno, String sentence);
    public native void setJNICfdShiftDisplay(int direction);

    static {
        System.loadLibrary("cfdjni");
    }
    public void clearDisplay()
    {
        setJNICfdClearDisplay();
    }

    public void setBacklight(boolean isOn)
    {
        if (isOn)
            setJNICfdSetBacklight(1);
        else
            setJNICfdSetBacklight(0);
    }
    
    public void setLine1(String line)
    {
    	setJNICfdText(0, line);
    }
    
    public void setLine2(String line)
    {
    	setJNICfdText(1, line);    	
    }

    public void shiftDisplay(int direction)
    {
    	setJNICfdShiftDisplay(direction);
    }
}
