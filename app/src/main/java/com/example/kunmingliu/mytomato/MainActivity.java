package com.example.kunmingliu.mytomato;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kunmingliu.mytomato.Module.GetTimeEvent;
import com.example.kunmingliu.mytomato.Parameters.Const;
import com.example.kunmingliu.mytomato.Service.AlarmToStartService;
import com.example.kunmingliu.mytomato.Service.ClockService;
import com.example.kunmingliu.mytomato.Service.GetTimeService;
import com.example.kunmingliu.mytomato.View.TomatoClockView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tomatoClock)
    TomatoClockView tomatoClock;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.button)
    Button button;
    private MyReceiver myReceiver;
    private IntentFilter intentFilter ;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        EventBus.getDefault().register(MainActivity.this);
        myReceiver = new MyReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Const.BROADCAST_CLOCK);
        registerReceiver(myReceiver,intentFilter);


        Intent intent = new Intent(MainActivity.this, GetTimeService.class);
        startService(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tomatoClock != null) {
            tomatoClock.setCurrentTime();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
        EventBus.getDefault().unregister(MainActivity.this);
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND,25);
        Intent intent = new Intent(MainActivity.this, ClockService.class);
        intent.putExtra("alarmTime", cal.getTimeInMillis());
        startService(intent);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(10,0);
        valueAnimator.setDuration(25*1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        valueAnimator.start();

    }
    public class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent != null){
                String action = intent.getAction();
                if(action.equals(Const.BROADCAST_CLOCK)){
                    Toast.makeText(context, "倒數結束拉", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    //publish from GetTimeService
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setCurrentTime(GetTimeEvent event){
        if (tomatoClock != null) {
            tomatoClock.setCurrentTime();
        }
    }
}
