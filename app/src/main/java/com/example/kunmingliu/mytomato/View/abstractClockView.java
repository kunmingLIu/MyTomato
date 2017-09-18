package com.example.kunmingliu.mytomato.View;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kunming.liu on 2017/9/15.
 */

public abstract class abstractClockView extends View {
    public abstractClockView(Context context) {
        super(context);
    }

    public abstractClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public abstractClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     * 必須要加上圓心座標，才是該點真正的座標
     * @param angle
     * @param length
     * @return
     */
    public int getSinLength(float angle, int length) {
        double sin = Math.sin(Math.toRadians(angle));
        return (int) ((float) length * sin);
    }
    /**
     * 必須要加上圓心座標，才是該點真正的座標
     * @param angle
     * @param length
     * @return
     */
    public int getCosLength(float angle, int length) {
        double cos = Math.cos(Math.toRadians(angle));
        return (int) ((float) length * cos);
    }
    /**
     * 計算X分鐘在時鐘上的角度
     * @param minute 0~59
     * @return
     */
    public float covertMinuteToAngle(int minute) {
        //因為把15分鐘的時刻方向當作是0度開始計算，且逆時針方向為正(因此0~14分鐘都是負值)
        // 十 0
        // 90
        //圓360度，然後分成四部分
        //時鐘上0~15的刻度剛好是直角，所以是90度，可推算出每5分鐘的刻度共佔30度；
        //因此可以推算出每分鐘刻度一共佔30*(5)=6
        /*
        如果傳入的是55分，那所佔的角度是55*6=330；但是這是以12點鐘方向為0度去算的
        我們是要從15分鐘的方向為0度去算，因此必須要扣掉12點鐘到15分鐘的方向的夾角值90(15*6)度
        因此，我們可以推出公式=(55-15)*6 = 240
         */
        float angle = (minute - 15) * 6;
        if (angle < 0) {
            angle = angle + 360;
        }
        return angle;
    }
    /**
     * 計算X時Y分在時鐘上的角度
     * @param hour 0~11
     * @param minute 0~59
     * @return
     */
    public float covertHourToAngle(int hour, int minute) {
        //因為把3點方向當作是0度開始計算，且逆時針方向為正(因此0~2點都是負值)
        // 十 0
        // 90
        //計算邏輯與算分鐘角度相同
        float angle = (hour - 3) * 30;
        if (angle < 0) {
            angle = angle + 360;
            //從上一小時一共要走5小格才會到下一個小時，因此可以算出每12分鐘要前進一小格
            //因此要針對時針的角度再做調整
            angle = angle + (minute / 12) * 6;
        } else {
            angle = angle + (minute / 12) * 6;
        }
        return angle;
    }
}
