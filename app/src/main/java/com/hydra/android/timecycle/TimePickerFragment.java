package com.hydra.android.timecycle;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

/**
 * Created by jslapnicka on 16.10.2015.
 */
// TODO: Customize timer to add seconds
public class TimePickerFragment extends android.support.v4.app.DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle bundle) {
        activity = (MainActivity) getActivity();
        return super.onCreateView(inflater, container, bundle);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), this, 0, 0, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String timeString = activity.formatTime(hourOfDay, minute, 0, 0);
        activity.setTimerTime(hourOfDay, minute, 0);
        activity.displayTime(timeString);
    }
}
