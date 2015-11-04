package com.hydra.android.timecycle.timerplan;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.hydra.android.timecycle.utils.MyConstants;
import com.hydra.android.timecycle.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRepetitionsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimerRepetitionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerRepetitionsFragment extends android.support.v4.app.Fragment
        implements NumberPickerDialogFragment.NumberPickerDialogHandler {

    private int repetitions;

    private TextView textView_repetitions;
    private NumberPickerBuilder numberPicker;
    private Button btnPlus1;
    private Button btnPlus5;
    private Button btnMin1;
    private Button btnMin5;

    private OnRepetitionsFragmentInteractionListener mListener;

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal,
                                  boolean isNegative, double fullNumber) {
        this.repetitions = number;
        setRepetitions();
    }

    private void setRepetitions() {
        textView_repetitions.setText(this.repetitions + "x");
        textView_repetitions.invalidate();
        if (mListener != null) {
            mListener.onRepetitionsFragmentInteraction(this.repetitions);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param repetitions Parameter 1.
     * @return A new instance of fragment TimerRepetitionsFragment.
     */
    public static TimerRepetitionsFragment newInstance(int repetitions) {
        TimerRepetitionsFragment fragment = new TimerRepetitionsFragment();
        Bundle args = new Bundle();
        args.putInt(MyConstants.ARG_REPETITIONS, repetitions);
        fragment.setArguments(args);
        return fragment;
    }

    public TimerRepetitionsFragment() {
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
            repetitions = getArguments().getInt(MyConstants.ARG_REPETITIONS);
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_timer_repetitions, container, false);
        textView_repetitions = (TextView) v.findViewById(R.id.textView_setRepetition);
        numberPicker = new NumberPickerBuilder()
                .setMinNumber(0)
                .setDecimalVisibility(View.INVISIBLE)
                .setPlusMinusVisibility(View.INVISIBLE)
                .setFragmentManager(getChildFragmentManager())
                .setStyleResId(R.style.CustomBetterPickerTheme)
                .setTargetFragment(this);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPicker.show();
            }
        };
        textView_repetitions.setOnClickListener(listener);

        initButtons(v);
        setRepetitions();

        return v;
    }

    private void initButtons(View v) {
        btnPlus1 = (Button) v.findViewById(R.id.button_repetition_plus1);
        btnPlus5 = (Button) v.findViewById(R.id.button_repetition_plus5);
        btnMin1 = (Button) v.findViewById(R.id.button_repetition_minus1);
        btnMin5 = (Button) v.findViewById(R.id.button_repetition_minus5);


        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeReps(v);
            }
        };

        btnPlus1.setOnClickListener(btnListener);
        btnPlus5.setOnClickListener(btnListener);
        btnMin1.setOnClickListener(btnListener);
        btnMin5.setOnClickListener(btnListener);
    }

    private void changeReps(View v) {
        switch (v.getId()) {
            case R.id.button_repetition_plus1:
                this.repetitions += 1;
                setRepetitions();
                break;
            case R.id.button_repetition_plus5:
                this.repetitions += 5;
                setRepetitions();
                break;
            case R.id.button_repetition_minus1:
                if (this.repetitions >= 1) {
                    this.repetitions -= 1;
                    setRepetitions();
                }
                break;
            case R.id.button_repetition_minus5:
                if (this.repetitions >= 5) {
                    this.repetitions -= 5;
                    setRepetitions();
                }
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRepetitionsFragmentInteractionListener) activity;
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
    public interface OnRepetitionsFragmentInteractionListener {
        public void onRepetitionsFragmentInteraction(int repetitions);
    }

}
