package com.example.kunmingliu.mytomato.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by kunming.liu on 2017/9/14.
 */

public class GrowthView extends View {
    private static final String TAG = "GrowthView";
    private int bgColor = Color.parseColor("#33485d");
    private int valColor = Color.parseColor("#ecb732");
    private int[] scores = new int[]{0, 10, 80, 180, 800, 5000, 20000, 50000, 100000};

    private Context mContext;

    private float w;
    private float h;

    private Paint bgPaint;
    private Paint growthPaint;
    private Paint textPaint;
    private Paint clipPaint;
    private Path bgPath;
    private Path bgClipPath;
    private Path growthPath;

    private int growthValue = 0;

    private float bgFullAngle = 240.0f;
    private float gapAngle = bgFullAngle / (scores.length - 1);

    private float gapRadius = 21.5f;//实际为21px 略大半个像素避免path无法缝合error
    private float outerRadius = 240.0f;
    private float innerRadius = outerRadius - gapRadius * 2;

    private RectF outerRecF;
    private RectF innerRecF;
    private RectF leftBoundRecF;
    private RectF rightBoundRecF;

    public GrowthView(Context context) {
        this(context, null);
    }

    public GrowthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GrowthView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    private void init() {
        Xfermode xFermode = new PorterDuffXfermode(PorterDuff.Mode.DARKEN);

        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(bgColor);
        bgPaint.setStrokeWidth(0.1f);
        bgPaint.setAntiAlias(true);

        growthPaint = new Paint();
        growthPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        growthPaint.setColor(valColor);
        growthPaint.setStrokeWidth(1f);
        growthPaint.setAntiAlias(true);

        clipPaint = new Paint();
        clipPaint.setStyle(Paint.Style.FILL);
        clipPaint.setColor(Color.WHITE);
        clipPaint.setStrokeWidth(.1f);
        clipPaint.setAntiAlias(true);
        clipPaint.setXfermode(xFermode);

        textPaint = new Paint();
        textPaint.setTextSize(96);//todo comfirm the textSize
        textPaint.setStrokeWidth(1f);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(valColor);


        bgPath = new Path();
        growthPath = new Path();

        //todo 暂定中心点为屏幕中心
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        w = metrics.widthPixels;
        h = metrics.heightPixels;

        outerRecF = new RectF(w / 2 - outerRadius, h / 2 - outerRadius, w / 2 + outerRadius, h / 2 + outerRadius);
        innerRecF = new RectF(w / 2 - innerRadius, h / 2 - innerRadius, w / 2 + innerRadius, h / 2 + innerRadius);

        rightBoundRecF = new RectF(w / 2 + (float) Math.pow(3, 0.5) * (innerRadius + gapRadius) / 2 - gapRadius,
                h / 2 + (innerRadius + gapRadius) / 2 - gapRadius,
                w / 2 + (float) Math.pow(3, 0.5) * (innerRadius + gapRadius) / 2 + gapRadius,
                h / 2 + (innerRadius + gapRadius) / 2 + gapRadius);

        leftBoundRecF = new RectF(w / 2 - (float) Math.pow(3, 0.5) * (innerRadius + gapRadius) / 2 - gapRadius,
                h / 2 + (innerRadius + gapRadius) / 2 - gapRadius,
                w / 2 - (float) Math.pow(3, 0.5) * (innerRadius + gapRadius) / 2 + gapRadius,
                h / 2 + (innerRadius + gapRadius) / 2 + gapRadius);

        bgClipPath = new Path();
        bgClipPath.arcTo(innerRecF, 150.0f, 359.9f, true);
        bgClipPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //bg
        float startAngle = 150.0f;
        float endRecfFullAngle = 180.0f;
        bgPath.arcTo(outerRecF, startAngle, bgFullAngle, true);
        bgPath.arcTo(rightBoundRecF, 30.0f, endRecfFullAngle, true);
        bgPath.arcTo(innerRecF, startAngle, bgFullAngle);
        bgPath.arcTo(leftBoundRecF, -30.0f, endRecfFullAngle);
        bgPath.rMoveTo(w / 2 - outerRadius * (float) Math.pow(3, 0.5) / 2, h / 2 + outerRadius / 2);
        bgPath.setFillType(Path.FillType.WINDING);
        bgPath.close();

        //growth
        if (getGrowthVal() != 0) {
            float temp = getGrowthAngle(getGrowthVal());
            growthPath.arcTo(outerRecF, startAngle, temp, true);
            growthPath.arcTo(getDynamicRecF(getGrowthVal()), getDynamicOriginAngle(getGrowthVal()), endRecfFullAngle, true);
            growthPath.arcTo(innerRecF, startAngle, temp);
            growthPath.arcTo(leftBoundRecF, -30.0f, endRecfFullAngle);
            growthPath.rMoveTo(w / 2 - outerRadius * (float) Math.pow(3, 0.5) / 2, h / 2 + outerRadius / 2);
            growthPath.close();
        }
        canvas.drawText(formatVal(getGrowthVal()), w / 2, h / 2, textPaint);
        canvas.clipPath(bgClipPath, Region.Op.DIFFERENCE);
        canvas.drawPath(bgPath, bgPaint);
        canvas.drawPath(growthPath, growthPaint);
        canvas.drawPath(bgClipPath, clipPaint);
    }

    private float getDynamicOriginAngle(int growthVal) {
        return growthVal <= 30 ? getGrowthAngle(growthVal) + 150 :
                getGrowthAngle(growthVal) - 210;
    }

    private RectF getDynamicRecF(int growthVal) {
        float dynamicAngle = getGrowthAngle(growthVal);
        //动态圆心
        float _w = w / 2 + (float) Math.sin(Math.toRadians(dynamicAngle - 120)) * (outerRadius - gapRadius);
        float _y = h / 2 - (float) Math.sin(Math.toRadians(dynamicAngle - 30)) * (outerRadius - gapRadius);
        return new RectF(_w - gapRadius, _y - gapRadius, _w + gapRadius, _y + gapRadius);
    }

    private int getGrowthVal() {
        return this.growthValue;
    }

    public void setGrowthValue(int value) {
        if (value < 0 || value > 100000) {
            try {
                throw new Exception("成长值不在范围内");
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        this.growthValue = value;
        invalidate();
    }

    private float getGrowthAngle(int growthVal) {
        return gapAngle * (getLevel(growthVal) - 1)
                + gapAngle * (growthVal - scores[getLevel(growthVal) - 1]) /
                (scores[getLevel(growthVal)] - scores[getLevel(growthVal) - 1]);
    }

    private int getLevel(int score) {
        return score < 0 ? -1 : score <= 10 ? 1 : score <= 80 ? 2 : score <= 180 ? 3 : score <= 800 ?
                4 : score <= 5000 ? 5 : score <= 20000 ? 6 : score <= 50000 ? 7 : 8;
    }

    private String formatVal(int value) {
        StringBuilder builder = new StringBuilder(String.valueOf(value));
        return value < 1000 ? builder.toString() : builder.insert(builder.length() - 3, ',').toString();
    }
}