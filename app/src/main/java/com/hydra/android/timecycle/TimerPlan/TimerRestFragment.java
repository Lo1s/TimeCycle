package com.hydra.android.timecycle.TimerPlan;

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
import com.hydra.android.timecycle.Utils.MyConstants;
import com.hydra.android.timecycle.R;
import com.hydra.android.timecycle.Utils.TimeFormatter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimerRestFragment.OnRestFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimerRestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerRestFragment extends android.support.v4.app.Fragment
        implements HmsPickerDialogFragment.HmsPickerDialogHandler {

    private long restTime;
    private int[] hmsTime;

    private TextView textView_restTime;
    private HmsPickerBuilder timePicker;
    private Button btnPlus30sec;
    private Button btnPlus1min;
    private Button btnPlus5min;
    private Button btnPlus10min;
    private Button btnMin30sec;
    private Button btnMin1min;
    private Button btnMin5min;
    private Button btnMin10min;

    private OnRestFragmentInteractionListener mListener;

    @Override
    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
        this.restTime = TimeFormatter.timeToMillis(hours, minutes, seconds);
        setRestTime();
    }

    private void setRestTime() {
        hmsTime = TimeFormatter.millisToHms(this.restTime);
        displayTime(TimeFormatter.formatTimeToString(hmsTime[0], hmsTime[1], hmsTime[2], 0));
        // Save it to arguments
        getArguments().putLong(MyConstants.ARG_REST_TIME, restTime);
    }

    private void displayTime(String time) {
        textView_restTime.setText(time);
        textView_restTime.invalidate();
        if (mListener != null) {
            mListener.onRestFragmentInteraction(this.restTime);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param restTime Parameter 1.
     * @return A new instance of fragment TimerRestFragment.
     */
    public static TimerRestFragment newInstance(long restTime) {
        TimerRestFragment fragment = new TimerRestFragment();
        Bundle args = new Bundle();
        args.putLong(MyConstants.ARG_REST_TIME, restTime);
        fragment.setArguments(args);
        return fragment;
    }

    public TimerRestFragment() {
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
            restTime = getArguments().getLong(MyConstants.ARG_REST_TIME);
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_timer_rest, container, false);
        textView_restTime = (TextView) v.findViewById(R.id.textView_setRestTime);
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

        textView_restTime.setOnClickListener(listener);

        initButtons(v);
        setRestTime();
        return v;
    }

    private void initButtons(View v) {
        btnPlus30sec = (Button) v.findViewById(R.id.button_restTime_plus30sec);
        btnPlus1min = (Button) v.findViewById(R.id.button_restTime_plus1min);
        btnPlus5min = (Button) v.findViewById(R.id.button_restTime_plus5min);
        btnPlus10min = (Button) v.findViewById(R.id.button_restTime_plus10min);
        btnMin30sec = (Button) v.findViewById(R.id.button_restTime_minus30sec);
        btnMin1min = (Button) v.findViewById(R.id.button_restTime_minus1min);
        btnMin5min = (Button) v.findViewById(R.id.button_restTime_minus5min);
        btnMin10min = (Button) v.findViewById(R.id.button_restTime_minus10min);

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
            case R.id.button_restTime_plus30sec:
                this.restTime += TimeFormatter.timeToMillis(0, 0, 30);
                setRestTime();
                break;
            case R.id.button_restTime_plus1min:
                this.restTime += TimeFormatter.timeToMillis(0, 1, 0);
                setRestTime();
                break;
            case R.id.button_restTime_plus5min:
                this.restTime += TimeFormatter.timeToMillis(0, 5, 0);
                setRestTime();
                break;
            case R.id.button_restTime_plus10min:
                this.restTime += TimeFormatter.timeToMillis(0, 10, 0);
                setRestTime();
                break;
            case R.id.button_restTime_minus30sec:
                if (restTime >= TimeFormatter.timeToMillis(0, 0, 30)) {
                    this.restTime += TimeFormatter.timeToMillis(0, 0, -30);
                    setRestTime();
                }
                break;
            case R.id.button_restTime_minus1min:
                if (restTime >= TimeFormatter.timeToMillis(0, 1, 0)) {
                    this.restTime += TimeFormatter.timeToMillis(0, -1, 0);
                    setRestTime();
                }
                break;
            case R.id.button_restTime_minus5min:
                if (restTime >= TimeFormatter.timeToMillis(0, 5, 0)) {
                    this.restTime += TimeFormatter.timeToMillis(0, -5, 0);
                    setRestTime();
                }
                break;
            case R.id.button_restTime_minus10min:
                if (restTime >= TimeFormatter.timeToMillis(0, 10, 0)) {
                    this.restTime += TimeFormatter.timeToMillis(0, -10, 0);
                    setRestTime();
                }
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRestFragmentInteractionListener) activity;
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
    public interface OnRestFragmentInteractionListener {
        public void onRestFragmentInteraction(long restTime);
    }

}
