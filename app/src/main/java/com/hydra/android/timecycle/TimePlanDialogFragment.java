package com.hydra.android.timecycle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by jslapnicka on 20.10.2015.
 */
public class TimePlanDialogFragment extends DialogFragment {

    private EditText editText_exerciseTime;
    private EditText editText_restTime;
    private EditText editText_repetitions;
    private LayoutInflater inflater;
    private View dialogLayout;

    private String exerciseTime;
    private String restTime;
    private String repetitions;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();
        dialogLayout = inflater.inflate(R.layout.dialog_layout, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogLayout)
                // Add Title and Action buttons
                .setMessage(R.string.DialogTitle)
                .setPositiveButton(R.string.SaveRun, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Saved and Running !", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton(R.string.Save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Saved !", Toast.LENGTH_SHORT).show();
                        saveTimes();
                    }
                })
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        return builder.create();
    }

    private void saveTimes() {
        editText_exerciseTime = (EditText) dialogLayout.findViewById(R.id.editText_exerciseTime);
        editText_restTime = (EditText) dialogLayout.findViewById(R.id.editText_restTime);
        editText_repetitions = (EditText) dialogLayout.findViewById(R.id.editText_repetitions);

        exerciseTime = editText_exerciseTime.getText().toString();
        restTime = editText_restTime.getText().toString();
        repetitions = editText_repetitions.getText().toString();

        Log.i("Exercise time", exerciseTime);
        Log.i("Rest time", restTime);
        Log.i("Repetitions", repetitions);
    }


}
