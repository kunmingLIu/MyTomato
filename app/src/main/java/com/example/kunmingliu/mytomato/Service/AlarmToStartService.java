package com.example.kunmingliu.mytomato.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by kunming.liu on 2017/9/13.
 */

public class AlarmToStartService  {

    public void startAlarm(Context context){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND,0);
        Intent intent = new Intent(context, ServiceClass.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);

    }
}
