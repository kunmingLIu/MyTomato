package com.example.kunmingliu.mytomato.Service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by kunming.liu on 2017/9/13.
 */
public class ServiceClass extends IntentService{


    public ServiceClass() {
        super("");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Calendar cal = Calendar.getInstance();

        AlarmToStartService alarmToStartService = new AlarmToStartService();
        alarmToStartService.startAlarm(getApplicationContext());
    }
}