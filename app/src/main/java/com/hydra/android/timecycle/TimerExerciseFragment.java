package com.hydra.android.timecycle;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;
import com.codetroopers.betterpickers.hmspicker.HmsPickerDialogFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnExerciseFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimerExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerExerciseFragment extends android.support.v4.app.Fragment
        implements HmsPickerDialogFragment.HmsPickerDialogHandler {

    private long exerciseTime;
    private int[] hmsTime;

    private TextView textView_exerciseTime;
    private HmsPickerBuilder timePicker;
    private Button btnPlus30sec;
    private Button btnPlus1min;
    private Button btnPlus5min;
    private Button btnPlus10min;
    private Button btnMin30sec;
    private Button btnMin1min;
    private Button btnMin5min;
    private Button btnMin10min;

    private OnExerciseFragmentInteractionListener mListener;

    @Override
    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
        this.exerciseTime = TimeFormatter.timeToMillis(hours, minutes, seconds);
        setExerciseTime();
    }

    public void setExerciseTime() {
        hmsTime = TimeFormatter.millisToHms(this.exerciseTime);
        displayTime(TimeFormatter.formatTimeToString(hmsTime[0], hmsTime[1], hmsTime[2], 0));
        // Save it to the arguments
        getArguments().putLong(MyConstants.ARG_EXERCISE_TIME, exerciseTime);
    }

    private void displayTime(String time) {
        textView_exerciseTime.setText(time);
        textView_exerciseTime.invalidate();
        if (mListener != null) {
            mListener.onExerciseFragmentInteraction(this.exerciseTime);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param exerciseTime Parameter 1.
     * @return A new instance of fragment TimerExerciseFragment.
     */
    public static TimerExerciseFragment newInstance(long exerciseTime) {
        TimerExerciseFragment fragment = new TimerExerciseFragment();
        Bundle args = new Bundle();
        args.putLong(MyConstants.ARG_EXERCISE_TIME, exerciseTime);
        fragment.setArguments(args);
        return fragment;
    }

    public TimerExerciseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            exerciseTime = getArguments().getLong(MyConstants.ARG_EXERCISE_TIME);
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_timer_exercise, container, false);
        textView_exerciseTime = (TextView) v.findViewById(R.id.textView_setExerciseTime);
        timePicker = new HmsPickerBuilder()
                .setFragmentManager(getChildFragmentManager())
                .setStyleResId(R.style.CustomBetterPickerTheme)
                .setTargetFragment(this);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show();
            }
        };
        textView_exerciseTime.setOnClickListener(listener);

        initButtons(v);
        setExerciseTime();
        return v;
    }

    private void initButtons(View v) {
        btnPlus30sec = (Button) v.findViewById(R.id.button_exerciseTime_plus30sec);
        btnPlus1min = (Button) v.findViewById(R.id.button_exerciseTime_plus1min);
        btnPlus5min = (Button) v.findViewById(R.id.button_exerciseTime_plus5min);
        btnPlus10min = (Button) v.findViewById(R.id.button_exerciseTime_plus10min);
        btnMin30sec = (Button) v.findViewById(R.id.button_exerciseTime_minus30sec);
        btnMin1min = (Button) v.findViewById(R.id.button_exerciseTime_minus1min);
        btnMin5min = (Button) v.findViewById(R.id.button_exerciseTime_minus5min);
        btnMin10min = (Button) v.findViewById(R.id.button_exerciseTime_minus10min);

        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTime(v);
            }
        };

        btnPlus30sec.setOnClickListener(btnListener);
        btnPlus1min.setOnClickListener(btnListener);
        btnPlus5min.setOnClickListener(btnListener);
        btnPlus10min.setOnClickListener(btnListener);
        btnMin30sec.setOnClickListener(btnListener);
        btnMin1min.setOnClickListener(btnListener);
        btnMin5min.setOnClickListener(btnListener);
        btnMin10min.setOnClickListener(btnListener);
    }

    private void changeTime(View v) {
        switch (v.getId()) {
            case R.id.button_exerciseTime_plus30sec:
                this.exerciseTime += TimeFormatter.timeToMillis(0, 0, 30);
                setExerciseTime();
                break;
            case R.id.button_exerciseTime_plus1min:
                this.exerciseTime += TimeFormatter.timeToMillis(0, 1, 0);
                setExerciseTime();
                break;
            case R.id.button_exerciseTime_plus5min:
                this.exerciseTime += TimeFormatter.timeToMillis(0, 5, 0);
                setExerciseTime();
                break;
            case R.id.button_exerciseTime_plus10min:
                this.exerciseTime += TimeFormatter.timeToMillis(0, 10, 0);
                setExerciseTime();
                break;
            case R.id.button_exerciseTime_minus30sec:
                if (exerciseTime >= TimeFormatter.timeToMillis(0, 0, 30)) {
                    this.exerciseTime += TimeFormatter.timeToMillis(0, 0, -30);
                    setExerciseTime();
                }
                break;
            case R.id.button_exerciseTime_minus1min:
                if (exerciseTime >= TimeFormatter.timeToMillis(0, 1, 0)) {
                    this.exerciseTime += TimeFormatter.timeToMillis(0, -1, 0);
                    setExerciseTime();
                }
                break;
            case R.id.button_exerciseTime_minus5min:
                if (exerciseTime >= TimeFormatter.timeToMillis(0, 5, 0)) {
                    this.exerciseTime += TimeFormatter.timeToMillis(0, -5, 0);
                    setExerciseTime();
                }
                break;
            case R.id.button_exerciseTime_minus10min:
                if (exerciseTime >= TimeFormatter.timeToMillis(0, 10, 0)) {
                    this.exerciseTime += TimeFormatter.timeToMillis(0, -10, 0);
                    setExerciseTime();
                }
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnExerciseFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnExerciseFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnExerciseFragmentInteractionListener {
        public void onExerciseFragmentInteraction(long exerciseTime);
    }

}
