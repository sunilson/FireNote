package com.pro3.planner.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.EditText;

import com.pro3.planner.R;

public class LinedEditText extends EditText {
    private Rect mRect;
    private Paint mPaint;
    private boolean mEnabled;

    // we need this constructor for LayoutInflater
    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.primary_text_color, null)); //SET YOUR OWN COLOR HERE
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //int count = getLineCount();

        int height = getHeight();
        int line_height = getLineHeight();

        int count = height / line_height;

        if (getLineCount() > count)
            count = getLineCount();//for long text with scrolling

        Rect r = mRect;
        Paint paint = mPaint;
        int baseline = getLineBounds(0, r);//first line

        for (int i = 0; i < count; i++) {

            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
            baseline += getLineHeight();//next line
        }

        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            if (!mEnabled) return;
            super.setEnabled(false);
            super.setEnabled(mEnabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
        super.setEnabled(enabled);
    }
}