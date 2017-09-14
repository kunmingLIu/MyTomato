package com.example.kunmingliu.mytomato.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.kunmingliu.mytomato.Module.GetTimeEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

/**
 * Created by kunming.liu on 2017/9/13.
 */
// TODO: 2017/9/13 整理變數名稱
public class TomatoClockView extends View {
    private Paint mPaint = null;
    private int mRadius = 0;
    private int mX = 0;
    private int mY = 0;
    private float angle;
    private int mHour = 0;
    private int mMinute = 0;
    private int mSecond = 0;
    private Calendar cal;
    private int mWorkHour = 0;
    private int mWorkMinute = 0;
    private RectF mBound = null;
    public TomatoClockView(Context context) {
        this(context, null);
    }

    public TomatoClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TomatoClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);

        cal = Calendar.getInstance();
        mHour = cal.get(Calendar.HOUR);
        mMinute = cal.get(Calendar.MINUTE);
        mSecond = cal.get(Calendar.SECOND);

        mWorkHour = mHour;
        mWorkMinute = mMinute+12;

        mBound = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        mPaint.setColor(Color.argb(255, 255, 85, 17));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 3, mPaint);

        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 3, mPaint);

        mX = getWidth() / 2;
        mY = getHeight() / 2;
        mRadius = getWidth() / 3;

        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        drawCenterCircle(canvas, mX, mY, mRadius / 8, mPaint);
        drawTimePiece(canvas, mPaint);
        drawHourAndMinuteHand(canvas, mHour, mMinute, mSecond, mPaint);
        drawWorkTime(canvas,mWorkHour,mWorkMinute, mPaint);

    }

    private void drawCenterCircle(Canvas canvas, int x, int y, int radius, Paint paint) {
        canvas.drawCircle(x, y, radius, paint);
    }

    private void drawHourAndMinuteHand(Canvas canvas, int hour , int minute, int mSecond , Paint paint){

        //秒針
        angle = covertMinuteToAngle(mSecond);
        int offsetX = getCosLength(angle , mRadius);
        int offsetY = getSinLength(angle , mRadius);
        //先當作輔助線
        paint.setColor(Color.WHITE);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeWidth(3);
        canvas.drawLine(mX,mY,mX + offsetX,mY + offsetY,paint);

        angle = covertMinuteToAngle(minute);
        int offsetX1 = getCosLength(angle , mRadius-35);
        int offsetY2 = getSinLength(angle , mRadius-35);
        //分針
        paint.setColor(Color.WHITE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(10);
        canvas.drawLine(mX,mY,mX + offsetX1,mY + offsetY2,paint);


        //時針
        angle = covertHourToAngle(hour, minute);
        offsetX1 = getCosLength(angle , mRadius-70);
        offsetY2 = getSinLength(angle , mRadius-70);
        paint.setColor(Color.WHITE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(10);
        canvas.drawLine(mX,mY,mX+offsetX1,mY+offsetY2,paint);

    }

    private void drawTimePiece(Canvas canvas, Paint paint) {
        //時鐘是每5分鐘會有一個刻度
        for(int i = 0 ; i <= 60 ; i = i +5){
            angle = covertMinuteToAngle(i);
            //剛好畫在圓弧上的點
            int stopX = mX + getCosLength(angle , mRadius) ;
            int stopY = mY + getSinLength(angle , mRadius);
            //用較短的半徑，就可以算出來的點跟圓弧上的點會是在同一條線上
            //如果是用減法，像是stopX-20之類的，刻度會有一點偏差
            int startX = mX + getCosLength(angle , mRadius-20);
            int startY = mY + getSinLength(angle , mRadius-20);
            canvas.drawLine(startX,startY,stopX,stopY,paint);
        }
    }
    //// TODO: 2017/9/13 不要再draw用太多變數 小時還沒有考慮
    private void drawWorkTime(Canvas canvas , int workHour , int workMinute, Paint paint){
        if(workHour >=0 || workMinute >= 0){
            angle = covertMinuteToAngle(mMinute);
            int offsetX1 = getCosLength(angle , mRadius);
            int offsetY2 = getSinLength(angle , mRadius);
            //分針
            paint.setColor(Color.BLACK);
            paint.setStrokeCap(Paint.Cap.BUTT);
            paint.setStrokeWidth(3);
            canvas.drawLine(mX,mY,mX+offsetX1,mY+offsetY2,paint);

            float sweapAngle = covertMinuteToAngle(workMinute);
            int offsetX11 = getCosLength(sweapAngle , mRadius);
            int offsetY22 = getSinLength(sweapAngle , mRadius);
            //分針
            paint.setColor(Color.BLACK);
            paint.setStrokeCap(Paint.Cap.BUTT);
            paint.setStrokeWidth(3);
            canvas.drawLine(mX,mY,mX+offsetX11,mY+offsetY22,paint);

            //drawArc ： 十 0
            //          90
            //另外arc是由某個角度出來，然後掃過某個角度的範圍，因此這邊要算出差值
            sweapAngle = angle >= sweapAngle ? sweapAngle+360-angle : sweapAngle - angle ;
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            mBound.set(mX-mRadius, mY-mRadius, mX+mRadius, mY+mRadius);
            canvas.drawArc(mBound, angle, sweapAngle, false, paint);//绘制圆弧，不含圆心

        }
    }

    private int getSinLength(float angle, int length){
        double sin = Math.sin(Math.toRadians(angle));
        return (int) ((float) length * sin);
    }
    private int getCosLength(float angle, int length){
        double cos = Math.cos(Math.toRadians(angle));
        return (int) ((float) length * cos);
    }
    private float covertMinuteToAngle(int minute){
        //因為把15分方向當作是0度開始計算，且逆時針方向為正
        // 十 0
        // 90
        float angle = (minute - 15) * 6;
        if(angle < 0 ){
            angle = angle +360;
        }
        return angle;
    }
    private float covertHourToAngle(int hour, int minute){
        //因為把3點方向當作是0度開始計算，且逆時針方向為正
        // 十 0
        // 90
        float angle =  (hour - 3) * 30;
        if(angle < 0 ){
            angle = angle +360;
            //每12分鐘，時針會前進一小格，但是逆時針方向為正，因此要用減的
            angle = angle - (minute/12)*6;
        }else{
            angle = angle - (minute/12)*6;
        }
        return angle;
    }
    public void setCurrentTime(){
        cal = Calendar.getInstance();
        mHour = cal.get(Calendar.HOUR);
        mMinute = cal.get(Calendar.MINUTE);
        mSecond = cal.get(Calendar.SECOND);
        invalidate();
    }
    //todo 沒效果，而且如果我不再activity去訂閱的話，會報錯
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setCurrentTime(GetTimeEvent event){
        Log.e("TestEventBus", "event : "+event.getMsg());
        cal = Calendar.getInstance();
        mHour = cal.get(Calendar.HOUR);
        mMinute = cal.get(Calendar.MINUTE);
        mSecond = cal.get(Calendar.SECOND);
        invalidate();
    }

}
