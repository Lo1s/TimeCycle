package com.hydra.android.timecycle.timerplan;

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
import com.hydra.android.timecycle.utils.MyConstants;
import com.hydra.android.timecycle.R;
import com.hydra.android.timecycle.utils.TimeFormatter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnCountDownFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimerCountDownFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerCountDownFragment extends android.support.v4.app.Fragment
        implements HmsPickerDialogFragment.HmsPickerDialogHandler {

    private long countDownTime;
    private int[] hmsTime;

    private TextView textView_countDownTime;
    private HmsPickerBuilder timePicker;
    private Button btnPlus1sec;
    private Button btnPlus5sec;
    private Button btnMin1sec;
    private Button btnMin5sec;

    private OnCountDownFragmentInteractionListener mListener;

    @Override
    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
        this.countDownTime = TimeFormatter.timeToMillis(hours, minutes, seconds);
        setCountDownTime();
    }

    private void setCountDownTime() {
        hmsTime = TimeFormatter.millisToHms(this.countDownTime);
        displayTime(TimeFormatter.formatTimeToString(hmsTime[1], hmsTime[2]));
        // Save it to arguments
        getArguments().putLong(MyConstants.ARG_COUNTDOWN, countDownTime);
    }

    private void displayTime(String time) {
        textView_countDownTime.setText(time);
        textView_countDownTime.invalidate();
        if (mListener != null) {
            mListener.onCountDownFragmentInteraction(this.countDownTime);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param countDownTime Parameter 1.
     * @return A new instance of fragment TimerCountDownFragment.
     */
    public static TimerCountDownFragment newInstance(long countDownTime) {
        TimerCountDownFragment fragment = new TimerCountDownFragment();
        Bundle args = new Bundle();
        args.putLong(MyConstants.ARG_COUNTDOWN, countDownTime);
        fragment.setArguments(args);
        return fragment;
    }

    public TimerCountDownFragment() {
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
            countDownTime = getArguments().getLong(MyConstants.ARG_COUNTDOWN);
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_timer_count_down, container, false);
        textView_countDownTime = (TextView) v.findViewById(R.id.textView_setCountdownTime);
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
        textView_countDownTime.setOnClickListener(listener);

        initButtons(v);
        setCountDownTime();

        return v;
    }

    private void initButtons(View v) {
        btnPlus1sec = (Button) v.findViewById(R.id.button_countdownTime_plus1sec);
        btnPlus5sec = (Button) v.findViewById(R.id.button_countdownTime_plus5sec);
        btnMin1sec = (Button) v.findViewById(R.id.button_countdownTime_minus1sec);
        btnMin5sec = (Button) v.findViewById(R.id.button_countdownTime_minus5sec);


        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTime(v);
            }
        };

        btnPlus1sec.setOnClickListener(btnListener);
        btnPlus5sec.setOnClickListener(btnListener);
        btnMin1sec.setOnClickListener(btnListener);
        btnMin5sec.setOnClickListener(btnListener);
    }

    private void changeTime(View v) {
        switch (v.getId()) {
            case R.id.button_countdownTime_plus1sec:
                this.countDownTime += TimeFormatter.timeToMillis(0, 0, 1);
                setCountDownTime();
                break;
            case R.id.button_countdownTime_plus5sec:
                this.countDownTime += TimeFormatter.timeToMillis(0, 0, 5);
                setCountDownTime();
                break;
            case R.id.button_countdownTime_minus1sec:
                if (this.countDownTime >= TimeFormatter.timeToMillis(0, 0, 1)) {
                    this.countDownTime += TimeFormatter.timeToMillis(0, 0, -1);
                    setCountDownTime();
                }
                break;
            case R.id.button_countdownTime_minus5sec:
                if (this.countDownTime >= TimeFormatter.timeToMillis(0, 0, 5)) {
                    this.countDownTime += TimeFormatter.timeToMillis(0, 0, -5);
                    setCountDownTime();
                }
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCountDownFragmentInteractionListener) activity;
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
    public interface OnCountDownFragmentInteractionListener {
        public void onCountDownFragmentInteraction(long countDownTime);
    }

}
