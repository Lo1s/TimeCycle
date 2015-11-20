package com.hydra.android.timecycle.common;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hydra.android.timecycle.R;

import java.util.ArrayList;

/**
 * Created by jslapnicka on 14.10.2015.
 */
public class LapTimesAdapter extends RecyclerView.Adapter<LapTimesAdapter.ViewHolder> {

    ArrayList<String[]> mLapTimes;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewTitle;
        public TextView mTextViewTime;
        public ViewHolder(View view) {
            super(view);
            mTextViewTitle = (TextView) view.findViewById(R.id.textView_lapTime_title);
            mTextViewTime = (TextView) view.findViewById(R.id.textView_lapTime_time);
        }

    }

    public LapTimesAdapter(ArrayList<String[]> lapTimes) {
        mLapTimes = lapTimes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_laptimes, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextViewTitle.setText(mLapTimes.get(position)[0]);
        holder.mTextViewTime.setText(mLapTimes.get(position)[1]);
    }

    @Override
    public int getItemCount() {
        return mLapTimes.size();
    }
}
