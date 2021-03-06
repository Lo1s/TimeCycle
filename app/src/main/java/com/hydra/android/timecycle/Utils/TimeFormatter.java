package com.hydra.android.timecycle.utils;

/**
 * Created by jslapnicka on 23.10.2015.
 */
public class TimeFormatter {

    /** Convert time in HMS format to milliseconds */
    public static long timeToMillis(int hours, int minutes, int seconds) {
        return (hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000);
    }

    /** Converte time in milliseconds to HMS format */
    public static int[] millisToHms(long time) {
        int timerHour = (int)(time / 60 / 60 / 1000);
        int timerMinute = (int)(time / 60 / 1000 - (timerHour * 60));
        int timerSecond = (int)(time / 1000 - (timerMinute * 60) - (timerHour * 60 * 60));
        int timerMillis = (int)(time - (timerSecond * 1000) - (timerMinute * 60 * 1000)
                - (timerHour * 60 * 60 * 1000)) / 100;

        return new int[] {timerHour, timerMinute, timerSecond, timerMillis};
    }

    /** Returns string representation of the time in format "HH:MM:SS,MS" */
    public static String formatTimeToString(int hours, int minutes, int seconds, int millis) {
        StringBuilder builder = new StringBuilder();
        String minuteString;
        String secondString;

        if (minutes < 10) {
            minuteString = "0" + minutes;
        } else {
            minuteString = "" + minutes;
        }

        if (seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;
        }

        builder.append(hours + ":");
        builder.append(minuteString + ":");
        builder.append(secondString + ",");
        builder.append(millis + "");

        return builder.toString();
    }

    /** Returns string representation of the time in format "HH:MM:SS,MS" */
    public static String formatTimeToString(long hours, long minutes, long seconds, long millis) {
        StringBuilder builder = new StringBuilder();
        String minuteString;
        String secondString;

        if (minutes < 10) {
            minuteString = "0" + minutes;
        } else {
            minuteString = "" + minutes;
        }

        if (seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;
        }


        builder.append(hours + ":");
        builder.append(minuteString + ":");
        builder.append(secondString + ",");
        builder.append(millis + "");

        return builder.toString();
    }

    /** Returns string representation of the time in format "MM:SS" */
    public static String formatTimeToString(long minutes, long seconds) {
        StringBuilder builder = new StringBuilder();
        String minuteString;
        String secondString;

        if (minutes < 10) {
            minuteString = "0" + minutes;
        } else {
            minuteString = "" + minutes;
        }

        if (seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;
        }


        builder.append(minuteString + ":");
        builder.append(secondString + "");

        return builder.toString();
    }

    /** Returns string representation of the time in format "SS" */
    public static String formatTimeToString(int seconds) {
        StringBuilder builder = new StringBuilder();
        String secondString;
        secondString = "" + seconds;
        builder.append(secondString + "");

        return builder.toString();
    }

    public static String formatTimeToString(long timeInMillis) {
        int[] temp = millisToHms(timeInMillis);
        return formatTimeToString(temp[0], temp[1], temp[2], temp[3]);
    }
}
