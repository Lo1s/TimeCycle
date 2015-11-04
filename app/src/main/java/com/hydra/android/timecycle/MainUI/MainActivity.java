package com.hydra.android.timecycle.mainui;

import android.content.Intent;
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
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;
import com.codetroopers.betterpickers.hmspicker.HmsPickerDialogFragment;
import com.hydra.android.timecycle.timers.CountDownTimer;
import com.hydra.android.timecycle.common.LapTimesAdapter;
import com.hydra.android.timecycle.R;
import com.hydra.android.timecycle.timers.StopWatch;
import com.hydra.android.timecycle.timerplan.TimerEditActivity;
import com.hydra.android.timecycle.timerplan.TimerPlan;
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
    private TextView textView_splitDisplay;
    private TextView textView_countDownDisplay;
    private RecyclerView recyclerView_lapTimes;
    private RecyclerView.LayoutManager rVlayoutManager;
    private RecyclerView.Adapter rVadapter;
    private ToggleButton startStopButton;
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
    private int numberOfCycles = 1;
    private int repetitions;
    private long exerciseTime;
    private long restTime;
    private long countDownTime;

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
    private StopWatch mainStopWatch = new StopWatch("main");
    private StopWatch splitStopWatch = new StopWatch("split");
    private CountDownTimer timer = new CountDownTimer();
    private ArrayList<String> mLapTimes;
    private TimerPlan timerPlan;
    private int timerType;

    // Handler refreshes UI with the stopwatch time by the given UI_REFRESH_RATE
    // made it static to prevent memory leaks
    private static class TimerHander extends Handler {

        private final WeakReference<MainActivity> mainActivityWeakReference;

        TimerHander(MainActivity activity) {
            mainActivityWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mActivity = mainActivityWeakReference.get();

            if (mActivity != null) {
                super.handleMessage(msg);

                long milliTimer = mActivity.timer.getElapsedTimeMilli();
                long secTimer = mActivity.timer.getElapsedTimeSecs();
                long minTimer = mActivity.timer.getElapsedTimeMinutes();
                long hourTimer = mActivity.timer.getElapsedTimeHours();

                long milliMainStopWatch = mActivity.mainStopWatch.getElapsedTimeMilli();
                long secMainStopWatch = mActivity.mainStopWatch.getElapsedTimeSecs();
                long minMainStopWatch = mActivity.mainStopWatch.getElapsedTimeMinutes();
                long hourMainStopWatch = mActivity.mainStopWatch.getElapsedTimeHours();

                long milliSplitStopWatch = mActivity.splitStopWatch.getElapsedTimeMilli();
                long secSplitStopWatch = mActivity.splitStopWatch.getElapsedTimeSecs();
                long minSplitStopWatch = mActivity.splitStopWatch.getElapsedTimeMinutes();
                long hourSplitStopWatch = mActivity.splitStopWatch.getElapsedTimeHours();

                switch (msg.what) {
                    case MainActivity.MSG_START_STOPWATCH:
                        mActivity.mainStopWatch.start();
                        mActivity.splitStopWatch.start();
                        mActivity.handler.sendEmptyMessage(MSG_UPDATE_STOPWATCH);
                        break;
                    case MSG_STOP_STOPWATCH:
                        mActivity.handler.removeMessages(MSG_UPDATE_STOPWATCH);
                        mActivity.mainStopWatch.stop();
                        mActivity.splitStopWatch.stop();
                        break;
                    case MSG_UPDATE_STOPWATCH:
                        mActivity.displayTime(TimeFormatter.formatTimeToString(hourMainStopWatch,
                                minMainStopWatch, secMainStopWatch, milliMainStopWatch));
                        mActivity.displaySplitTime(TimeFormatter.formatTimeToString(
                                hourSplitStopWatch, minSplitStopWatch, secSplitStopWatch,
                                milliSplitStopWatch));
                        mActivity.handler.sendEmptyMessageDelayed(MSG_UPDATE_STOPWATCH,
                                UI_REFRESH_RATE);
                        break;
                    case MSG_START_TIMER:
                        if (!mActivity.timer.isStopped()) {
                            mActivity.timer.setTime(mActivity.hmsTime[0], mActivity.hmsTime[1],
                                    mActivity.hmsTime[2]);
                            // Convert time back to milliseconds
                            mActivity.background.startAnimation(mActivity.time,
                                    mActivity.backgroundColor);
                        }
                        mActivity.timer.start();
                        mActivity.background.resumeAnimation();
                        mActivity.startStopButton.setChecked(true);
                        mActivity.handler.sendEmptyMessage(MSG_UPDATE_TIMER);
                        break;
                    case MSG_PAUSE_TIMER:
                        mActivity.handler.removeMessages(MSG_UPDATE_TIMER);
                        mActivity.background.pauseAnimaton();
                        mActivity.timer.pause();
                        break;

                    case MSG_STOP_TIMER:
                        mActivity.handler.removeMessages(MSG_UPDATE_TIMER);
                        mActivity.reset();
                        mActivity.startStopButton.setChecked(false);
                        break;

                    case MSG_UPDATE_TIMER:
                        if (!mActivity.isTimerFinished(hourTimer, minTimer, secTimer, milliTimer)) {
                            mActivity.displayTime(TimeFormatter.formatTimeToString(
                                    hourTimer, minTimer, secTimer, milliTimer));
                            mActivity.handler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER,
                                    UI_REFRESH_RATE);
                        } else {
                            sendEmptyMessage(MSG_STOP_TIMER);

                            if (mActivity.timerPlan == null ||
                                    (mActivity.numberOfCycles) > mActivity.repetitions) {
                                mActivity.textView_splitDisplay.setText(mActivity.getResources()
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
                        mActivity.background.resumeAnimation();
                        mActivity.handler.sendEmptyMessage(MSG_UPDATE_COUNTDOWN);
                        break;

                    case MSG_STOP_COUNTDOWN:
                        mActivity.handler.removeMessages(MSG_UPDATE_COUNTDOWN);
                        mActivity.reset();
                        break;

                    case MSG_PAUSE_COUNTDOWN:
                        mActivity.handler.removeMessages(MSG_UPDATE_COUNTDOWN);
                        mActivity.background.pauseAnimaton();
                        mActivity.timer.pause();
                        break;

                    case MSG_UPDATE_COUNTDOWN:
                        if (!mActivity.isTimerFinished(hourTimer, minTimer, secTimer, milliTimer)) {
                            mActivity.displayCountdownTime(TimeFormatter.formatTimeToString(
                                    secTimer));
                            mActivity.handler.sendEmptyMessageDelayed(MSG_UPDATE_COUNTDOWN,
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
        handler = new TimerHander(this);

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
        handler = null;
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
        textView_splitDisplay = (TextView) contentView.findViewById(R.id.textView_splitTime);

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
        startStopButton = (ToggleButton) contentView.findViewById(R.id.button_startStop);
        //Create a reference to a listener to be sure that listener exist as long as activity does
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    start();
                    startStopButton.setTextColor(Color.parseColor("#cc0000"));
                    if (timerPlan == null && (mainStopWatch.isRunning() || textView_timeDisplay.getText()
                            .equals(getResources().getString(R.string.textView_default_time))))
                        lapReset.setText(R.string.Lap);
                    else {
                        lapReset.setEnabled(false);
                    }
                } else {
                    if (mainStopWatch.isRunning() || timer.isRunning())
                        stop();
                    startStopButton.setTextColor(Color.parseColor("#99cc00"));
                    lapReset.setEnabled(true);
                    lapReset.setText(R.string.Reset);
                }
            }
        };
        startStopButton.setOnCheckedChangeListener(listener);
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
                    textView_splitDisplay.setText(
                            getResources().getString(R.string.textView_default_time));
                } else {
                    lap();
                }
            }
        };
        lapReset.setOnClickListener(listener);
    }

    // Starts the StopWatch/Timer
    private void start() {
        if (timerPlan == null && (mainStopWatch.isRunning() || textView_timeDisplay.getText().equals(
                getResources().getString(R.string.textView_default_time)))) {
            Log.i("start()", "Stopwatch start called");
            handler.sendEmptyMessage(MSG_START_STOPWATCH);
        } else if (!textView_timeDisplay.getText().equals(
                getResources().getString(R.string.textView_default_time))
                && timerType != MyConstants.COUNTDOWN_TIME) {
            Log.i("start()", "Timer start called");
            handler.sendEmptyMessage(MSG_START_TIMER);
        } else if (timerType == MyConstants.COUNTDOWN_TIME) {
            Log.i("start()", "Countdown start called");
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
            textView_splitDisplay.setText(getResources().getString(R.string.textView_default_time));
        } else if (timer.isRunning()) {
            timer.resetTime();
        }
        textView_timeDisplay.setText(R.string.textView_default_time);
        background.setBackgroundColor(Color.argb(255, 250, 250, 250));
        background.invalidate();
        mLapTimes.clear();
    }

    public void setTimerTime(long time) {
        hmsTime = TimeFormatter.millisToHms(time);
        lapReset.setText(R.string.Reset);
        lapReset.setEnabled(false);
        displayTime(TimeFormatter.formatTimeToString(hmsTime[0], hmsTime[1], hmsTime[2], 0));
    }

    private boolean isTimerFinished(long hour, long minute, long sec, long milli) {
        if ((hour == 0) && (minute == 0) && (sec == 0) && (milli == 0)) {
            return true;
        } else {
            return false;
        }
    }

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
                textView_splitDisplay.setText("Exercise (" + numberOfCycles
                        + "/" + repetitions + ")");
                backgroundColor = Color.argb(255, 255, 64, 129);
            } else if (type == MyConstants.REST_TIME) {
                time = restTime;
                setTimerTime(time);
                textView_splitDisplay.setText("Rest (" + numberOfCycles
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

    private void displaySplitTime(String time) {
        textView_splitDisplay.setText(time);
    }

    private void displayCountdownTime(String time) {
        textView_countDownDisplay.setText(time);
    }

}
