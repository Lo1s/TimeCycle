package com.hydra.android.timecycle.timers;

import android.util.Log;

public class StopWatch {
    private long startTime = 0;
    private long pausedTime = 0;
    private boolean isRunning = false;
    private boolean isStopped = false;
    private String type = "";
    private static final String TAG = "StopWatch";

    public StopWatch() {

    }

    public StopWatch(String type) {
        this.type = type;
    }

    public void start() {
        Log.i(TAG, type + " started");
        if (!isStopped)
            this.startTime = System.currentTimeMillis();
        else {
            this.startTime = System.currentTimeMillis() - pausedTime;
        }
        this.isRunning = true;
        this.isStopped = false;
    }

    public void resume(long startTime) {
        Log.i(TAG, type + " resumed");
        this.startTime = startTime;
        this.isRunning = true;
        this.isStopped = false;
    }

    public void stop() {
        Log.i(TAG, type + " stopped");
        this.pausedTime = System.currentTimeMillis() - startTime;
        this.isRunning = true;
        this.isStopped = true;
    }

    public void resetTime() {
        Log.i(TAG, type + " reseted");
        this.startTime = 0;
        this.pausedTime = 0;
        isStopped = false;
        isRunning = false;
    }

    public void split() {
        Log.i(TAG, type + " splitted");
        this.startTime = System.currentTimeMillis();
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setPausedTime(long time) {
        this.pausedTime = time;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getPausedTime() {
        return this.pausedTime;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public boolean isStopped() {
        return this.isStopped;
    }

    public String getType() {
        return this.type;
    }

    // Returns time in milliseconds
    public long getElapsedTimeMilli() {
        long elapsedMillis = 0;
        if (isRunning) {
            elapsedMillis = ((System.currentTimeMillis() - startTime) / 100) % 10;
        }
        return elapsedMillis;
    }

    // Returns time in seconds
    public long getElapsedTimeSecs() {
        long elapsedSecs = 0;
        if (isRunning) {
            elapsedSecs = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        }
        return elapsedSecs;
    }

    // Returns time in minutes
    public long getElapsedTimeMinutes() {
        long elapsedMins = 0;
        if (isRunning) {
            elapsedMins = (((System.currentTimeMillis() - startTime) / 1000) / 60) % 60;
        }
        return elapsedMins;
    }

    // Returns time in hours
    public long getElapsedTimeHours() {
        long elapsedHours = 0;
        if (isRunning) {
            // TODO: Check the mod 24 if working
            elapsedHours = ((((System.currentTimeMillis() - startTime) / 1000) / 60) / 60) % 24;
        }
        return elapsedHours;
    }


}
