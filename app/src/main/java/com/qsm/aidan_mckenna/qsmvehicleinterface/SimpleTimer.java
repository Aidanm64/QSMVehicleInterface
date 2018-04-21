package com.qsm.aidan_mckenna.qsmvehicleinterface;

import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.TextView;

/**
 * Created by aidan_mckenna on 2018-04-04.
 */

public class SimpleTimer {
    private Chronometer mChrono;
    private long startTime;
    private long elapsedTime;

    SimpleTimer(Chronometer chrono)
    {
        mChrono = chrono;
    }
    void start()
    {
        startTime = SystemClock.elapsedRealtime();
        startAtTime(startTime);
    }
    void startAtTime(long base)
    {
        startTime = base;
        mChrono.setBase(startTime);
        mChrono.start();
    }
    void stop()
    {
        elapsedTime = SystemClock.elapsedRealtime() - mChrono.getBase();
        mChrono.stop();
    }
    void resume()
    {
        mChrono.setBase(SystemClock.elapsedRealtime() - elapsedTime);

    }
    void reset()
    {
        elapsedTime = 0;
        mChrono.setBase(SystemClock.elapsedRealtime());
        mChrono.stop();
    }

    long getTime()
    {
        return(SystemClock.elapsedRealtime() -startTime);
    }
}
