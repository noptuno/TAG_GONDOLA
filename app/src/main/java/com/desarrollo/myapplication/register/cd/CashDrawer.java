package com.desarrollo.myapplication.register.cd;

/**
 * Created by elo on 9/12/17.
 */

public class CashDrawer {
    public native String getJNIDi();
    public native void setJNICashDrawerOpen();

    private int m_count=0;
    private final int MAX_DI = 1;
    private final int DO_CASHDRAWER=0;

    static {
        System.loadLibrary("cashdrawerjni");
    }


    public boolean isDrawerOpen() {
        String di_list = getJNIDi();

        int value = Integer.valueOf(di_list.substring(0, 1));
        return (value != 0);
    }

    public void openCashDrawer()
    {
        setJNICashDrawerOpen();
    }
}
