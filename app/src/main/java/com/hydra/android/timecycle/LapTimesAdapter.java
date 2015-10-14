package com.hydra.android.timecycle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jslapnicka on 14.10.2015.
 */
public class LapTimesAdapter extends RecyclerView.Adapter<LapTimesAdapter.ViewHolder> {

    ArrayList<String> mLapTimes;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(TextView textView) {
            super(textView);
            mTextView = textView;
        }

    }

    public LapTimesAdapter(ArrayList<String> lapTimes) {
        mLapTimes = lapTimes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.textview_laptimes, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder((TextView) v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mLapTimes.get(position));
    }

    @Override
    public int getItemCount() {
        return mLapTimes.size();
    }
}
