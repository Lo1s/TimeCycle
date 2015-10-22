package com.hydra.android.timecycle;

/**
 * Created by jslapnicka on 22.10.2015.
 */
public class TimerPlan {

    public static final int EXERCISE = 0;
    public static final int REST = 1;
    public static final int REPETITIONS = 2;
    public static final int COUNTDOWN = 3;

    private final int id;
    private final long exerciseTimeInMillis;
    private final long restTimeInMillis;
    private final int repetitions;
    private final long countDownTimeInMillis;
    private final int intensity;

    public static class Builder {

        // Required
        private final int id;
        private final long exerciseTimeInMillis;
        private final int repetitions;

        // Optional
        private long restTimeInMillis = 0;
        private long countDownTimeInMillis = 0;
        private int intensity;

        public Builder(int id, long exerciseTimeInMilis, int repetitions) {
            this.id = id;
            this.exerciseTimeInMillis = exerciseTimeInMilis;
            this.repetitions = repetitions;
        }

        public Builder setRestTime(long restTimeInMillis) {
            this.restTimeInMillis = restTimeInMillis;
            return this;
        }

        public Builder setCountDown(long countDownTimeInMillis) {
            this.countDownTimeInMillis = countDownTimeInMillis;
            return this;
        }

        public Builder setIntensity(int intensity) {
            this.intensity = intensity;
            return this;
        }

        public TimerPlan build() {
            return new TimerPlan(this);
        }
    }

    private TimerPlan(Builder builder) {
        id = builder.id;
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

    public int getIntensity() {
        return this.intensity;
    }

}
