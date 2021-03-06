package com.example.kunmingliu.mytomato.View;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.kunmingliu.mytomato.BuildConfig;
import com.example.kunmingliu.mytomato.Module.GetTimeEvent;
import com.example.kunmingliu.mytomato.Utils.Util;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import static com.example.kunmingliu.mytomato.Utils.Util.log;

/**
 * Created by kunming.liu on 2017/9/13.
 */
// TODO: 2017/9/13 整理變數名稱
public class TomatoClockView extends abstractClockView {
    private Paint mPaint = null;
    private int mRadius = 0;
    private int mX = 0;
    private int mY = 0;
    private float angle;
    private float endAngle;
    private float sweepAngle;
    private int mHour = 0;
    private int mMinute = 0;
    private int mSecond = 0;
    private Calendar cal;
    private int mEndWorkHour = 0;
    private int mEndWorkMin = 0;
    private int mEndWorkSec = 0;
    private int mStartWorkHour = 0;
    private int mStartWorkMin = 0;
    private int mStartWorkSec = 0;

    private RectF mBound = null;

    private int startX = 0;
    private int startY = 0;
    private int stopX = 0;
    private int stopY = 0;
    //時鐘背景顏色
    private int mClockColor = Color.argb(255, 255, 85, 17);
    //時鐘外筐顏色
    private int mClockTintColor = Color.WHITE;
    //時鐘刻度顏色
    private int mTimePieceColor = Color.WHITE;
    //番茄工作區塊的顏色
    private int mWorkTimeColor = Color.YELLOW;

    private Path arcPath = null;
    private float distance ;

    private PathMeasure pathMeasure = null;




    public TomatoClockView(Context context) {
        this(context, null);
    }

    public TomatoClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TomatoClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        mBound = new RectF();

        setTestData();

