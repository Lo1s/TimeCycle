package com.hydra.android.timecycle;

// TODO: bug: start -> lap -> stop -> reset -> start
public class StopWatch {
    private long startTime = 0;
    private long elapsedTime = 0;
    private boolean isRunning = false;
    private boolean isStopped = false;
    private long currentTime = 0;

    public StopWatch() {

    }

    public StopWatch(long startTime, long endTime) {
        this.startTime = startTime;
        this.elapsedTime = endTime;
    }

    public void start() {
        if (!isStopped)
            this.startTime = System.currentTimeMillis();
        else {
            this.startTime = System.currentTimeMillis() - elapsedTime;
        }
        this.isRunning = true;
        this.isStopped = false;
    }

    public void stop() {
        this.elapsedTime = System.currentTimeMillis() - startTime;
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
        this.startTime = 0;
        this.elapsedTime = 0;
        isStopped = true;
        isRunning = false;
    }

    public void split() {
        this.startTime = System.currentTimeMillis();
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
