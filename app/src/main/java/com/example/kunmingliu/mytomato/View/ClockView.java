package com.example.kunmingliu.mytomato.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.example.kunmingliu.mytomato.Module.ClockText;
import com.example.kunmingliu.mytomato.Utils.Util;

import java.util.Calendar;

/**
 * Created by kunming.liu on 2017/9/15.
 */

public class ClockView extends abstractClockView implements View.OnTouchListener{
    private Paint mPaint = null;
    private int mRadius = 0;
    private int mX = 0;
    private int mY = 0;
    private int angle;
    private int mHour = 0;
    private int mMinute = 0;
    private int mSecond = 0;
    private Calendar cal = null;
    private Rect mHourRect = null;
    private Rect mMinuteRect = null;
    private int mBackGroundColor = Color.DKGRAY;
    private int mClickCircleColor = Color.GREEN;

    private SparseArray<Integer> mCosValues;
    private SparseArray<Integer> mSinValues;
    private SparseArray<ClockText> mClockTexts;
    private ClockText mClockText;
    private boolean isHour = true;
    private int touchPointX = 0;
    private int touchPointY = 0;

    public ClockView(Context context) {
        this(context, null, -1);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttr(attrs);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        setLayerType(LAYER_TYPE_SOFTWARE, mPaint);

        cal = Calendar.getInstance();
        mHour = cal.get(Calendar.HOUR);
        mMinute = cal.get(Calendar.MINUTE);
        mSecond = cal.get(Calendar.SECOND);

        mPaint.setTextSize(20);
        mHourRect = new Rect();
        mMinuteRect = new Rect();

        setOnTouchListener(this);
    }

    private void initAttr(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mX = getWidth() / 2;
        mY = getHeight() / 2;
        mRadius = getWidth() / 3;

        initCosValues();
        initSinValues();
        initClockText();

        mPaint.setColor(mBackGroundColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mX, mY, mRadius, mPaint);

        showAssistantLine(canvas,false);

        if(isHour){
            drawCircleOnHourText(canvas);
        }else{
            drawCircleOnMinuteText(canvas);
        }

        drawCenterCircle(canvas);
        drawTimeHands(canvas);
        drawTouchPoint(canvas);

    }
    private void drawCenterCircle(Canvas canvas){
        mPaint.setColor(mClickCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mX,mY,4,mPaint);

    }
    private void drawTimeHands(Canvas canvas){
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        for (int i = 0; i < 360; i = i + 30) {
            mClockText = mClockTexts.get(i);
            if (isHour) {
                canvas.drawText(mClockText.getHourText(), mClockText.getHourTextPosX(), mClockText.getHourTextPosY(), mPaint);
            } else {
                canvas.drawText(mClockText.getMinuteText(), mClockText.getMinuteTextPosX(), mClockText.getMinuteTextPosY(), mPaint);
            }
        }
    }
    private void drawCircleOnHourText(Canvas canvas){
        mPaint.setColor(mClickCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);

        angle = (int)covertHourToAngle(mHour,0);
        canvas.drawLine(mX,mY,mClockTexts.get(angle).getHourRect().centerX(),mClockTexts.get(angle).getHourRect().centerY(),mPaint);
        canvas.drawCircle(mClockTexts.get(angle).getHourRect().centerX(),
                mClockTexts.get(angle).getHourRect().centerY(),(float)(Math.toRadians(15)*mRadius),mPaint);

    }
    private void drawCircleOnMinuteText(Canvas canvas){
        mPaint.setColor(mClickCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);

        angle = (int)covertMinuteToAngle(mMinute);
        canvas.drawLine(mX,mY,mClockTexts.get(angle).getMinuteRect().centerX(),mClockTexts.get(angle).getMinuteRect().centerY(),mPaint);
        canvas.drawCircle(mClockTexts.get(angle).getHourRect().centerX(),
                mClockTexts.get(angle).getHourRect().centerY(),(float)(Math.toRadians(15)*mRadius),mPaint);
    }
    private void drawTouchPoint(Canvas canvas){
        if(touchPointX != 0 || touchPointY != 0){
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(5);
            canvas.drawPoint(touchPointX,touchPointY,mPaint);
        }
    }
    private void initCosValues() {
        if (mCosValues == null) {
            mCosValues = new SparseArray<>();
            for (int i = 0; i < 360; i = i + 6) {
                mCosValues.put(i, getCosLength(i, mRadius));
            }
        }
    }

