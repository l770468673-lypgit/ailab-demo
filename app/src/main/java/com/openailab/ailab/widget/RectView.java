package com.openailab.ailab.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.openailab.ailab.R;

/**
 * @author ZyElite
 */
public class RectView extends View {
    private Rect rect;
    private Paint mPaint;
    private Paint mTpaint;
    private Paint mTemperature;

    private Paint mRoundRectPaint;
    private String str = "";
    private Bitmap mBitmap;

    private int mRoundRectHeight;
    private int mRoundRectWidth;

    private int mAbnormal = Color.parseColor("#BA2700");
    private int mNormal = Color.parseColor("#FFFFFF");

    private int mAbnormalBg = Color.parseColor("#33BA2700");
    private int mNormalBg = Color.parseColor("#03C81D");

    private int mMasksBg = Color.parseColor("#EB8D00");

    private float mRemaining;

    private float mOffsetX;
    private float mOffset;
    private int mMargin;
    private String mTemperatureText;

    public RectView(Context context) {
        this(context, null);
    }

    public RectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_matrix);

        mOffset = mTpaint.measureText("体温：");
        mRemaining = mRoundRectWidth - mOffset;
        float tx = mTemperature.measureText("37.9");
        mOffsetX = (mRemaining - tx) / 2;
        mMargin = dp2px(30);
    }


    public void setState(int state) {
        if (state == 2) {
            mTpaint.setColor(mNormal);
            mTemperature.setColor(mNormal);
            mPaint.setColor(Color.GREEN);
            mRoundRectPaint.setColor(mNormalBg);
        } else if (state == 3) {
            mTpaint.setColor(mAbnormal);
            mTemperature.setColor(mAbnormal);
            mPaint.setColor(mAbnormal);
            mRoundRectPaint.setColor(mAbnormalBg);
        } else if (state == 4) {
            mPaint.setColor(mMasksBg);
        }

    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(6F);
        mPaint.setColor(Color.GREEN);


        mTpaint = new Paint();
        mTpaint.setAntiAlias(true);
        mTpaint.setColor(Color.GREEN);
        mTpaint.setStrokeWidth(15F);
        mTpaint.setTextAlign(Paint.Align.LEFT);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mTpaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 54F, displayMetrics));

        mTemperature = new Paint();
        mTemperature.setAntiAlias(true);
        mTemperature.setColor(Color.GREEN);
        mTemperature.setStrokeWidth(20F);
        mTemperature.setTextAlign(Paint.Align.LEFT);
        mTemperature.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 72F, displayMetrics));

        mRoundRectPaint = new Paint();
        mRoundRectPaint.setAntiAlias(true);
        mRoundRectPaint.setColor(Color.parseColor("#03C81D"));
        mRoundRectPaint.setStyle(Paint.Style.FILL);

        mRoundRectHeight = dp2px(130);
        mRoundRectWidth = dp2px(370);
    }

    public void drawFaceRect(Rect rect) {
        this.rect = rect;
        postInvalidate();
    }

    public void clearRect() {
        rect = new Rect(-40, -40, -40, -40);
        mTemperatureText = "";
        postInvalidate();
    }

    public void setTemperature(String text) {
        mTemperatureText = text;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rect != null) {
            canvas.drawLine(rect.left, rect.top, rect.left, rect.top + 30, mPaint);
            canvas.drawLine(rect.left, rect.top + 30 + 5, rect.left, rect.top + 30 + 5 + 10, mPaint);
            canvas.drawLine(rect.left, rect.top, rect.left + 30, rect.top, mPaint);
            canvas.drawLine(rect.left + 30 + 5, rect.top, rect.left + 30 + 5 + 10, rect.top, mPaint);
            canvas.drawLine(rect.right, rect.top, rect.right - 30, rect.top, mPaint);
            canvas.drawLine(rect.right - 30 - 5, rect.top, rect.right - 30 - 5 - 10, rect.top, mPaint);
            canvas.drawLine(rect.right, rect.top, rect.right, rect.top + 30, mPaint);
            canvas.drawLine(rect.right, rect.top + 30 + 5, rect.right, rect.top + 30 + 5 + 10, mPaint);
            canvas.drawLine(rect.left, rect.bottom, rect.left, rect.bottom - 30, mPaint);
            canvas.drawLine(rect.left, rect.bottom - 30 - 5, rect.left, rect.bottom - 30 - 5 - 10, mPaint);
            canvas.drawLine(rect.left, rect.bottom, rect.left + 30, rect.bottom, mPaint);
            canvas.drawLine(rect.left + 30 + 5, rect.bottom, rect.left + 30 + 5 + 10, rect.bottom, mPaint);

            canvas.drawLine(rect.right, rect.bottom, rect.right, rect.bottom - 30, mPaint);
            canvas.drawLine(rect.right, rect.bottom - 30 - 5, rect.right, rect.bottom - 30 - 5 - 10, mPaint);
            canvas.drawLine(rect.right, rect.bottom, rect.right - 30, rect.bottom, mPaint);
            canvas.drawLine(rect.right - 30 - 5, rect.bottom, rect.right - 30 - 5 - 10, rect.bottom, mPaint);
            Rect mSrc = new Rect(0, 0, mBitmap.getWidth(), getHeight());
            Rect mDest = new Rect(rect.left, rect.top, rect.right, rect.bottom);
            canvas.drawBitmap(mBitmap, mSrc, mDest, mPaint);

            if (!mTemperatureText.isEmpty()) {
                RectF rel2 = new RectF(rect.left, rect.top - mRoundRectHeight - mMargin, rect.left + mRoundRectWidth, rect.top - mMargin);
                canvas.drawRoundRect(rel2, mRoundRectHeight, mRoundRectHeight, mRoundRectPaint);
                int offsetY = rect.top - mRoundRectHeight / 2;
                canvas.drawText("体温：", rect.left + mMargin, offsetY, mTpaint);
                canvas.drawText(mTemperatureText, rect.left + mOffset + mOffsetX, offsetY, mTemperature);
            }
        }
    }

    public int dp2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
