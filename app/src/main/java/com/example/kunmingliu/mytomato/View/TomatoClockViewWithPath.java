package com.example.kunmingliu.mytomato.View;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import com.example.kunmingliu.mytomato.BuildConfig;
import com.example.kunmingliu.mytomato.Utils.Util;

import java.util.Calendar;

/**
 * Created by kunming.liu on 2017/9/13.
 */
// TODO: 2017/9/13 整理變數名稱
public class TomatoClockViewWithPath extends abstractClockView {
    private Paint mPaint = null;
    private Paint mTimePaint = null;
    private int mRadius = 0;
    private int mInnerRadius = 0;
    private int mHourRadius = 0;
    private int mMinuteRadius = 0;
    private int mSecondRadius = 0;
    private int mX = 0;
    private int mY = 0;
    private float angle;
    private float secondAngle;
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

    private PathMeasure clockPathMeasure = null;
    private PathMeasure innerPathMeasure = null;
    private Path clockPath = null;
    private Path innerClockPath = null;
    private Path hourPath = null;
    private Path minutePath = null;
    private Path secondPath = null;
    private PathMeasure hourPathMeasure = null;
    private PathMeasure minutePathMeasure = null;
    private PathMeasure secondPathMeasure = null;
    private float[] clockPos;
    private float[] innerPos;
    private float[] hourPos;
    private float[] minutePos;
    private float[] secondPos;

    private float arcLength;
    private float circleLength;




    public TomatoClockViewWithPath(Context context) {
        this(context, null);
    }

