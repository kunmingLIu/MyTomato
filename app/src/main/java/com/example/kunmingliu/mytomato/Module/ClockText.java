package com.example.kunmingliu.mytomato.Module;

import android.graphics.Rect;

/**
 * Created by kunming.liu on 2017/9/15.
 */

public class ClockText {
    private String hourText;
    private int hourTextPosX;
    private int hourTextPosY;
    private String minuteText;
    private int minuteTextPosX;
    private int minuteTextPosY;



    private Rect hourRect = null;
    private Rect minuteRect = null;

    public void setHourText(String hourText, int hourTextPosX, int hourTextPosY){
        this.hourText = hourText;
        this.hourTextPosX = hourTextPosX;
        this.hourTextPosY = hourTextPosY;
    }

    public void setMinuteText(String minuteText, int minuteTextPosX, int minuteTextPosY){
        this.minuteText = minuteText;
        this.minuteTextPosX = minuteTextPosX;
        this.minuteTextPosY = minuteTextPosY;
    }
    public void setHourText(String hourText, int hourTextPosX, int hourTextPosY,Rect hourRect){
        this.hourText = hourText;
        this.hourTextPosX = hourTextPosX;
        this.hourTextPosY = hourTextPosY;
        this.hourRect = new Rect();
        this.hourRect.left = hourTextPosX;
        this.hourRect.right = hourTextPosX+hourRect.width();
        this.hourRect.top = hourTextPosY - hourRect.height();
        this.hourRect.bottom = hourTextPosY;

    }

    public void setMinuteText(String minuteText, int minuteTextPosX, int minuteTextPosY, Rect minuteRect){
        this.minuteText = minuteText;
        this.minuteTextPosX = minuteTextPosX;
        this.minuteTextPosY = minuteTextPosY;
        this.minuteRect = new Rect();
        this.minuteRect.left = minuteTextPosX;
        this.minuteRect.right = minuteTextPosX+minuteRect.width();
        this.minuteRect.top = minuteTextPosY - minuteRect.height();
        this.minuteRect.bottom = minuteTextPosY;
    }


    public String getHourText() {
        return hourText;
    }

    public int getHourTextPosX() {
        return hourTextPosX;
    }

    public int getHourTextPosY() {
        return hourTextPosY;
    }

    public String getMinuteText() {
        return minuteText;
    }

    public int getMinuteTextPosX() {
        return minuteTextPosX;
    }

    public int getMinuteTextPosY() {
        return minuteTextPosY;
    }

    public Rect getHourRect() {
        return hourRect;
    }

    public Rect getMinuteRect() {
        return minuteRect;
    }
}
