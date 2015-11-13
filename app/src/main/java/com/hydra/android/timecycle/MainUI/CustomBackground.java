package com.hydra.android.timecycle.mainui;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hydra.android.timecycle.utils.MyConstants;

/**
 * Created by jslapnicka on 14.10.2015.
 */
public class CustomBackground extends View {
    private Paint mPaint;
    private float width;
    private float height;
    private float x;
    private long mDuration;
    private ObjectAnimator animator;

    public CustomBackground(Context context) {
        super(context);
        mPaint = new Paint();
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        FloatEvaluator evaluator = new FloatEvaluator();
        animator = ObjectAnimator.ofObject(
                this, "transition", evaluator, 0f, width);
        animator.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, x, height, mPaint);
    }

    public void setBackgroundColor(int color) {
        mPaint.setColor(color);
    }

    public void setFullScreenBackgroundColor(int color) {
        mPaint.setColor(color);
        x = width;
    }

    public void setTransition(float x) {
        this.x = x;
        invalidate();
    }

    public void setDuration(long duration) {
        mDuration = duration;
        animator.setDuration(mDuration);
    }

    public void startAnimation(long duration, int color) {
        animator.setCurrentFraction(0F);
        mPaint.setColor(color);
        setDuration(duration);
        animator.start();
    }

    public void pauseAnimaton(float progress) {
        if (progress > MyConstants.NO_PROGRESS) {
            animator.setCurrentFraction(progress);
        } else {
            animator.pause();
        }
    }

    public void resumeAnimation(float progress) {
        if (progress > MyConstants.NO_PROGRESS) {
            animator.start();
            animator.setCurrentFraction(progress);
        } else {
            animator.resume();
        }

    }

    public void stopAnimation() {
        animator.cancel();
    }

}