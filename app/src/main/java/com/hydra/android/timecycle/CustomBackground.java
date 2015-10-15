package com.hydra.android.timecycle;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

    public CustomBackground(Context context) {
        super(context);
        mPaint = new Paint();
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        FloatEvaluator evaluator = new FloatEvaluator();
        ObjectAnimator animator = ObjectAnimator.ofObject(
                this, "transition", evaluator, 0f, width);
        animator.setDuration(10000);
        animator.start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);

        canvas.drawRect(0, 0, x, height, mPaint);
    }

    public void setTransition(float x) {
        this.x = x;
        invalidate();
    }

}