package com.hydra.android.timecycle.timers;

import android.util.Log;

/**
 * Created by jslapnicka on 16.10.2015.
 */
public class CountDownTimer {
    private long startTime = 0;
    private long endTime = 0;
    private boolean isRunning = false;
    private boolean isStopped = false;
    private long elapsedTime = 0;
    private long hour;
    private long minute;
    private long second;
    private static final String TAG = "Timer";

    public CountDownTimer() {

    }

    public CountDownTimer(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setTime(long hour, long minute, long second) {
        this.hour = hour * 60 * 60 * 1000;
        this.minute = minute * 60 * 1000;
        this.second = second * 1000;
    }

    public void start() {
        Log.i(TAG, "started");
        // Convert to millis
        if (!isStopped) {
            this.startTime = System.currentTimeMillis() + hour + minute + second;
            this.endTime = System.currentTimeMillis();
        } else {
            this.startTime = System.currentTimeMillis() + elapsedTime;
        }
        this.isRunning = true;
        this.isStopped = false;
    }

    public void stop() {
        Log.i(TAG, "stopped");
        resetTime();
    }

    public void pause() {
        Log.i(TAG, "paused");
        this.elapsedTime = startTime - System.currentTimeMillis();
        this.isRunning = true;
        this.isStopped = true;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void resetTime() {
        Log.i(TAG, "reseted");
        this.startTime = 0;
        this.endTime = 0;
        isStopped = false;
        isRunning = false;
    }

    public void split() {
        Log.i(TAG, "splitted");
        this.startTime = System.currentTimeMillis();
    }

    // Returns time in milliseconds
    public long getElapsedTimeMilli() {
        long elapsedMillis = 0;
        if (isRunning) {
            elapsedMillis = ((startTime - System.currentTimeMillis()) / 100) % 10;
        }
        return elapsedMillis;
    }

    // Returns time in seconds
    public long getElapsedTimeSecs() {
        long elapsedSecs = 0;
        if (isRunning) {
            elapsedSecs = ((startTime - System.currentTimeMillis()) / 1000) % 60;
        }
        return elapsedSecs;
    }

    // Returns time in minutes
    public long getElapsedTimeMinutes() {
        long elapsedMinutes = 0;
        if (isRunning) {
            elapsedMinutes = (((startTime - System.currentTimeMillis()) / 1000) / 60) % 60;
        }
        return elapsedMinutes;
    }

    // Returns time in hours
    public long getElapsedTimeHours() {
        long elapsedHours = 0;
        if (isRunning) {
            // TODO: Check the mod 24 if working
            elapsedHours = ((((startTime - System.currentTimeMillis())  / 1000) / 60) / 60) % 24;
        }
        return elapsedHours;
    }
}
