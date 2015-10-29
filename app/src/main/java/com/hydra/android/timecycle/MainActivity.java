package com.hydra.android.timecycle;

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
import com.hydra.android.timecycle.TimerPlan.TimerEditActivity;
import com.hydra.android.timecycle.TimerPlan.TimerPlan;
import com.hydra.android.timecycle.Utils.MyConstants;
import com.hydra.android.timecycle.Utils.TimeFormatter;

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

    private final int UI_REFRESH_RATE = 100;
    private final int MSG_STOP_STOPWATCH = 0;
    private final int MSG_START_STOPWATCH = 1;
    private final int MSG_UPDATE_STOPWATCH = 2;
    private final int MSG_PAUSE_TIMER = 3;
    private final int MSG_START_TIMER = 4;
    private final int MSG_UPDATE_TIMER = 5;
    private final int MSG_STOP_TIMER = 6;
    private final int MSG_START_COUNTDOWN = 7;
    private final int MSG_STOP_COUNTDOWN = 8;
    private final int MSG_PAUSE_COUNTDOWN = 9;
    private final int MSG_UPDATE_COUNTDOWN = 10;
    private StopWatch mainStopWatch = new StopWatch("main");
    private StopWatch splitStopWatch = new StopWatch("split");
    private CountDownTimer timer = new CountDownTimer();
    private ArrayList<String> mLapTimes;
    private TimerPlan timerPlan;
    private int timerType;

    // Handler refreshes UI with the stopwatch time by the given UI_REFRESH_RATE
    // TODO: Check the leaks
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            long milliTimer = timer.getElapsedTimeMilli();
            long secTimer = timer.getElapsedTimeSecs();
            long minTimer = timer.getElapsedTimeMinutes();
            long hourTimer = timer.getElapsedTimeHours();

            long milliMainStopWatch = mainStopWatch.getElapsedTimeMilli();
            long secMainStopWatch = mainStopWatch.getElapsedTimeSecs();
            long minMainStopWatch = mainStopWatch.getElapsedTimeMinutes();
            long hourMainStopWatch = mainStopWatch.getElapsedTimeHours();

            long milliSplitStopWatch = splitStopWatch.getElapsedTimeMilli();
            long secSplitStopWatch = splitStopWatch.getElapsedTimeSecs();
            long minSplitStopWatch = splitStopWatch.getElapsedTimeMinutes();
            long hourSplitStopWatch = splitStopWatch.getElapsedTimeHours();

            switch (msg.what) {
                case MSG_START_STOPWATCH:
                    mainStopWatch.start();
                    splitStopWatch.start();
                    handler.sendEmptyMessage(MSG_UPDATE_STOPWATCH);
                    break;
                case MSG_STOP_STOPWATCH:
                    handler.removeMessages(MSG_UPDATE_STOPWATCH);
                    mainStopWatch.stop();
                    splitStopWatch.stop();
                    break;
                case MSG_UPDATE_STOPWATCH:
                    displayTime(TimeFormatter.formatTimeToString(hourMainStopWatch, minMainStopWatch,
                            secMainStopWatch, milliMainStopWatch));
                    displaySplitTime(TimeFormatter.formatTimeToString(hourSplitStopWatch,
                            minSplitStopWatch, secSplitStopWatch, milliSplitStopWatch));
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_STOPWATCH, UI_REFRESH_RATE);
                    break;
                case MSG_START_TIMER:
                    if (!timer.isStopped()) {
                        timer.setTime(hmsTime[0], hmsTime[1], hmsTime[2]);
                        // Convert time back to milliseconds
                        background.startAnimation(time, backgroundColor);
                    }
                    timer.start();
                    background.resumeAnimation();
                    startStopButton.setChecked(true);
                    handler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;
                case MSG_PAUSE_TIMER:
                    handler.removeMessages(MSG_UPDATE_TIMER);
                    background.pauseAnimaton();
                    timer.pause();
                    break;

                case MSG_STOP_TIMER:
                    handler.removeMessages(MSG_UPDATE_TIMER);
                    reset();
                    startStopButton.setChecked(false);
                    break;

                case MSG_UPDATE_TIMER:
                    if (!isTimerFinished(hourTimer, minTimer, secTimer, milliTimer)) {
                        displayTime(TimeFormatter.formatTimeToString(
                                hourTimer, minTimer, secTimer, milliTimer));
                        handler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, UI_REFRESH_RATE);
                    } else {
                        sendEmptyMessage(MSG_STOP_TIMER);

                        if (timerPlan == null || (numberOfCycles) > repetitions) {
                            textView_splitDisplay.setText(getResources()
                                    .getString(R.string.textView_default_time));
                            timerType = 0;
                            timerPlan = null;
                        }

                        if (timerPlan != null && numberOfCycles <= repetitions) {
                            timerType = (timerType + 1) % 2;
                            Log.i("Test", timerType + "");
                            runTimerPlan(timerPlan, timerType);
                        }
                    }
                    break;

                case MSG_START_COUNTDOWN:
                    if (!timer.isStopped()) {
                        timer.setTime(hmsTime[0], hmsTime[1], hmsTime[2]);
                        // Convert time back to milliseconds
                        background.startAnimation(time, backgroundColor);
                    }

                    timer.start();
                    background.resumeAnimation();
                    handler.sendEmptyMessage(MSG_UPDATE_COUNTDOWN);
                    break;

                case MSG_STOP_COUNTDOWN:
                    handler.removeMessages(MSG_UPDATE_COUNTDOWN);
                    reset();
                    break;

                case MSG_PAUSE_COUNTDOWN:
                    handler.removeMessages(MSG_UPDATE_COUNTDOWN);
                    background.pauseAnimaton();
                    timer.pause();
                    break;

                case MSG_UPDATE_COUNTDOWN:
                    if (!isTimerFinished(hourTimer, minTimer, secTimer, milliTimer)) {
                        displayCountdownTime(TimeFormatter.formatTimeToString(
                                secTimer));
                        handler.sendEmptyMessageDelayed(MSG_UPDATE_COUNTDOWN, UI_REFRESH_RATE);
                    } else {
                        sendEmptyMessage(MSG_STOP_COUNTDOWN);
                        setContentView(initLayout(inflater));
                        timerType = MyConstants.EXERCISE_TIME;
                        runTimerPlan(timerPlan, timerType);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        setContentView(initLayout(inflater));
        timePicker = new HmsPickerBuilder()
                .setFragmentManager(getSupportFragmentManager())
                .setStyleResId(R.style.CustomBetterPickerTheme);

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
