package com.hydra.android.timecycle;

/**
 * Created by jslapnicka on 12.10.2015.
 */
public class StopWatch {
    private long startTime = 0;
    private long endTime = 0;
    private boolean isRunning = false;
    private boolean isStopped = false;
    private long currentTime = 0;

    public StopWatch() {

    }

    public StopWatch(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void start() {
        if (!isStopped)
            this.startTime = System.currentTimeMillis();
        else {
            this.startTime = System.currentTimeMillis() - endTime;
        }
        this.isRunning = true;
        this.isStopped = false;
    }

    public void stop() {
        this.endTime = System.currentTimeMillis() - startTime;
        this.isRunning = true;
        this.isStopped = true;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void resetTime() {
        this.startTime = 0;
        this.endTime = 0;
        isStopped = true;
    }

    public void split() {
        this.startTime = System.currentTimeMillis();
    }

    // Returns time in milliseconds
    public long getElapsedTimeMilli() {
        long elapsed = 0;
        if (isRunning) {
            elapsed = ((System.currentTimeMillis() - startTime) / 100) % 10;
        }
        return elapsed;
    }

    // Returns time in seconds
    public long getElapsedTimeSecs() {
        long elapsed = 0;
        if (isRunning) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        }
        return elapsed;
    }

    // Returns time in minutes
    public long getElapsedTimeMinutes() {
        long elapsed = 0;
        if (isRunning) {
            elapsed = (((System.currentTimeMillis() - startTime) / 1000) / 60) % 60;
        }
        return elapsed;
    }

    // Returns time in hours
    public long getElapsedTimeHours() {
        long elapsed = 0;
        if (isRunning) {
            // TODO: Check the mod 24 if working
            elapsed = ((((System.currentTimeMillis() - startTime) / 1000) / 60) / 60) % 24;
        }
        return elapsed;
    }

}
