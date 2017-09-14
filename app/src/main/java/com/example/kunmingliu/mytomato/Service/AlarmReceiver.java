package com.example.kunmingliu.mytomato.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by kunming.liu on 2017/9/13.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context,ServiceClass.class);
        context.startService(intentService);

    }
}
