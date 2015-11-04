package com.hydra.android.timecycle.timerplan;

import android.os.Parcel;
import android.os.Parcelable;

import com.hydra.android.timecycle.timers.CountDownTimer;

/**
 * Created by jslapnicka on 22.10.2015.
 */
public class TimerPlan implements Parcelable {

    public static final int EXERCISE = 0;
    public static final int REST = 1;
    public static final int REPETITIONS = 2;
    public static final int COUNTDOWN = 3;
    public static final int INTENSITY = 4;

    private static final int NUMBER_OF_PARAMS = 6;

    private int id;
    private long exerciseTimeInMillis;
    private long restTimeInMillis;
    private int repetitions;
    private long countDownTimeInMillis;
    private float intensity;
    private CountDownTimer timer;

    public static class Builder {

        // Required
        private final int id;

        // Optional
        private long exerciseTimeInMillis = 0;
        private int repetitions = 0;
        private long restTimeInMillis = 0;
        private long countDownTimeInMillis = 0;
        private float intensity = 0;

        public Builder(int id) {
            this.id = id;
        }

        public Builder setExerciseTime(long exerciseTimeInMillis) {
            this.exerciseTimeInMillis = exerciseTimeInMillis;
            return this;
        }

        public Builder setRestTime(long restTimeInMillis) {
            this.restTimeInMillis = restTimeInMillis;
            return this;
        }

        public Builder setRepetitions(int repetitions) {
            this.repetitions = repetitions;
            return this;
        }

        public Builder setCountDown(long countDownTimeInMillis) {
            this.countDownTimeInMillis = countDownTimeInMillis;
            return this;
        }

        public Builder setIntensity(float intensity) {
            this.intensity = intensity;
            return this;
        }

        public TimerPlan build() {
            return new TimerPlan(this);
        }
    }

    private TimerPlan(Builder builder) {
        id = builder.id;
        timer = new CountDownTimer();
        exerciseTimeInMillis = builder.exerciseTimeInMillis;
        restTimeInMillis = builder.restTimeInMillis;
        repetitions = builder.repetitions;
        countDownTimeInMillis = builder.countDownTimeInMillis;
        intensity = builder.intensity;
    }

    public int getId() {
        return this.id;
    }

    public long getExerciseTime() {
        return this.exerciseTimeInMillis;
    }

    public long getRestTime() {
        return this.restTimeInMillis;
    }

    public int getRepetitions() {
        return this.repetitions;
    }

    public long getCountDown() {
        return this.countDownTimeInMillis;
    }

    public float getIntensity() {
        return this.intensity;
    }

    public void startTimerPlan() {

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setExerciseTime(long exerciseTime) {
        this.exerciseTimeInMillis = exerciseTime;
    }

    public void setRestTime(long restTime) {
        this.restTimeInMillis = restTime;
    }

    public void setCountdown(long countdown) {
        this.countDownTimeInMillis = countdown;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    /** Parcelable constructor and methods to be able
     * to pass this object to other activity via Intent */

    public TimerPlan(Parcel in) {
        String[] data = new String[NUMBER_OF_PARAMS];

        in.readStringArray(data);
        setId(Integer.valueOf(data[0]));
        setExerciseTime(Long.valueOf(data[1]));
        setRestTime(Long.valueOf(data[2]));
        setCountdown(Long.valueOf(data[3]));
        setRepetitions(Integer.valueOf(data[4]));
        setIntensity(Float.valueOf(data[5]));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                String.valueOf(getId()),
                String.valueOf(getExerciseTime()),
                String.valueOf(getRestTime()),
                String.valueOf(getCountDown()),
                String.valueOf(getRepetitions()),
                String.valueOf(getIntensity())
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<TimerPlan> CREATOR
            = new Parcelable.Creator<TimerPlan>() {

        @Override
        public TimerPlan createFromParcel(Parcel source) {
            return new TimerPlan(source);
        }

        @Override
        public TimerPlan[] newArray(int size) {
            return new TimerPlan[size];
        }
    };
}
