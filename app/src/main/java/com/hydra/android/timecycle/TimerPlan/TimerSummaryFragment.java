package com.hydra.android.timecycle.timerplan;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hydra.android.timecycle.mainui.MainActivity;
import com.hydra.android.timecycle.R;
import com.hydra.android.timecycle.utils.MyConstants;
import com.hydra.android.timecycle.utils.TimeFormatter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimerSummaryFragment.OnSummaryFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimerSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerSummaryFragment extends android.support.v4.app.Fragment {

    private long exerciseTime;
    private int[] exerciseHmsTime;
    private long restTime;
    private int[] restHmsTime;
    private int repetitions;
    private long countDown;
    private int[] countDownHmsTime;
    private float intensity;

    private TimerPlan timerPlan;
    private int id;

    private TextView textView_exerciseTime;
    private TextView textView_restTime;
    private TextView textView_repetitionTime;
    private TextView textView_countDown;
    private Button button_startTimerPlan;
    private Button button_saveTimerPlan;
    private Button button_resetTimerPlan;
    private String exerciseTimeString;
    private String restTimeString;
    private String countDownString;
    private boolean isVisible = false;

    private OnSummaryFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param exerciseTime Parameter 1.
     * @param restTime     Parameter 2.
     * @param repetitions  Parameter 3.
     * @param countDown    Parameter 4.
     * @param intensity    Parameter 5.
     * @return A new instance of fragment TimerSummaryFragment.
     */
    public static TimerSummaryFragment newInstance(long exerciseTime, long restTime,
                                                   int repetitions, long countDown,
                                                   float intensity) {
        TimerSummaryFragment fragment = new TimerSummaryFragment();
        Bundle args = new Bundle();
        args.putLong(MyConstants.ARG_EXERCISE_TIME, exerciseTime);
        args.putLong(MyConstants.ARG_REST_TIME, restTime);
        args.putInt(MyConstants.ARG_REPETITIONS, repetitions);
        args.putLong(MyConstants.ARG_COUNTDOWN, countDown);
        args.putFloat(MyConstants.ARG_INTENSITY, intensity);
        fragment.setArguments(args);
        return fragment;
    }

    public TimerSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            getTimers(getArguments());
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_timer_summary, container, false);
        RatingBar ratingBarIntensity = (RatingBar) v.findViewById(R.id.ratingBar);
        textView_exerciseTime = (TextView) v.findViewById(R.id.textView_exerciseTime_value);
        textView_restTime = (TextView) v.findViewById(R.id.textView_restTime_value);
        textView_repetitionTime = (TextView) v.findViewById(R.id.textView_repetitions_value);
        textView_countDown = (TextView) v.findViewById(R.id.textView_countdown_value);
        getIntensity(ratingBarIntensity);
        setSummary(getArguments());
        initButtons(v);

        textView_exerciseTime = (TextView) v.findViewById(R.id.textView_exerciseTime_value);
        textView_restTime = (TextView) v.findViewById(R.id.textView_restTime_value);
        textView_repetitionTime = (TextView) v.findViewById(R.id.textView_repetitions_value);
        textView_countDown = (TextView) v.findViewById(R.id.textView_countdown_value);
        Log.i("onCreateView", "called");
        return v;
    }


    private void initButtons(View v) {
        button_startTimerPlan = (Button) v.findViewById(R.id.button_start_timerPlan);
        button_saveTimerPlan = (Button) v.findViewById(R.id.button_save_timerPlan);
        button_resetTimerPlan = (Button) v.findViewById(R.id.button_reset_timerPlan);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_start_timerPlan:
                        // TODO: Add option to settings to opt out "start & save"
                        saveTimerPlan();
                        runTimerPlan();
                        break;
                    case R.id.button_save_timerPlan:
                        saveTimerPlan();
                        break;
                    case R.id.button_reset_timerPlan:
                        resetTimerPlan();
                        break;
                }
            }
        };

        button_startTimerPlan.setOnClickListener(listener);
        button_saveTimerPlan.setOnClickListener(listener);
        button_resetTimerPlan.setOnClickListener(listener);
    }

    private void getTimers(Bundle timers) {
        exerciseTime = timers.getLong(MyConstants.ARG_EXERCISE_TIME);
        restTime = timers.getLong(MyConstants.ARG_REST_TIME);
        repetitions = timers.getInt(MyConstants.ARG_REPETITIONS);
        countDown = timers.getLong(MyConstants.ARG_COUNTDOWN);
        intensity = timers.getFloat(MyConstants.ARG_INTENSITY);
    }

    // TODO: This smells so much as redundant !!!
    private void saveTimers(Bundle summaryBundle) {
        getArguments().putLong(MyConstants.ARG_EXERCISE_TIME,
                summaryBundle.getLong(MyConstants.ARG_EXERCISE_TIME));
        getArguments().putLong(MyConstants.ARG_REST_TIME,
                summaryBundle.getLong(MyConstants.ARG_REST_TIME));
        getArguments().putLong(MyConstants.ARG_COUNTDOWN,
                summaryBundle.getLong(MyConstants.ARG_COUNTDOWN));
        getArguments().putInt(MyConstants.ARG_REPETITIONS,
                summaryBundle.getInt(MyConstants.ARG_REPETITIONS));
        getArguments().putFloat(MyConstants.ARG_INTENSITY,
                summaryBundle.getFloat(MyConstants.ARG_INTENSITY));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get SharedPreferences and restore id for TimerPlan
        SharedPreferences sharedPreferences
                = getActivity().getPreferences(Context.MODE_PRIVATE);
        id = sharedPreferences.getInt(getResources().getString(R.string.sharedPreferences_id), 0);
        Log.i("onResume", "called");
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save the auto-incremented value for TimerPlan id
        SharedPreferences sharedPreferences
                = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getResources().getString(R.string.sharedPreferences_id), id);
        editor.commit();
        Log.i("onPause", "called");
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSummaryFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnExerciseFragmentInteractionListener");
        }
        Log.i("onAttach", "called");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.i("onDetach", "called");
    }

    // TODO: Change it to not create it everytime user slides to this fragment
    private void getIntensity(RatingBar ratingBarIntensity) {
        RatingBar.OnRatingBarChangeListener listener = new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                intensity = rating;
                if (mListener != null) {
                    mListener.onSummaryFragmentInteraction(intensity);
                }
            }
        };
        ratingBarIntensity.setOnRatingBarChangeListener(listener);
    }

    public void setSummary(Bundle summaryBundle) {
        getTimers(summaryBundle);
        exerciseHmsTime = TimeFormatter.millisToHms(this.exerciseTime);
        restHmsTime = TimeFormatter.millisToHms(this.restTime);
        countDownHmsTime = TimeFormatter.millisToHms(this.countDown);

        exerciseTimeString =
                TimeFormatter.formatTimeToString(exerciseHmsTime[0], exerciseHmsTime[1],
                        exerciseHmsTime[2], 0);
        restTimeString =
                TimeFormatter.formatTimeToString(restHmsTime[0], restHmsTime[1],
                        restHmsTime[2], 0);
        countDownString =
                TimeFormatter.formatTimeToString(countDownHmsTime[1],
                        countDownHmsTime[2]);
        displayTimers();
        saveTimers(summaryBundle);
    }

    private void displayTimers() {
        textView_exerciseTime.setText(exerciseTimeString);
        textView_exerciseTime.invalidate();
        textView_restTime.setText(restTimeString);
        textView_restTime.invalidate();
        textView_countDown.setText(countDownString);
        textView_countDown.invalidate();
        textView_repetitionTime.setText(repetitions + "x");
        textView_repetitionTime.invalidate();
    }

    private void runTimerPlan() {
        if (timerPlan != null) {
            Intent timerPlanIntent = new Intent(getContext(), MainActivity.class);
            timerPlanIntent.putExtra(MyConstants.EXTRA_TIMERPLAN, timerPlan);
            startActivity(timerPlanIntent);
        } else {
            Log.e("TimerPlan", "TimerPlan is null !");
        }
    }

    private void saveTimerPlan() {
        if (exerciseTime > 0) {
            timerPlan = new TimerPlan.Builder(id++)
                    .setExerciseTime(exerciseTime)
                    .setRestTime(restTime)
                    .setRepetitions(repetitions)
                    .setCountDown(countDown)
                    .setIntensity(intensity)
                    .build();
            // TODO: Reset the times in order to prevent multiple entries when double clicked etc.
        }

    }

    private void resetTimerPlan() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSummaryFragmentInteractionListener {
        public void onSummaryFragmentInteraction(float intensity);
    }
}
