package com.hydra.android.timecycle.mainui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;
import com.codetroopers.betterpickers.hmspicker.HmsPickerDialogFragment;
import com.hydra.android.timecycle.R;
import com.hydra.android.timecycle.common.LapTimesAdapter;
import com.hydra.android.timecycle.timerplan.TimerEditActivity;
import com.hydra.android.timecycle.timerplan.TimerPlan;
import com.hydra.android.timecycle.timers.CountDownTimer;
import com.hydra.android.timecycle.timers.StopWatch;
import com.hydra.android.timecycle.utils.MyConstants;
import com.hydra.android.timecycle.utils.TimeFormatter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements HmsPickerDialogFragment.HmsPickerDialogHandler {

    private final String TAG = "MainActivity";
    public static final String TIMER = "timerTextView";

    // UI Components
    private LayoutInflater inflater;
    private TextView textView_timeDisplay;
    private TextView textView_secondaryTimeDisplay;
    private TextView textView_countDownDisplay;
    private RecyclerView recyclerView_lapTimes;
    private RecyclerView.LayoutManager rVlayoutManager;
    private RecyclerView.Adapter rVadapter;
    private Button startStopButton;
    private Button lapReset;
    private CustomBackground background;
    private int backgroundColor;
    private FrameLayout frameLayout;
    private RelativeLayout stopWatchLayout;
    private View mainView;
    private View contentView;
    private HmsPickerBuilder timePicker;

    private long time;
    private int[] hmsTime;
    private long pausedTime;
    private int numberOfCycles = 1;
    private int repetitions;
    private long exerciseTime;
    private long restTime;
    private long countDownTime;

    // Timer Handler & messages
    private TimerHander handler;
    private final static int UI_REFRESH_RATE = 100;
    private final static int MSG_STOP_STOPWATCH = 0;
    private final static int MSG_START_STOPWATCH = 1;
    private final static int MSG_UPDATE_STOPWATCH = 2;
    private final static int MSG_PAUSE_TIMER = 3;
    private final static int MSG_START_TIMER = 4;
    private final static int MSG_UPDATE_TIMER = 5;
    private final static int MSG_STOP_TIMER = 6;
    private final static int MSG_START_COUNTDOWN = 7;
    private final static int MSG_STOP_COUNTDOWN = 8;
    private final static int MSG_PAUSE_COUNTDOWN = 9;
    private final static int MSG_UPDATE_COUNTDOWN = 10;
    private final static int MSG_RESUME_STOPWATCH = 11;
    private final static int MSG_RESUME_TIMER = 12;
    private final static int MSG_RESUME_PAUSED_TIMER = 13;
    private final static int MSG_RESUME_PAUSED_STOPWATCH = 14;

    // Helper timer classes
    private StopWatch mainStopWatch = new StopWatch("main");
    private StopWatch splitStopWatch = new StopWatch("split");
    private CountDownTimer timer = new CountDownTimer();

    private ArrayList<String> mLapTimes;
    private TimerPlan timerPlan;
    private int timerType;

    // Saving activity state during re-orientation
    private SharedPreferences sharedPrefs;
    private final static String TYPE_OF_TIMER = "typeOfTimer";
    private final static int STOPWATCH_IS_RUNNING = 0;
    private final static int TIMER_IS_RUNNING = 1;
    private final static int NONE_TIMER_IS_RUNNING = -1;
    private final static String SAVED_MAIN_STOPWATCH_TIME = "savedMainStopWatchTime";
    private final static String SAVED_MAIN_STOPWATCH_ELAPSED_TIME = "savedMainStopWatchElapsedTime";
    private final static String SAVED_SPLIT_STOPWATCH_TIME = "savedSplitStopWatchTime";
    private final static String SAVED_SPLIT_STOPWATCH_ELAPSED_TIME = "savedSplitStopWatchElapsedTime";
    private final static String SAVED_STOPWATCH_IS_STOPPED = "savedMainStopWatchIsStopped";
    private final static String SAVED_TIMER_START_TIME = "savedTimerStartTime";
    private final static String SAVED_TIMER_DURATION = "savedTimerDuration";
    private final static String SAVED_TIMER_IS_STOPPED = "savedTimerIsStopped";
    private final static String SAVED_TIMER_ELAPSED_TIME = "savedTimerElapsedTime";
    private final static String SAVED_TIMER_PROGRESS = "savedTimerProgress";
    private boolean isStopWatchRunning;
    private boolean isTimerRunning;


    // Handler refreshes UI with the stopwatch time by the given UI_REFRESH_RATE
    // made it static to prevent memory leaks
    private static class TimerHander extends Handler {

        private WeakReference<MainActivity> mTarget;
        private long milliTimer;
        private long secTimer;
        private long minTimer;
        private long hourTimer;
        private long milliMainStopWatch;
        private long secMainStopWatch;
        private long minMainStopWatch;
        private long hourMainStopWatch;
        private long milliSplitStopWatch;
        private long secSplitStopWatch;
        private long minSplitStopWatch;
        private long hourSplitStopWatch;

        TimerHander(MainActivity target) {
            mTarget = new WeakReference<>(target);
        }

        public void setTarget(MainActivity target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mActivity = mTarget.get();

            if (mActivity != null) {
                super.handleMessage(msg);

                getTimes(mActivity);

                switch (msg.what) {
                    case MainActivity.MSG_START_STOPWATCH:
                        mActivity.mainStopWatch.start();
                        mActivity.splitStopWatch.start();
                        sendEmptyMessage(MSG_UPDATE_STOPWATCH);
                        break;
                    case MainActivity.MSG_RESUME_STOPWATCH:
                        mActivity.mainStopWatch.resume(mActivity.sharedPrefs
                                .getLong(SAVED_MAIN_STOPWATCH_TIME, 0));
                        mActivity.splitStopWatch.resume(mActivity.sharedPrefs
                                .getLong(SAVED_SPLIT_STOPWATCH_TIME, 0));
                        sendEmptyMessage(MSG_UPDATE_STOPWATCH);
                        mActivity.setUpStartButton();
                        break;
                    case MainActivity.MSG_RESUME_PAUSED_STOPWATCH:
                        mActivity.mainStopWatch.setPausedTime(mActivity.sharedPrefs.getLong(
                                SAVED_MAIN_STOPWATCH_ELAPSED_TIME, 0));
                        mActivity.splitStopWatch.setPausedTime(mActivity.sharedPrefs.getLong(
                                SAVED_SPLIT_STOPWATCH_ELAPSED_TIME, 0));
                        mActivity.mainStopWatch.setStopped(true);
                        mActivity.splitStopWatch.setStopped(true);
                        mActivity.mainStopWatch.setRunning(true);
                        mActivity.splitStopWatch.setRunning(true);
                        mActivity.displayTime(TimeFormatter.formatTimeToString(
                                mActivity.mainStopWatch.getPausedTime()));
                        mActivity.displaySecondaryTime(TimeFormatter.formatTimeToString(
                                mActivity.splitStopWatch.getPausedTime()));
                        mActivity.setUpStopButton();
                        removeMessages(MSG_UPDATE_STOPWATCH);
                        break;
                    case MSG_STOP_STOPWATCH:
                        mActivity.mainStopWatch.stop();
                        mActivity.splitStopWatch.stop();
                        mActivity.displayTime(TimeFormatter.formatTimeToString(
                                mActivity.mainStopWatch.getPausedTime()));
                        mActivity.displaySecondaryTime(TimeFormatter.formatTimeToString(
                                mActivity.splitStopWatch.getPausedTime()));
                        removeMessages(MSG_UPDATE_STOPWATCH);
                        break;
                    case MSG_UPDATE_STOPWATCH:
                        mActivity.displayTime(TimeFormatter.formatTimeToString(hourMainStopWatch,
                                minMainStopWatch, secMainStopWatch, milliMainStopWatch));
                        mActivity.displaySecondaryTime(TimeFormatter.formatTimeToString(
                                hourSplitStopWatch, minSplitStopWatch, secSplitStopWatch,
                                milliSplitStopWatch));
                        sendEmptyMessageDelayed(MSG_UPDATE_STOPWATCH,
                                UI_REFRESH_RATE);
                        break;
                    case MSG_START_TIMER:
                        if (!mActivity.timer.isStopped()) {
                            mActivity.timer.setTime(mActivity.hmsTime[0], mActivity.hmsTime[1],
                                    mActivity.hmsTime[2]);
                            // Convert time back to milliseconds
                            mActivity.background.startAnimation(mActivity.time,
                                    mActivity.backgroundColor);
                        } else {
                            mActivity.background.resumeAnimation(mActivity.timer.getProgress());
                        }
                        mActivity.timer.start();
                        sendEmptyMessage(MSG_UPDATE_TIMER);
                        break;
                    case MSG_RESUME_TIMER:
                        mActivity.backgroundColor = Color.argb(255, 255, 64, 129);
                        mActivity.timer.setDuration(
                                mActivity.sharedPrefs.getLong(SAVED_TIMER_DURATION, 0));
                        mActivity.background.setBackgroundColor(mActivity.backgroundColor);
                        mActivity.background.setDuration(mActivity.timer.getDuration());
                        mActivity.timer.resume(
                                mActivity.sharedPrefs.getLong(SAVED_TIMER_START_TIME, 0));
                        mActivity.background.resumeAnimation(mActivity.timer.getProgress());
                        mActivity.displayTime("");
                        mActivity.setUpStartButton();
                        sendEmptyMessage(MSG_UPDATE_TIMER);
                        // Clear the timer display (not to mark the lapReset button as "lap")
                        break;
                    case MSG_RESUME_PAUSED_TIMER:
                        mActivity.backgroundColor = Color.argb(255, 255, 64, 129);
                        mActivity.timer.setDuration(
                                mActivity.sharedPrefs.getLong(SAVED_TIMER_DURATION, 0));
                        mActivity.timer.setStartTime(System.currentTimeMillis()
                                + mActivity.sharedPrefs.getLong(SAVED_TIMER_ELAPSED_TIME, 0));
                        mActivity.background.setBackgroundColor(mActivity.backgroundColor);
                        mActivity.background.setDuration(mActivity.timer.getDuration());
                        mActivity.background.pauseAnimaton(mActivity.sharedPrefs.getFloat(
                                SAVED_TIMER_PROGRESS, 0));
                        mActivity.displayTime(TimeFormatter.formatTimeToString(
                                (mActivity.sharedPrefs.getLong(SAVED_TIMER_ELAPSED_TIME, 0))));
                        mActivity.timer.pause();
                        mActivity.pausedTime = mActivity.timer.getPausedTime();
                        //Redundant but just to be sure
                        removeMessages(MSG_UPDATE_TIMER);
                        break;
                    case MSG_PAUSE_TIMER:
                        mActivity.background.pauseAnimaton(MyConstants.NO_PROGRESS);
                        mActivity.timer.pause();
                        mActivity.pausedTime = mActivity.timer.getPausedTime();
                        mActivity.displayTime(
                                TimeFormatter.formatTimeToString(mActivity.pausedTime));
                        removeMessages(MSG_UPDATE_TIMER);
                        break;
                    case MSG_STOP_TIMER:
                        removeMessages(MSG_UPDATE_TIMER);
                        mActivity.reset();
                        mActivity.startStopButton.setTextColor(Color.parseColor("#99cc00"));
                        mActivity.startStopButton.setText(R.string.Start);
                        mActivity.lapReset.setEnabled(true);
                        break;
                    case MSG_UPDATE_TIMER:
                        if (!mActivity.isTimerFinished(hourTimer, minTimer, secTimer, milliTimer)) {
                            mActivity.displayTime(TimeFormatter.formatTimeToString(
                                    hourTimer, minTimer, secTimer, milliTimer));
                            sendEmptyMessageDelayed(MSG_UPDATE_TIMER,
                                    UI_REFRESH_RATE);
                            Log.i("Progress", mActivity.timer.getProgress() + "");
                        } else {
                            sendEmptyMessage(MSG_STOP_TIMER);

                            if (mActivity.timerPlan == null ||
                                    (mActivity.numberOfCycles) > mActivity.repetitions) {
                                mActivity.textView_secondaryTimeDisplay.setText(mActivity.getResources()
                                        .getString(R.string.textView_default_time));
                                mActivity.timerType = 0;
                                mActivity.timerPlan = null;
                            }

                            if (mActivity.timerPlan != null &&
                                    mActivity.numberOfCycles <= mActivity.repetitions) {
                                mActivity.timerType = (mActivity.timerType + 1) % 2;
                                mActivity.runTimerPlan(mActivity.timerPlan, mActivity.timerType);
                            }
                        }
                        break;
                    case MSG_START_COUNTDOWN:
                        if (!mActivity.timer.isStopped()) {
                            mActivity.timer.setTime(mActivity.hmsTime[0], mActivity.hmsTime[1],
                                    mActivity.hmsTime[2]);
                            // Convert time back to milliseconds
                            mActivity.background.startAnimation(mActivity.time,
                                    mActivity.backgroundColor);
                        }

                        mActivity.timer.start();
                        mActivity.background.resumeAnimation(MyConstants.NO_PROGRESS);
                        sendEmptyMessage(MSG_UPDATE_COUNTDOWN);
                        break;
                    case MSG_STOP_COUNTDOWN:
                        removeMessages(MSG_UPDATE_COUNTDOWN);
                        mActivity.reset();
                        break;
                    case MSG_PAUSE_COUNTDOWN:
                        removeMessages(MSG_UPDATE_COUNTDOWN);
                        mActivity.background.pauseAnimaton(MyConstants.NO_PROGRESS);
                        mActivity.timer.pause();
                        break;
                    case MSG_UPDATE_COUNTDOWN:
                        if (!mActivity.isTimerFinished(hourTimer, minTimer, secTimer, milliTimer)) {
                            mActivity.displayCountdownTime(TimeFormatter.formatTimeToString(
                                    secTimer));
                            sendEmptyMessageDelayed(MSG_UPDATE_COUNTDOWN,
                                    UI_REFRESH_RATE);
                        } else {
                            sendEmptyMessage(MSG_STOP_COUNTDOWN);
                            mActivity.setContentView(mActivity.initLayout(mActivity.inflater));
                            mActivity.timerType = MyConstants.EXERCISE_TIME;
                            mActivity.runTimerPlan(mActivity.timerPlan, mActivity.timerType);
                        }
                        break;
                }
            }
        }

        private void getTimes(MainActivity mActivity) {
            milliTimer = mActivity.timer.getElapsedTimeMilli();
            secTimer = mActivity.timer.getElapsedTimeSecs();
            minTimer = mActivity.timer.getElapsedTimeMinutes();
            hourTimer = mActivity.timer.getElapsedTimeHours();

            milliMainStopWatch = mActivity.mainStopWatch.getElapsedTimeMilli();
            secMainStopWatch = mActivity.mainStopWatch.getElapsedTimeSecs();
            minMainStopWatch = mActivity.mainStopWatch.getElapsedTimeMinutes();
            hourMainStopWatch = mActivity.mainStopWatch.getElapsedTimeHours();

            milliSplitStopWatch = mActivity.splitStopWatch.getElapsedTimeMilli();
            secSplitStopWatch = mActivity.splitStopWatch.getElapsedTimeSecs();
            minSplitStopWatch = mActivity.splitStopWatch.getElapsedTimeMinutes();
            hourSplitStopWatch = mActivity.splitStopWatch.getElapsedTimeHours();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        setContentView(initLayout(inflater));
        timePicker = new HmsPickerBuilder()
                .setFragmentManager(getSupportFragmentManager())
                .setStyleResId(R.style.CustomBetterPickerTheme);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handler == null)
            handler = new TimerHander(this);
        else
            handler.setTarget(this);

        if (sharedPrefs == null)
            sharedPrefs = getPreferences(Context.MODE_PRIVATE);

        switch (sharedPrefs.getInt(TYPE_OF_TIMER, NONE_TIMER_IS_RUNNING)) {
            case STOPWATCH_IS_RUNNING:
                if (sharedPrefs.getBoolean(SAVED_STOPWATCH_IS_STOPPED, false))
                    handler.sendEmptyMessage(MSG_RESUME_PAUSED_STOPWATCH);
                else
                    handler.sendEmptyMessage(MSG_RESUME_STOPWATCH);
                break;
            case TIMER_IS_RUNNING:
                if (!isTimerFinished(sharedPrefs.getLong(SAVED_TIMER_START_TIME, 0))) {
                    if (sharedPrefs.getBoolean(SAVED_TIMER_IS_STOPPED, false)) {
                        handler.sendEmptyMessage(MSG_RESUME_PAUSED_TIMER);
                    } else {
                        handler.sendEmptyMessage(MSG_RESUME_TIMER);
                    }
                } else {
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putLong(SAVED_TIMER_START_TIME, 0);
                    editor.putInt(TYPE_OF_TIMER, NONE_TIMER_IS_RUNNING);
                    editor.commit();
                }
                break;
            case NONE_TIMER_IS_RUNNING:
                Log.i("SharedPreferences", "None timer is running !");
                break;
        }

        // Catch the TimerPlan which is sent as Parcelable via intent
        Intent intent = getIntent();
        if (intent.hasExtra(MyConstants.EXTRA_TIMERPLAN)) {
            Log.i("TimerPlan", "Successfully passed");
            timerPlan = intent.getParcelableExtra(MyConstants.EXTRA_TIMERPLAN);
            exerciseTime = timerPlan.getExerciseTime();
            restTime = timerPlan.getRestTime();
            countDownTime = timerPlan.getCountDown();
            repetitions = timerPlan.getRepetitions();
            timerType = MyConstants.COUNTDOWN_TIME;
            Log.i("TimerPlan", "ExerciseTime: " + exerciseTime);
            Log.i("TimerPlan", "RestTime: " + restTime);
            Log.i("TimerPlan", "CountDown: " + countDownTime);
            Log.i("TimerPlan", "Repetitions: " + repetitions);
            Log.i("TimerPlan", "Intensity: " + timerPlan.getIntensity());
            runTimerPlan(timerPlan, timerType);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (sharedPrefs == null)
            sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        if (mainStopWatch.isRunning()) {
            editor.putInt(TYPE_OF_TIMER, STOPWATCH_IS_RUNNING);
            editor.putLong(SAVED_MAIN_STOPWATCH_TIME, mainStopWatch.getStartTime());
            editor.putLong(SAVED_MAIN_STOPWATCH_ELAPSED_TIME, mainStopWatch.getPausedTime());
            editor.putLong(SAVED_SPLIT_STOPWATCH_TIME, splitStopWatch.getStartTime());
            editor.putLong(SAVED_SPLIT_STOPWATCH_ELAPSED_TIME, splitStopWatch.getPausedTime());
            editor.putBoolean(SAVED_STOPWATCH_IS_STOPPED, mainStopWatch.isStopped());
            handler.sendEmptyMessage(MSG_STOP_STOPWATCH);
            handler.removeMessages(MSG_UPDATE_STOPWATCH);
        }

        if (timer.isRunning()) {
            editor.putInt(TYPE_OF_TIMER, TIMER_IS_RUNNING);
            editor.putLong(SAVED_TIMER_START_TIME, timer.getStartTime());
            editor.putLong(SAVED_TIMER_DURATION, timer.getDuration());
            editor.putBoolean(SAVED_TIMER_IS_STOPPED, timer.isStopped());
            editor.putLong(SAVED_TIMER_ELAPSED_TIME, pausedTime);
            editor.putFloat(SAVED_TIMER_PROGRESS, timer.getProgress());
            handler.sendEmptyMessage(MSG_PAUSE_TIMER);
            handler.removeMessages(MSG_UPDATE_TIMER);
        }
        editor.commit();

        handler = null;
    }

    // TODO: Implement in order to recover decently when destroyed unexpectedly
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // TODO: Reconsider the layout for better performance (redundant removing and adding view here)
    private View initLayout(LayoutInflater inflater) {
        // Inflate xml layouts
        mainView = inflater.inflate(R.layout.activity_main, null);
        contentView = inflater.inflate(R.layout.content_main, null);
        CoordinatorLayout coordinatorLayout =
                (CoordinatorLayout) mainView.findViewById(R.id.coordinatorLayout);

        // Main parts of UI
        frameLayout = (FrameLayout) contentView.findViewById(R.id.framelayout_container);
        stopWatchLayout = (RelativeLayout) contentView.findViewById(R.id.stopwatch_layout);
        background = new CustomBackground(this);
        // Toolbar
        AppBarLayout appBarLayout = (AppBarLayout) mainView.findViewById(R.id.appBarLayout);
        Toolbar toolbar = (Toolbar) mainView.findViewById(R.id.toolbar);
        appBarLayout.removeAllViews();
        appBarLayout.addView(toolbar);
        setSupportActionBar(toolbar);
        // Floating Button
        FloatingActionButton fab = (FloatingActionButton) mainView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TimerEditActivity.class));
            }
        });

        // Initialize UI
        setUpTimers();
        startStop();
        lapReset();

        // Sets up recycler view for lap history
        setUpRecyclerView();

        frameLayout.removeAllViews();
        frameLayout.addView(background);
        frameLayout.addView(stopWatchLayout);

        coordinatorLayout.removeAllViews();
        coordinatorLayout.addView(appBarLayout);
        coordinatorLayout.addView(frameLayout);
        coordinatorLayout.addView(fab);
        return coordinatorLayout;
    }

    private View initCountdownLayout(LayoutInflater inflater) {
        // Inflate xml layouts
        mainView = inflater.inflate(R.layout.activity_main, null);
        contentView = inflater.inflate(R.layout.countdown_layout, null);
        CoordinatorLayout coordinatorLayout =
                (CoordinatorLayout) mainView.findViewById(R.id.coordinatorLayout);

        // Main parts of UI
        frameLayout = (FrameLayout) contentView.findViewById(R.id.framelayout_container_countDown);
        textView_countDownDisplay =
                (TextView) contentView.findViewById(R.id.textView_countdown_main);
        RelativeLayout countDownLayout =
                (RelativeLayout) contentView.findViewById(R.id.relativeLayout_countDown);
        background = new CustomBackground(this);
        // Toolbar
        AppBarLayout appBarLayout = (AppBarLayout) mainView.findViewById(R.id.appBarLayout);
        Toolbar toolbar = (Toolbar) mainView.findViewById(R.id.toolbar);
        appBarLayout.removeAllViews();
        appBarLayout.addView(toolbar);
        setSupportActionBar(toolbar);
        // Floating Button
        FloatingActionButton fab = (FloatingActionButton) mainView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TimerEditActivity.class));
            }
        });

        frameLayout.removeAllViews();
        frameLayout.addView(background);
        frameLayout.addView(countDownLayout);

        coordinatorLayout.removeAllViews();
        coordinatorLayout.addView(appBarLayout);
        coordinatorLayout.addView(frameLayout);
        coordinatorLayout.addView(fab);
        return coordinatorLayout;
    }

    // Set up RecyclerView for lap times
    private void setUpRecyclerView() {
        recyclerView_lapTimes = (RecyclerView) contentView.findViewById(R.id.recyclerView_lapTimes);
        recyclerView_lapTimes.setHasFixedSize(true);
        rVlayoutManager = new LinearLayoutManager(this);
        recyclerView_lapTimes.setLayoutManager(rVlayoutManager);
        mLapTimes = new ArrayList<>();
        rVadapter = new LapTimesAdapter(mLapTimes);
        recyclerView_lapTimes.setAdapter(rVadapter);
    }

    // Create textViews for Timers and register listener for the main one
    private void setUpTimers() {
        textView_timeDisplay = (TextView) contentView.findViewById(R.id.textView_timeDisplay);
        textView_secondaryTimeDisplay = (TextView) contentView.findViewById(R.id.textView_secondaryTime);

        // Create a reference to the listener in order to be sure that it exists
        // as long as activity does
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mainStopWatch.isRunning() && !timer.isRunning()) {
                    timePicker.show();
                }
            }
        };
        textView_timeDisplay.setOnClickListener(listener);
    }

    @Override
    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
        this.time = TimeFormatter.timeToMillis(hours, minutes, seconds);
        backgroundColor = Color.argb(255, 255, 64, 129);
        setTimerTime(time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Listener for the StartStop Button
    private void startStop() {
        startStopButton = (Button) contentView.findViewById(R.id.button_startStop);
        //Create a reference to a listener to be sure that listener exist as long as activity does
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!mainStopWatch.isRunning() && !timer.isRunning())
                        || mainStopWatch.isStopped() || timer.isStopped()) {
                    start();
                    setUpStartButton();
                } else {
                    if (mainStopWatch.isRunning() || timer.isRunning())
                        stop();
                    setUpStopButton();
                }
            }
        };

        startStopButton.setOnClickListener(listener);
    }

    private void setUpStartButton() {
        startStopButton.setTextColor(Color.parseColor("#cc0000"));
        startStopButton.setText(R.string.Stop);
        if (timerPlan == null && (mainStopWatch.isRunning() ||
                textView_timeDisplay.getText().equals(getResources().getString(
                        R.string.textView_default_time))))
            lapReset.setText(R.string.Lap);
        else {
            lapReset.setEnabled(false);
            lapReset.setText(R.string.Reset);
        }
    }

    private void setUpStopButton() {
        startStopButton.setTextColor(Color.parseColor("#99cc00"));
        startStopButton.setText(R.string.Start);
        lapReset.setEnabled(true);
        lapReset.setText(R.string.Reset);
    }

    // Listener for the lapReset Button
    private void lapReset() {
        lapReset = (Button) contentView.findViewById(R.id.button_lapReset);
        //Create a reference to a listener to be sure that listener exist as long as activity does
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainStopWatch.isStopped() || timer.isStopped()) {
                    reset();
                    timerPlan = null;
                    textView_secondaryTimeDisplay.setText(
                            getResources().getString(R.string.textView_default_time));
                } else {
                    if (mainStopWatch.isRunning())
                        lap();
                    else {
                        textView_timeDisplay.setText(R.string.textView_default_time);
                    }
                }
            }
        };
        lapReset.setOnClickListener(listener);
    }

    // Starts the StopWatch/Timer
    private void start() {
        if (timerPlan == null && (mainStopWatch.isRunning() || textView_timeDisplay.getText().equals(
                getResources().getString(R.string.textView_default_time))) && sharedPrefs.getInt(TYPE_OF_TIMER, NONE_TIMER_IS_RUNNING)
                != TIMER_IS_RUNNING) {
            handler.sendEmptyMessage(MSG_START_STOPWATCH);
        } else if (!textView_timeDisplay.getText().equals(
                getResources().getString(R.string.textView_default_time)) && timerType
                != MyConstants.COUNTDOWN_TIME &&
                sharedPrefs.getInt(TYPE_OF_TIMER, NONE_TIMER_IS_RUNNING) != STOPWATCH_IS_RUNNING) {
            handler.sendEmptyMessage(MSG_START_TIMER);
        } else if (timerType == MyConstants.COUNTDOWN_TIME) {
            handler.sendEmptyMessage(MSG_START_COUNTDOWN);
        }
    }

    // Stops the StopWatch/Timer
    private void stop() {
        if (timerPlan == null && ((mainStopWatch.isRunning() || textView_timeDisplay.getText()
                .equals(getResources().getString(R.string.textView_default_time)))
                && !timer.isRunning())) {
            handler.sendEmptyMessage(MSG_STOP_STOPWATCH);
        } else if (timer.isRunning() && !mainStopWatch.isRunning()) {
            handler.sendEmptyMessage(MSG_PAUSE_TIMER);
        }

        /*if (sharedPrefs == null)
            sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(TYPE_OF_TIMER, NONE_TIMER_IS_RUNNING);
        editor.commit();*/
    }

    // Laps the time
    private void lap() {
        if (mainStopWatch.isRunning()) {
            splitStopWatch.split();
            mLapTimes.add(TimeFormatter.formatTimeToString(
                    mainStopWatch.getElapsedTimeHours(),
                    mainStopWatch.getElapsedTimeMinutes(),
                    mainStopWatch.getElapsedTimeSecs(),
                    mainStopWatch.getElapsedTimeMilli()));
            rVadapter.notifyDataSetChanged();
        }
    }

    // Resets the time
    private void reset() {
        if (mainStopWatch.isRunning()) {
            mainStopWatch.resetTime();
            splitStopWatch.resetTime();
            textView_secondaryTimeDisplay.setText(getResources().getString(R.string.textView_default_time));
        } else if (timer.isRunning()) {
            timer.resetTime();
        }
        textView_timeDisplay.setText(R.string.textView_default_time);
        background.setFullScreenBackgroundColor(Color.argb(255, 250, 250, 250));
        background.invalidate();
        mLapTimes.clear();

        if (sharedPrefs == null)
            sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(TYPE_OF_TIMER, NONE_TIMER_IS_RUNNING);
        editor.commit();
    }

    public void setTimerTime(long time) {
        hmsTime = TimeFormatter.millisToHms(time);
        lapReset.setText(R.string.Reset);
        lapReset.setEnabled(true);
        displayTime(TimeFormatter.formatTimeToString(hmsTime[0], hmsTime[1], hmsTime[2], 0));
    }

    // TODO: Should be in Timer class
    private boolean isTimerFinished(long hour, long minute, long sec, long milli) {
        if ((hour == 0) && (minute == 0) && (sec == 0) && (milli == 0)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isTimerFinished(long startTime) {
        if (startTime <= System.currentTimeMillis())
            return true;
        else
            return false;
    }

    // TODO: make sure that timer is running in background if user clicked the fab during running
    // state, also make sure to properly stop -> start when running in background but user started
    // timerplan instead
    private void runTimerPlan(TimerPlan timerPlan, int type) {
        if (timerPlan != null) {
            if (type == MyConstants.COUNTDOWN_TIME) {
                setContentView(initCountdownLayout(inflater));
                time = countDownTime;
                setTimerTime(time);
                backgroundColor = Color.argb(255, 63, 81, 181);
            } else if (type == MyConstants.EXERCISE_TIME) {
                time = exerciseTime;
                setTimerTime(time);
                // TODO: Make resources for translation compatibility
                textView_secondaryTimeDisplay.setText("Exercise (" + numberOfCycles
                        + "/" + repetitions + ")");
                backgroundColor = Color.argb(255, 255, 64, 129);
            } else if (type == MyConstants.REST_TIME) {
                time = restTime;
                setTimerTime(time);
                textView_secondaryTimeDisplay.setText("Rest (" + numberOfCycles
                        + "/" + repetitions + ")");
                numberOfCycles++;
                backgroundColor = Color.argb(255, 0, 150, 136);
            }
            start();
        } else {
            Log.i("TimerPlan", "TimerPlan is null");
        }
    }

    // Display the main time
    private void displayTime(String time) {
        textView_timeDisplay.setText(time);
    }

    private void displaySecondaryTime(String time) {
        textView_secondaryTimeDisplay.setText(time);
    }

    private void displayCountdownTime(String time) {
        textView_countDownDisplay.setText(time);
    }

}
