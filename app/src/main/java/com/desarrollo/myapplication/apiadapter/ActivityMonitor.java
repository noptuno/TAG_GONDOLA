package com.desarrollo.myapplication.apiadapter;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by elo on 9/9/17.
 *
 * This class is used to notify the individual adapters if there's an Activity lifecycle
 * event in the Main Activity.  Use of this monitor is optional.  The Activity is required
 * to trigger the events, if it's used.
 *
 * The class itself is a subclass of Observable.  Any interested adapters can register themselves
 * as an observer of the Monitor.
 */

public class ActivityMonitor extends Observable {
    public static final String EVENT_ON_START = "onStart";
    public static final String EVENT_ON_STOP = "onStop";
    public static final String EVENT_ON_RESUME = "onResume";
    public static final String EVENT_ON_PAUSE = "onPause";

    /**
     * Called by the Activity to indicate that it is undergoing an Activity lifecycle event.
     * It should only ever be called by said activity.
     *
     * @param event  String indicating the event that is happening.  Must be one of the EVENT constants
     */
    public void onActivityEvent(String event) {
        setChanged();
        notifyObservers(event);
    }

    @Override
    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }
}