    public TomatoClockViewWithPath(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TomatoClockViewWithPath(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        mTimePaint = new Paint();
        mTimePaint.setAntiAlias(true);
        mTimePaint.setColor(Color.WHITE);
        mTimePaint.setStyle(Paint.Style.STROKE);
        mTimePaint.setStrokeWidth(1);
        setLayerType(LAYER_TYPE_SOFTWARE, mTimePaint);

        cal = Calendar.getInstance();
        mHour = cal.get(Calendar.HOUR);
        mMinute = cal.get(Calendar.MINUTE);
        mSecond = cal.get(Calendar.SECOND);

        mBound = new RectF();

        setTestData(true);

        clockPath = new Path();
        innerClockPath = new Path();
        hourPath = new Path();
        minutePath = new Path();
        secondPath = new Path();

        clockPathMeasure = new PathMeasure();
        innerPathMeasure = new PathMeasure();
        hourPathMeasure = new PathMeasure();
        minutePathMeasure = new PathMeasure();
        secondPathMeasure = new PathMeasure();

        clockPos = new float[2];
        innerPos = new float[2];
        hourPos = new float[2];
        minutePos = new float[2];
        secondPos = new float[2];
    }
    private void initAttr(AttributeSet attrs){
        if(attrs == null){
            return ;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mX = getWidth() /2;
        mY = getHeight() /2;
        mRadius = getWidth() /3;
        mInnerRadius = mRadius -20;
        mSecondRadius = mInnerRadius -10;
        mMinuteRadius = mSecondRadius -20;
        mHourRadius = mMinuteRadius -20;

        clockPath.addCircle(0,0,mRadius, Path.Direction.CW);
        innerClockPath.addCircle(0,0,mInnerRadius, Path.Direction.CW);
        hourPath.addCircle(0,0,mHourRadius, Path.Direction.CW);
        minutePath.addCircle(0,0,mMinuteRadius, Path.Direction.CW);
        secondPath.addCircle(0,0,mSecondRadius, Path.Direction.CW);

        clockPathMeasure.setPath(clockPath,false);
        innerPathMeasure.setPath(innerClockPath,false);
        hourPathMeasure.setPath(hourPath,false);
        minutePathMeasure.setPath(minutePath,false);
        secondPathMeasure.setPath(secondPath,false);

        secondAngle = covertMinuteToAngle(mSecond);
        secondPathMeasure.getPosTan((float)Math.toRadians(secondAngle)*mSecondRadius,secondPos,null);

        angle = covertMinuteToAngle(mMinute);
        minutePathMeasure.getPosTan((float)Math.toRadians(angle)*mMinuteRadius,minutePos,null);

        angle = covertHourToAngle(mHour, mMinute);
        hourPathMeasure.getPosTan((float)Math.toRadians(angle)*mHourRadius,hourPos,null);

//        secondHandAnim();
//        minuteHandAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(getWidth()/2,getHeight()/2);
        //畫一個圓當時鐘的外筐
        mPaint.setColor(mClockColor);
        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(mX, mY, mRadius, mPaint);
        canvas.drawPath(clockPath,mPaint);

        //描外匡的邊
        mPaint.setColor(mClockTintColor);
        mPaint.setStyle(Paint.Style.STROKE);
//        canvas.drawCircle(mX, mY, mRadius, mPaint);
        canvas.drawPath(clockPath,mPaint);


        drawCenterCircle(canvas);
        drawTimePiece(canvas);
        drawTimeHands(canvas);
        drawWorkTime(canvas,mX, mY, mRadius,mStartWorkHour,mStartWorkMin,mStartWorkSec,
                mEndWorkHour,mEndWorkMin,mEndWorkSec,mPaint);
        canvas.restore();
//        drawTimeHands(canvas,mX, mY, mRadius, mHour, mMinute, mSecond, mPaint);
//




    }

    /**
     * 在中心畫一個圓圈，當作時針與分針的樞紐
     * @param canvas
     */
    private void drawCenterCircle(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(1);
        canvas.drawCircle(0, 0, mRadius/8, mPaint);
    }

    /**
     * 畫時針、分針、秒針
     * @param canvas
     */
    // TODO: 2017/9/17 比中原標準時間慢了大概2~3秒
    private void drawTimeHands(Canvas canvas) {

        mTimePaint.setShadowLayer(4, 2, 2, 0x80000000);
        //畫秒針
        angle = covertMinuteToAngle(mSecond);
//        Util.log("mSecond = %d",mSecond);
        secondPathMeasure.getPosTan((float)Math.toRadians(angle)*mSecondRadius,secondPos,null);
        mTimePaint.setColor(Color.WHITE);
        mTimePaint.setStrokeCap(Paint.Cap.ROUND);
        mTimePaint.setStrokeWidth(5);
        canvas.drawLine(0, 0, secondPos[0], secondPos[1], mTimePaint);

        //畫分針
        angle = covertMinuteToAngle(mMinute);
        minutePathMeasure.getPosTan((float)Math.toRadians(angle)*mMinuteRadius,minutePos,null);
        mTimePaint.setColor(Color.WHITE);
        mTimePaint.setStrokeCap(Paint.Cap.ROUND);
        mTimePaint.setStrokeWidth(10);
        canvas.drawLine(0, 0, minutePos[0], minutePos[1], mTimePaint);


        //畫時針
        angle = covertHourToAngle(mHour, mMinute);
        hourPathMeasure.getPosTan((float)Math.toRadians(angle)*mHourRadius,hourPos,null);
        canvas.drawLine(0, 0, hourPos[0], hourPos[1], mTimePaint);

    }

    /**
     * 畫時鐘的刻度
     * @param canvas
     */
    private void drawTimePiece(Canvas canvas) {
        mPaint.setColor(mTimePieceColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        for (int i = 0; i < 360; i = i + 30) {
            clockPathMeasure.getPosTan(mRadius*(float)Math.toRadians(i),clockPos,null);
            innerPathMeasure.getPosTan(mInnerRadius*(float)Math.toRadians(i),innerPos,null);
            canvas.drawLine(innerPos[0], innerPos[1], clockPos[0], clockPos[1], mPaint);
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
        Util.log("endWorkMin = %d ",endWorkMin);
        Util.log("startWorkMin = %d ",startWorkMin);
        if (endWorkHour >= 0 || endWorkMin >= 0 || endWorkSec >= 0) {
            angle = covertMinuteToAngle(startWorkMin);
            endAngle = covertMinuteToAngle(startWorkMin+10);

            sweepAngle = getSweepAngle(angle,endAngle);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.FILL);

            if(arcPath == null){
                arcPath = new Path();
            }
            else{
                arcPath.reset();
            }
            // TODO: 2017/9/18 當startArcLength >  endArcLength 要怎麼處理
            //一直畫不出來是因為startArcLength >  endArcLength，導致getSegment失敗
            //當getSegment的stopD大於length的話，那就只會擷取到startD到length而已
            //因此如果發現startArcLength >  endArcLength(代表起碼要順時針繞過原點一次)，那就先擷取startArcLength到0
            //然後再擷取0到endArcLength
            angle = covertMinuteToAngle(startWorkMin);
            endAngle = covertMinuteToAngle(startWorkMin+25);
            float startArcLength = (float)Math.toRadians(angle)*mInnerRadius;
            float endArcLength = (float)Math.toRadians(endAngle)*mInnerRadius;
            Util.log("startArcLength = %f ",startArcLength);
            Util.log("endArcLength = %f ",endArcLength);
            if(endArcLength < startArcLength){
                innerPathMeasure.getSegment(startArcLength,innerPathMeasure.getLength(),arcPath,true);
                innerPathMeasure.getSegment(0,endArcLength,arcPath,true);
            }
            else{
                innerPathMeasure.getSegment(startArcLength,endArcLength,arcPath,true);
            }
            float[] pos = new float[2];
            float[] pos1 = new float[2];

            innerPathMeasure.getPosTan(startArcLength,pos,null);
            innerPathMeasure.getPosTan(endArcLength,pos1,null);

            Path path = new Path();
            path.addPath(arcPath);

            arcPath.reset();//key point
            startArcLength = (float)Math.toRadians(angle)*mRadius;
            endArcLength = (float)Math.toRadians(endAngle)*mRadius;
            Util.log("startArcLength = %f ",startArcLength);
            Util.log("endArcLength = %f ",endArcLength);
            if(endArcLength < startArcLength){
                clockPathMeasure.getSegment(startArcLength,clockPathMeasure.getLength(),arcPath,true);
                clockPathMeasure.getSegment(0,endArcLength,arcPath,true);
            }
            else{
                clockPathMeasure.getSegment(startArcLength,endArcLength,arcPath,true);
            }
            path.addPath(arcPath);
            arcPath.reset();
            float[] pos2 = new float[2];
            float[] pos3 = new float[2];
            clockPathMeasure.getPosTan(startArcLength,pos2,null);
            clockPathMeasure.getPosTan(endArcLength,pos3,null);
            path.moveTo(pos[0],pos[1]);
            path.lineTo(pos2[0],pos2[1]);
            path.moveTo(pos1[0],pos1[1]);
            path.lineTo(pos3[0],pos3[1]);
            path.close();
            path.setFillType(Path.FillType.EVEN_ODD);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(mWorkTimeColor);
            paint.setAlpha(200);
            canvas.drawPath(path, paint);



//            arcPath.reset();
//            path.reset();
//            angle = covertMinuteToAngle(startWorkMin+20);
//            endAngle = covertMinuteToAngle(endWorkMin);
//            startArcLength = (float)Math.toRadians(angle)*mInnerRadius;
//            endArcLength = (float)Math.toRadians(endAngle)*mInnerRadius;
//            if(endArcLength < startArcLength){
//                innerPathMeasure.getSegment(startArcLength,innerPathMeasure.getLength(),arcPath,true);
//                innerPathMeasure.getSegment(0,endArcLength,arcPath,true);
//            }
//            else{
//                innerPathMeasure.getSegment(startArcLength,endArcLength,arcPath,true);
//            }
//            pos = new float[2];
//            pos1 = new float[2];
//
//            innerPathMeasure.getPosTan(startArcLength,pos,null);
//            innerPathMeasure.getPosTan(endArcLength,pos1,null);
//
//            path.addPath(arcPath);
//            arcPath.reset();//key point
//
//            startArcLength = (float)Math.toRadians(angle)*mRadius;
//            endArcLength = (float)Math.toRadians(endAngle)*mRadius;
//            if(endArcLength < startArcLength){
//                clockPathMeasure.getSegment(startArcLength,clockPathMeasure.getLength(),arcPath,true);
//                clockPathMeasure.getSegment(0,endArcLength,arcPath,true);
//            }
//            else{
//                clockPathMeasure.getSegment(startArcLength,endArcLength,arcPath,true);
//            }
//            path.addPath(arcPath);
//            pos2 = new float[2];
//            pos3 = new float[2];
//            clockPathMeasure.getPosTan(startArcLength,pos2,null);
//            clockPathMeasure.getPosTan(endArcLength,pos3,null);
//            path.moveTo(pos[0],pos[1]);
//            path.lineTo(pos2[0],pos2[1]);
//            path.moveTo(pos1[0],pos1[1]);
//            path.lineTo(pos3[0],pos3[1]);
//            path.close();
//            path.setFillType(Path.FillType.EVEN_ODD);
//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(mWorkTimeColor);
//            paint.setAlpha(200);
//            canvas.drawPath(path, paint);





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
    private void setTestData(boolean isTest){
        if(isTest){
            if(BuildConfig.DEBUG){
                mStartWorkHour = mHour;
                mStartWorkMin = mMinute;
                mStartWorkSec = 0;

                mEndWorkHour = mHour;
                mEndWorkMin = mMinute + 30;
                mEndWorkSec = 0;

            }
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
    /*
    todo
    秒數大概會慢個3秒左右
    而且繞回到原點後，還沒想到該如何處理
     */
    private void secondHandAnim(){
        //float angle = covertMinuteToAngle(mSecond);
        arcLength =(float)Math.toRadians(secondAngle)*mSecondRadius;
        circleLength = secondPathMeasure.getLength();

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,circleLength);
        valueAnimator.setDuration(60*1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(arcLength+(float)(animation.getAnimatedValue()) >= circleLength){
                    arcLength = 0 ;
                    secondPathMeasure.getPosTan(0,secondPos,null);
                }else{
                    secondPathMeasure.getPosTan(arcLength+(float)animation.getAnimatedValue(),secondPos,null);
                }

                postInvalidate();
            }
        });
        valueAnimator.start();
    }
    private void minuteHandAnim(){
        float angle = covertMinuteToAngle(mMinute);
        float arcLength =(float)Math.toRadians(angle)*mMinuteRadius;
        float circleLength = minutePathMeasure.getLength();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(arcLength,arcLength+circleLength);
        valueAnimator.setDuration(60*60*1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                minutePathMeasure.getPosTan((float)animation.getAnimatedValue(),minutePos,null);
                postInvalidate();
            }
        });
        valueAnimator.start();
    }
}
