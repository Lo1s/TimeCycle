package com.hydra.android.timecycle;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by jslapnicka on 14.10.2015.
 */
public class CustomBackground extends View {
    private Paint mPaint;
    private float width;
    private float height;
    private float x;
    private ObjectAnimator animator;

    public CustomBackground(Context context) {
        super(context);
        mPaint = new Paint();
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        FloatEvaluator evaluator = new FloatEvaluator();
        animator = ObjectAnimator.ofObject(
                this, "transition", evaluator, 0f, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(0, 0, x, height, mPaint);
    }

    public void setBackgroundColor(int color) {
        mPaint.setColor(color);
        x = width;
    }

    public void setTransition(float x) {
        this.x = x;
        invalidate();
    }

    public void startAnimation(long duration, int color) {
        mPaint.setColor(color);
        animator.setDuration(duration);
        animator.start();
    }

    public void pauseAnimaton() {
        animator.pause();
    }

    public void resumeAnimation() {
        animator.resume();
    }

    public void stopAnimation() {
        animator.cancel();
    }

}