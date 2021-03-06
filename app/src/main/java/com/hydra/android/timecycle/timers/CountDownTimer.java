package com.hydra.android.timecycle.timers;

import android.util.Log;

/**
 * Created by jslapnicka on 16.10.2015.
 */
public class CountDownTimer {
    private long startTime = 0;
    private long endTime = 0;
    private long duration = 0;
    private boolean isRunning = false;
    private boolean isStopped = false;
    private long pausedTime = 0;
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

    public void start() {
        Log.i(TAG, "started");
        // Convert to millis
        if (!isStopped) {
            this.startTime = System.currentTimeMillis() + hour + minute + second;
            this.endTime = System.currentTimeMillis();
        } else {
            this.startTime = System.currentTimeMillis() + pausedTime;
        }
        this.isRunning = true;
        this.isStopped = false;
    }

    public void resume(long startTime) {
        Log.i(TAG, "resumed");
        this.startTime = startTime;
        this.isRunning = true;
        this.isStopped = false;
    }

    public void stop() {
        Log.i(TAG, "stopped");
        resetTime();
    }

    public void pause() {
        Log.i(TAG, "paused");
        this.pausedTime = startTime - System.currentTimeMillis();
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

    public void setTime(long hour, long minute, long second) {
        this.hour = hour * 60 * 60 * 1000;
        this.minute = minute * 60 * 1000;
        this.second = second * 1000;
        setDuration(this.hour, this.minute, this.second);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setDuration(long hoursInMillis, long minutesInMillis, long secondsInMillis) {
        duration = hoursInMillis + minutesInMillis + secondsInMillis;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public float getProgress() {
        float progress = 0;
        if (!isStopped()) {
            progress = (float) (duration - getElapsedTime()) / (float) duration;
        } else {
            progress = (float) (duration - pausedTime) / (float) duration;
        }
        return progress;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getPausedTime() {
        return this.pausedTime;
    }

    public long getElapsedTime() {
        long elapsedTime = 0;
        if (isRunning) {
            elapsedTime = ((startTime - System.currentTimeMillis()));
        }
        return elapsedTime;
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
            elapsedHours = ((((startTime - System.currentTimeMillis()) / 1000) / 60) / 60) % 24;
        }
        return elapsedHours;
    }
}
