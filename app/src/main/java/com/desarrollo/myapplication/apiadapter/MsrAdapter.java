package com.desarrollo.myapplication.apiadapter;


public interface MsrAdapter {

    public class CardData {
        String allText;
    }

    public interface MsrCallback {
        public void onCardSwipe(String cardData);
    }

    public void startMSR(MsrCallback callback);
    public void stopMSR();

    // device configuration related APIs.
    // TODO: use better way, make it more transparent and general
    public boolean isMsrInKbMode();
    public boolean isMsrInHidMode();
    public void setMsrKbMode();
    public void setMsrHidMode();
}
