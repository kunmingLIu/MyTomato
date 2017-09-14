package com.example.kunmingliu.mytomato.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.kunmingliu.mytomato.Parameters.Const;

import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by kunming.liu on 2017/9/13.
 */

public class ClockService extends Service {
    private Calendar calendar = null;
    private long alarmTime ;
    private FutureTask<Boolean> futureTask = null;
    private ClockRunnable clockRunnable = null;
    private ExecutorService executorService ;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Test", "onCreate");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.e("Test", "onStartCommand");
        if(intent != null){
            alarmTime = intent.getLongExtra("alarmTime",-1);
        }
        if(calendar == null){
            calendar = Calendar.getInstance();
        }
        if(clockRunnable == null){
            clockRunnable = new ClockRunnable();
        }
        if(futureTask == null){
            futureTask = new FutureTask<Boolean>(clockRunnable){
                @Override
                public void run() {
                    super.run();
                }

                @Override
                protected void done() {
                    super.done();
                    Intent broadcastIntent = new Intent(Const.BROADCAST_CLOCK);
                    sendBroadcast(broadcastIntent);
                }
            };
        }
        if(executorService == null){
            executorService = Executors.newSingleThreadExecutor();
        }
        executorService.submit(futureTask);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private class ClockRunnable implements Callable<Boolean>{
        @Override
        public Boolean call() throws Exception {
            // TODO: 2017/9/13 不會跳出while迴圈
            /*
            一開始上面activity傳過來是long型態，但是用getInt去接，因此都是-1
            另外，cal.getTimeInMillis是不會隨著系統時間跟著變化，必須要在做一次cal.getInstance
            因此改成System.currentTimeMillis()
             */
            while(System.currentTimeMillis()!= alarmTime){

            }
            return true;
        }
    }

}
