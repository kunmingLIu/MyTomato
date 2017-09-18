package com.example.kunmingliu.mytomato.View;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
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

public class ClockViewWithPath extends abstractClockView implements View.OnTouchListener{
    public static int HOUR_TO_MINUTE = 0;
    public static int MINUTE_TO_HOUR = 1;
    private Paint mPaint = null;

    private int mRadius1 = 0;
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

    private SparseArray<ClockText> mClockTexts;
    private ClockText mClockText;
    private boolean isHour = true;
    private int touchPointX = 0;
    private int touchPointY = 0;
    private Path clockPath = null;
    private PathMeasure pathMeasure = null;
    private float[] hourPos = null;

    public ClockViewWithPath(Context context) {
        this(context, null, -1);
    }

    public ClockViewWithPath(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ClockViewWithPath(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        clockPath = new Path();
        pathMeasure = new PathMeasure();

        setOnTouchListener(this);

        hourPos = new float[2];


    }

    private void initAttr(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mX = getWidth() / 2;
        mY = getHeight() / 2;
        mRadius1 = getWidth() / 3;
        clockPath.addCircle(mX,mY,mRadius1, Path.Direction.CW);
        pathMeasure.setPath(clockPath,false);
        angle = (int)covertHourToAngle(mHour,0);
        pathMeasure.getPosTan((float)(mRadius1*Math.toRadians(angle)),hourPos,null);
        initClockText(pathMeasure);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(clockPath,mPaint);
        showAssistantLine(canvas,true);

        drawCircleOnText(canvas,(int)hourPos[0],(int)hourPos[1]);
//
        drawCenterCircle(canvas);
        drawTimeHands(canvas);

    }

    /**
     * 中間的小圓
     * @param canvas
     */
    private void drawCenterCircle(Canvas canvas){
        mPaint.setColor(mClickCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mX,mY,4,mPaint);

    }

    /**
     * 畫上時或分的數字
     * @param canvas
     */
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

    /**
     * 在現在的時間或是所挑選的時間的文字上，畫上一個圓圈
     * @param canvas
     * @param x 現在的時間或是所挑選的時間的文字的中心點x
     * @param y 現在的時間或是所挑選的時間的文字的中心點y
     */
    private void drawCircleOnText(Canvas canvas, int x , int y){
        mPaint.setColor(mClickCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);

        canvas.drawLine(mX,mY,x,y,mPaint);
        canvas.drawCircle(x,y,(float)(Math.toRadians(15)*(mRadius1-10)),mPaint);

        /*
        如果不是5整數分的話，就多畫上一個小白點
        如：36,18,4...
         */
        if(!isHour){
            if(mMinute % 5 != 0 ){
                mPaint.setColor(Color.WHITE);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(x,y,3,mPaint);
            }
        }
    }
    private void drawTouchPoint(Canvas canvas){
        if(touchPointX != 0 || touchPointY != 0){
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(5);
            canvas.drawPoint(touchPointX,touchPointY,mPaint);
        }
    }

    /**
     * 改變現在時鐘的數值類型(小時或是分鐘)。當發生改變的時候，啟動動畫。
     * @param hour
     */
    public void setHour(boolean hour) {
        if(isHour != hour){
            if(isHour == true){
                startAnim(HOUR_TO_MINUTE,mHour,mMinute);
            }else{
                startAnim(MINUTE_TO_HOUR,mMinute,mHour);
            }
            isHour = hour;
            postInvalidate();//雖然動畫完成後，也會刷新畫面。但是要先刷新時鐘上的刻度
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
            canvas.drawCircle(mX, mY, mRadius1, paint);
            PathMeasure pathMeasure = new PathMeasure();
            pathMeasure.setPath(clockPath,false);
            float[] pos = new float[2];
            for(int i = 0 ; i < 360 ; i = i +30){
                pathMeasure.getPosTan((float)mRadius1*(float)Math.toRadians(i),pos,null);
                canvas.drawLine(mX,mY,pos[0],pos[1] ,paint);
                canvas.drawCircle(pos[0],
                        pos[1],(float)(Math.toRadians(15)*mRadius1),paint1);
                

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

    /**
     * 當使用者進行時/分的切換的時候，將指針移動時候，加上動畫
     * 由現在小時的指針所指向的位置移動到分鐘的指針要指向的位置 or
     * 由現在分鐘的指針所指向的位置移動到小時的指針要指向的位置
     * @param type HOUR_TO_MINUTE or MINUTE_TO_HOUR
     * @param oldTime 目前指針所指向的的小時數或分鐘數
     * @param newTime 指針要移動到新的分鐘數或小時數
     */
    public void startAnim(int type, int oldTime ,int newTime){
        float oldAngle;
        float newAngle;
        if(type == HOUR_TO_MINUTE ){
            oldAngle = covertHourToAngle(oldTime,0);
            newAngle = covertMinuteToAngle(newTime);
        }else{
            oldAngle  = covertMinuteToAngle(oldTime);
            newAngle = covertHourToAngle(newTime,0);
        }
        /*
        s = r * rad
        用現在指針所指向的小時數/分鐘數，換算出目前的角度。再用角度去算出目前的弧長。
        之後再用新的小時數/分鐘數，換算出新的角度。再用心的角度去換算新角度的弧長
        然後用新弧長 - 舊弧長，就可以知道要移動多少弧長了。
         */
        final float oldLength = (float)mRadius1* (float)Math.toRadians(oldAngle);
        final float newLength = (float)mRadius1* (float)Math.toRadians(newAngle);
        ValueAnimator animator = ValueAnimator.ofFloat(0,(newLength-oldLength));
        animator.setDuration(800);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //oldLength+(float)animation.getAnimatedValue() 目前指針已經移動的弧長
                //到最後，指針就會移動了newLengt長的弧長，那此時指針就指到新的小時數/分鐘數
                pathMeasure.getPosTan(oldLength+(float)animation.getAnimatedValue(),hourPos,null);
                postInvalidate();
            }
        });
        animator.start();
    }

    /**
     * 用pathMeasure來算出每個小時數字跟分鐘數字要顯示的座標位置
     * @param pathMeasure
     */
    private void initClockText(PathMeasure pathMeasure) {
        if (mClockTexts == null) {
            String hourString;
            String minuteString;
            float hourBaseLineX;
            float hourBaseLineY;
            float minuteBaseLineX;
            float minuteBaseLineY;
            float[] pos = new float[2];

            mPaint.setTextSize(20);
            mHourRect = new Rect();
            mMinuteRect = new Rect();

            String[] mHourStrings = new String[]{"03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02"};
            String[] mMinuteStrings = new String[]{"15", "20", "25", "30", "35", "40", "45", "50", "55", "00", "05", "10"};

            mClockTexts = new SparseArray<>();
            float distance = 0 ;
            for(int i = 0 ; i < 360 ; i = i +30){
                //取得現在角度的總弧長
                distance  = (float)mRadius1*(float)Math.toRadians(i);
                mClockText = new ClockText();
                hourString = mHourStrings[i / 30];
                minuteString = mMinuteStrings[i / 30];
                mPaint.getTextBounds(hourString, 0, hourString.length(), mHourRect);
                mPaint.getTextBounds(minuteString, 0, minuteString.length(), mMinuteRect);
                pathMeasure.getPosTan(distance, pos , null);
                //該弧長的座標
                hourBaseLineX = pos[0];
                hourBaseLineY = pos[1];
                minuteBaseLineX = pos[0];
                minuteBaseLineY = pos[1];
                //將字的中心與該弧長的座標對齊
                hourBaseLineX = hourBaseLineX - mHourRect.width()/2;
                hourBaseLineY = hourBaseLineY + mHourRect.height()/2;
                minuteBaseLineX = minuteBaseLineX - mMinuteRect.width()/2;
                minuteBaseLineY = minuteBaseLineY + mMinuteRect.height()/2;

                mClockText.setHourText(hourString,(int)hourBaseLineX,(int)hourBaseLineY);
                mClockText.setMinuteText(minuteString,(int)minuteBaseLineX,(int)minuteBaseLineY);
                mClockTexts.put(i,mClockText);
            }

        }
    }
}
