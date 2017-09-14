package com.example.kunmingliu.mytomato.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.kunmingliu.mytomato.Module.GetTimeEvent;
import com.example.kunmingliu.mytomato.Parameters.Const;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by kunmingliu on 2017/9/13.
 */

public class GetTimeService extends Service {
    private Handler handler = null;
    private Runnable repeatRunnable = null;
    private GetTimeEvent getTimeEvent =null;
    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        if(handler == null){
            handler = new Handler();
        }
        if(getTimeEvent == null){
            getTimeEvent = new GetTimeEvent();
        }
        if(repeatRunnable == null){
            repeatRunnable = new Runnable() {
                @Override
                public void run() {
                    //sendBroadcast(sendIntent);
                    EventBus.getDefault().post(getTimeEvent);
                    handler.postDelayed(this,500);
                }
            };
        }
        handler.post(repeatRunnable);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
