package com.hydra.android.timecycle;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    // UI Components
    private TextView textView_timeDisplay;
    private TextView textView_splitDisplay;
    private RecyclerView recyclerView_lapTimes;
    private RecyclerView.LayoutManager rVlayoutManager;
    private RecyclerView.Adapter rVadapter;
    private ToggleButton startStopButton;
    private Button lapReset;
    private CustomBackground background;
    private FrameLayout frameLayout;
    private RelativeLayout stopWatchLayout;


    private final int UI_REFRESH_RATE = 100;
    private final int MSG_STOP_TIMER = 0;
    private final int MSG_START_TIMER = 1;
    private final int MSG_UPDATE_TIMER = 2;
    private StopWatch mainStopWatch = new StopWatch();
    private StopWatch splitStopWatch = new StopWatch();
    private ArrayList<String> mLapTimes;


    // Handler refreshes UI with the stopwatch time by the given UI_REFRESH_RATE
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_START_TIMER:
                    mainStopWatch.start();
                    splitStopWatch.start();
                    handler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;
                case MSG_STOP_TIMER:
                    handler.removeMessages(MSG_UPDATE_TIMER);
                    mainStopWatch.stop();
                    splitStopWatch.stop();
                    displayTime(formatTime(
                            mainStopWatch.getElapsedTimeHours(),
                            mainStopWatch.getElapsedTimeMinutes(),
                            mainStopWatch.getElapsedTimeSecs(),
                            mainStopWatch.getElapsedTimeMilli()));
                    break;
                case MSG_UPDATE_TIMER:
                    displayTime(formatTime(
                            mainStopWatch.getElapsedTimeHours(),
                            mainStopWatch.getElapsedTimeMinutes(),
                            mainStopWatch.getElapsedTimeSecs(),
                            mainStopWatch.getElapsedTimeMilli()));
                    displaySplitTime(formatTime(
                            splitStopWatch.getElapsedTimeHours(),
                            splitStopWatch.getElapsedTimeMinutes(),
                            splitStopWatch.getElapsedTimeSecs(),
                            splitStopWatch.getElapsedTimeMilli()));
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, UI_REFRESH_RATE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayout(getLayoutInflater()));
    }

    private View initLayout(LayoutInflater inflater) {
        // Inflate xml layouts
        View mainView = inflater.inflate(R.layout.activity_main, null);
        View contentView = inflater.inflate(R.layout.content_main, null);
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Initialize UI
        startStopButton = (ToggleButton) contentView.findViewById(R.id.button_startStop);
        lapReset = (Button) contentView.findViewById(R.id.button_lapReset);
        textView_timeDisplay = (TextView) contentView.findViewById(R.id.textView_timeDisplay);
        textView_splitDisplay = (TextView) contentView.findViewById(R.id.textView_splitTime);
        startStop();
        lapReset();
        // Set up RecyclerView for lap times
        recyclerView_lapTimes = (RecyclerView) contentView.findViewById(R.id.recyclerView_lapTimes);
        recyclerView_lapTimes.setHasFixedSize(true);
        rVlayoutManager = new LinearLayoutManager(this);
        recyclerView_lapTimes.setLayoutManager(rVlayoutManager);
        mLapTimes = new ArrayList<>();
        rVadapter = new LapTimesAdapter(mLapTimes);
        recyclerView_lapTimes.setAdapter(rVadapter);

        frameLayout.removeAllViews();
        frameLayout.addView(background);
        frameLayout.addView(stopWatchLayout);

        coordinatorLayout.removeAllViews();
        coordinatorLayout.addView(appBarLayout);
        coordinatorLayout.addView(frameLayout);
        coordinatorLayout.addView(fab);
        return coordinatorLayout;
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
        //Create a reference to a listener to be sure that listener exist as long as activity does
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    start();
                    startStopButton.setTextColor(Color.parseColor("#cc0000"));
                    lapReset.setText(R.string.Lap);
                } else {
                    stop();
                    startStopButton.setTextColor(Color.parseColor("#99cc00"));
                    lapReset.setText(R.string.Reset);
                }
            }
        };
        startStopButton.setOnCheckedChangeListener(listener);
    }

    // Starts the StopWatch
    private void start() {
        handler.sendEmptyMessage(MSG_START_TIMER);
    }

    // Stops the StopWatch
    private void stop() {
        handler.sendEmptyMessage(MSG_STOP_TIMER);
    }

    // Listener for the lapReset Button
    private void lapReset() {
        //Create a reference to a listener to be sure that listener exist as long as activity does
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainStopWatch.isStopped()) {
                    reset();
                } else {
                    lap();
                }
            }
        };
        lapReset.setOnClickListener(listener);
    }

    // Laps the time
    private void lap() {
        splitStopWatch.split();
        mLapTimes.add(formatTime(
                mainStopWatch.getElapsedTimeHours(),
                mainStopWatch.getElapsedTimeMinutes(),
                mainStopWatch.getElapsedTimeSecs(),
                mainStopWatch.getElapsedTimeMilli()));
        rVadapter.notifyDataSetChanged();
    }

    // Resets the time
    private void reset() {
        mainStopWatch.resetTime();
        splitStopWatch.resetTime();
        textView_timeDisplay.setText(R.string.textView_default_time);
        textView_splitDisplay.setText(R.string.textView_default_time);
        mLapTimes.clear();
    }

    // Display the main time
    private void displayTime(String time) {
        textView_timeDisplay.setText(time);
    }

    private void displaySplitTime(String time) {
        textView_splitDisplay.setText(time);
    }

    // Format the time
    private String formatTime(long hour, long minute, long second, long milli) {
        StringBuilder builder = new StringBuilder();
        String minuteString;
        String secondString;

        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = "" + minute;
        }

        if (second < 10) {
            secondString = "0" + second;
        } else {
            secondString = "" + second;
        }


        builder.append(hour + ":");
        builder.append(minuteString + ":");
        builder.append(secondString + ",");
        builder.append(milli + "");

        return builder.toString();
    }
}