    private void initSinValues() {
        if (mSinValues == null) {
            mSinValues = new SparseArray<>();
            for (int i = 0; i < 360; i = i + 6) {
                mSinValues.put(i, getSinLength(i, mRadius));
            }
        }
    }

    private void initClockText(){
        if (mClockTexts == null) {
            String hourString;
            String minuteString;
            int hourBaseLineX;
            int hourBaseLineY;
            int minuteBaseLineX;
            int minuteBaseLineY;

            mPaint.setTextSize(20);
            mHourRect = new Rect();
            mMinuteRect = new Rect();

            String[] mHourStrings = new String[]{"03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02"};
            String[] mMinuteStrings = new String[]{"15", "20", "25", "30", "35", "40", "45", "50", "55", "00", "05", "10"};

            mClockTexts = new SparseArray<>();
            for (int i = 0; i < 360; i = i + 6) {
                mClockText = new ClockText();
                hourString = mHourStrings[i / 30];
                minuteString = mMinuteStrings[i / 30];
                mPaint.getTextBounds(hourString, 0, hourString.length(), mHourRect);
                mPaint.getTextBounds(minuteString, 0, minuteString.length(), mMinuteRect);
                hourBaseLineX = mX+mCosValues.get(i);
                hourBaseLineY = mY+mSinValues.get(i);
                minuteBaseLineX = mX+mCosValues.get(i);
                minuteBaseLineY = mY+mSinValues.get(i);
                if(i == 0){
                    hourBaseLineY = hourBaseLineY + mHourRect.height()/2;
                    minuteBaseLineY = minuteBaseLineY + mMinuteRect.height()/2;
                }else if(i == 30 || i == 60){
                    hourBaseLineY = hourBaseLineY + mHourRect.height();
                    minuteBaseLineY = minuteBaseLineY + mMinuteRect.height();
                }else if(i == 90){
                    hourBaseLineX = hourBaseLineX - mHourRect.width()/2;
                    hourBaseLineY = hourBaseLineY + mHourRect.height();
                    minuteBaseLineX = minuteBaseLineX - mMinuteRect.width()/2;
                    minuteBaseLineY = minuteBaseLineY + mMinuteRect.height();
                }else if(i == 120 || i == 150){
                    hourBaseLineX = hourBaseLineX - mHourRect.width();
                    hourBaseLineY = hourBaseLineY + mHourRect.height();
                    minuteBaseLineX = minuteBaseLineX - mMinuteRect.width();
                    minuteBaseLineY = minuteBaseLineY + mMinuteRect.height();
                }else if(i == 180){
                    hourBaseLineX = hourBaseLineX - mHourRect.width();
                    hourBaseLineY = hourBaseLineY + mHourRect.height()/2;
                    minuteBaseLineX = minuteBaseLineX - mMinuteRect.width();
                    minuteBaseLineY = minuteBaseLineY + mMinuteRect.height()/2;
                }else if(i == 210|| i== 240){
                    hourBaseLineX = hourBaseLineX - mHourRect.width();
                    minuteBaseLineX = minuteBaseLineX - mMinuteRect.width();
                }else if(i == 270){
                    hourBaseLineX = hourBaseLineX - mHourRect.width()/2;
                    minuteBaseLineX = minuteBaseLineX - mMinuteRect.width()/2;
                }else{

                }
                mClockText.setHourText(hourString,hourBaseLineX,hourBaseLineY,mHourRect);
                mClockText.setMinuteText(minuteString,minuteBaseLineX,minuteBaseLineY,mHourRect);
                mClockTexts.put(i,mClockText);
            }
        }
    }

    public void setHour(boolean hour) {
        if(isHour != hour){
            isHour = hour;
            invalidate();
        }

    }
    private void showAssistantLine(Canvas canvas , boolean isShow){
        if(isShow){
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(1);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);

            Paint paint1 = new Paint();
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setAntiAlias(true);
            paint1.setStrokeWidth(1);
            paint1.setColor(Color.RED);
            paint1.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mX, mY, mRadius, paint);
            for(int i = 0 ; i < 360 ; i = i +30){
                canvas.drawLine(mX,mY,mX+mCosValues.get(i),mY+mSinValues.get(i) ,paint);
                canvas.drawCircle(mClockTexts.get(i).getHourRect().centerX(),
                        mClockTexts.get(i).getHourRect().centerY(),(float)(Math.toRadians(15)*mRadius),paint1);
                

            }

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Util.log("x = %d , y = %d",x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return false;
    }
}