        pathMeasure = new PathMeasure();
    }
    private void initAttr(AttributeSet attrs){
        if(attrs == null){
            return ;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mX = getWidth() /2;
        mY = getHeight() /2;
        mRadius = getWidth() /3;

        //畫一個圓當時鐘的外筐
        mPaint.setColor(mClockColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mX, mY, mRadius, mPaint);

        //描外匡的邊
        mPaint.setColor(mClockTintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mX, mY, mRadius, mPaint);


        drawCenterCircle(canvas, mX, mY, mRadius / 8, mPaint);
        drawTimePiece(canvas,mX, mY, mRadius , mPaint);
        drawTimeHands(canvas,mX, mY, mRadius, mHour, mMinute, mSecond, mPaint);

        drawWorkTime(canvas,mX, mY, mRadius,mStartWorkHour,mStartWorkMin,mStartWorkSec,
                mEndWorkHour,mEndWorkMin,mEndWorkSec,mPaint);



    }

    /**
     * 在中心畫一個圓圈，當作時針與分針的樞紐
     * @param canvas
     * @param x 圓心
     * @param y 圓心
     * @param radius 半徑 : 預設用時鐘的半徑/8
     * @param paint
     */
    private void drawCenterCircle(Canvas canvas, int x, int y, int radius, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1);
        canvas.drawCircle(x, y, radius, paint);
    }

    /**
     * 畫時針、分針、秒針
     * @param canvas
     * @param x 圓心
     * @param y 圓心
     * @param radius 半徑
     * @param hour  現在時間是幾點
     * @param minute 現在時間是幾分
     * @param mSecond 現在時間是幾秒
     * @param paint
     */
    private void drawTimeHands(Canvas canvas, int x, int y,int radius ,int hour, int minute, int mSecond, Paint paint) {

        paint.setShadowLayer(4, 2, 2, 0x80000000);
        //畫秒針，角度跟分針角度算法相同
        angle = covertMinuteToAngle(mSecond);
        startX = x;
        startY = y;
        stopX = startX + getCosLength(angle, radius);
        stopY = startY + getSinLength(angle, radius);
        paint.setColor(Color.WHITE);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeWidth(3);
        canvas.drawLine(startX, startY, stopX, stopY, paint);

        //畫分針
        angle = covertMinuteToAngle(minute);
        //分針稍微短一點，不要碰到圓弧，因此要比半徑更短
        stopX = startX + getCosLength(angle, radius-35);
        stopY = startY + getSinLength(angle, radius-35);
        paint.setColor(Color.WHITE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(10);
        canvas.drawLine(startX, startY, stopX, stopY, paint);


        //畫時針
        angle = covertHourToAngle(hour, minute);
        //時針要更短
        stopX = startX + getCosLength(angle, radius-70);
        stopY = startY + getSinLength(angle, radius-70);
        paint.setColor(Color.WHITE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(10);
        canvas.drawLine(startX, startY, stopX, stopY, paint);

    }

    /**
     * 畫時鐘的刻度
     * @param canvas
     * @param x 圓心
     * @param y 圓心
     * @param radius 半徑
     * @param paint
     */
    private void drawTimePiece(Canvas canvas,int x , int y,  int radius,  Paint paint) {
        paint.setColor(mTimePieceColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        //時鐘是每5分鐘會有一個刻度，利用三角函數來算出每個刻度的座標位置
        for (int i = 0; i <= 60; i = i + 5) {
            angle = covertMinuteToAngle(i);
            //剛好畫在圓弧上的點
            stopX = x + getCosLength(angle, radius);
            stopY = y + getSinLength(angle, radius);
            //用較短的半徑，就可以算出來的點跟圓弧上的點會是在同一條線上
            //如果是用減法，像是stopX-20之類的，刻度會有一點偏差
            startX = x + getCosLength(angle, radius - 20);
            startY = y + getSinLength(angle, radius - 20);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }

    //// TODO: 2017/9/13 小時跟秒還沒有考慮  思考arcTo addArc
    private void drawWorkTime(Canvas canvas,int x , int y,  int radius, int startWorkHour, int startWorkMin, int startWorkSec,
                              int endWorkHour, int endWorkMin,int endWorkSec,Paint paint) {

        /*
        這段遇到的最大問題就是一開始問題是想要用drawArc跟drawLine去畫出圖形，雖然是做到了，但是無法塗滿顏色。
        後來想說用path去畫出一個多邊形，但是也遇到不能塗滿顏色的問題，有可能是我搞不清楚arcTo跟addArc的差異
        後來就想說用畫兩個扇形的方式，之後再去求出差集的區域，結果雖然效果達到，但是會出現很明顯的鋸齒狀且不知道為何，
        扇形會有一條很細的邊存在。
        之後看到有人說可以改用PorterDuffXfermode，也是類似取差集的部分，但也是效果不好，會有鋸齒狀存在。
        也試過了canvas.clipPath但還是不行。
        最後最後，在stackFlow上看到有人也是畫弧線，但是成功上色了～後來就去研究，發現是我arcTo跟addArc濫用的關係，
        導致上色會變得很詭異。
         */
        if (endWorkHour >= 0 || endWorkMin >= 0 || endWorkSec >= 0) {
            angle = covertMinuteToAngle(startWorkMin);
            endAngle = covertMinuteToAngle(endWorkMin);
            sweepAngle = getSweepAngle(angle,endAngle);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.FILL);

            if(arcPath == null){
                arcPath = new Path();
            }
            else{
                arcPath.reset();
            }


            //計算目前分針所在的點，要從此點開始畫出番茄工作時間的區間
             stopX = getCosLength(angle , radius-20);
             stopY = getSinLength(angle , radius-20);
            //ovalPath.moveTo(mX+offsetX1, mY+offsetY2);//分針所在位置
            //要畫弧線必須要先指定一個矩形，而該點要剛好在矩形的邊上，這樣畫弧線才會剛好畫過該點，
            //所以用該點的座標-圓心座標的距離當作一半的對角線長，然後畫出該矩形
            //兩點之間的距離 sqrt((x2-x1)＊(x2-x1) + (y2-y1)＊(y2-y1))
            //而x2-x1 = stopX ; y2-y1 = stopY
            distance = (float)Math.round(Math.sqrt((Math.abs(stopX*stopX)) + Math.abs((stopY*stopY))));
            //有了對角線的半長，就可由圓心推算出矩形的四點座標
            mBound.set(x-distance, y-distance, x+distance, y+distance);
            //直接在畫面上畫上一個弧
            arcPath.addArc(mBound,angle,sweepAngle);
            mBound.set(x-radius, y-radius, x+radius, y+radius);//外弧
            //從目前path上的最後一點出發，並畫上一個弧。
            //因為內弧是用順時針方向去畫弧的，因此外弧如果是由內弧的最後一點開始畫，必須要逆時針畫弧。
            //因此sweepAngle必須要指定-sweepAngle
            arcPath.arcTo(mBound,endAngle,-sweepAngle);
            arcPath.close();

            paint.setColor(mWorkTimeColor);
            paint.setAlpha(200);
            canvas.drawPath(arcPath, paint);


        }
    }

    public void setCurrentTime() {
        cal = Calendar.getInstance();
        mHour = cal.get(Calendar.HOUR);
        mMinute = cal.get(Calendar.MINUTE);
        mSecond = cal.get(Calendar.SECOND);
        postInvalidate();
    }

    //    //todo 沒效果，而且如果我不再activity去訂閱的話，會報錯
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void setCurrentTime(GetTimeEvent event){
//        cal = Calendar.getInstance();
//        mHour = cal.get(Calendar.HOUR);
//        mMinute = cal.get(Calendar.MINUTE);
//        mSecond = cal.get(Calendar.SECOND);
//        invalidate();
//    }

    /**
     * 開始進行番茄工作
     * @param workHour 這次要進行的時數
     * @param workMin 這次要進行的分鐘數
     * @param workSec 這次要進行的秒數
     */
    public  void startWork(int workHour,int workMin, int workSec){
        cal = Calendar.getInstance();
        mStartWorkHour = cal.get(Calendar.HOUR);
        mStartWorkMin = cal.get(Calendar.MINUTE);
        mStartWorkSec = cal.get(Calendar.SECOND);

        cal.add(Calendar.HOUR,workHour);
        cal.add(Calendar.MINUTE,workMin);
        cal.add(Calendar.SECOND,workSec);

        mEndWorkHour = cal.get(Calendar.HOUR);
        mEndWorkMin = cal.get(Calendar.MINUTE);
        mEndWorkSec = cal.get(Calendar.SECOND);

        invalidate();
    }
    //測試
    private void setTestData(){
        if(BuildConfig.DEBUG){
            mStartWorkHour = mHour;
            mStartWorkMin = mMinute;
            mStartWorkSec = 0;

            mEndWorkHour = mHour;
            mEndWorkMin = mMinute + 30;
            mEndWorkSec = 0;

        }
    }

    /**
     * 取得SweepAngle給之後畫arc用，因為畫arc是從某個角度開始，然後掃過某個角度，因此要算出第二個角度與第一個角度的差值
     * @param startAngle
     * @param endAngle
     * @return sweepAngle for drawArc , arcTo or addArc
     */
    private float getSweepAngle(float startAngle, float endAngle){
        /* 270
        180 + 0 經過測試畫弧度的時候，一律都是以3點鐘方向為0度
            90
          ie. 如果現在是08:30，但是我要做50分的番茄工作，所以到09:20，
          此時endAngle比startAngle還要小(30分的角度是90，20分的角度是60)，因此算出來sweepAngle是-30，
          所以arc會逆時針畫30度。
          但是為了方便思考，統一都是用順時針的方向去畫弧，因此如果發現endAngle比startAngle小的話，就加上360，
          這樣就能能夠順時針去畫弧
         */
        return startAngle >= endAngle ? endAngle + 360 - startAngle : endAngle - startAngle;
    }
}
