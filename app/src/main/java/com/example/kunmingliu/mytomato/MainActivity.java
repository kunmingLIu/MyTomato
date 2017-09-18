package com.example.kunmingliu.mytomato;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kunmingliu.mytomato.Dialog.ClockDialogFragment;
import com.example.kunmingliu.mytomato.Module.GetTimeEvent;
import com.example.kunmingliu.mytomato.Module.TimerEvent;
import com.example.kunmingliu.mytomato.Service.GetTimeService;
import com.example.kunmingliu.mytomato.Service.TimerService;
import com.example.kunmingliu.mytomato.View.TomatoClockView;
import com.example.kunmingliu.mytomato.View.TomatoClockViewWithPath;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.support.v4.app.Fragment;

import static com.example.kunmingliu.mytomato.Utils.Util.log;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tomatoClock)
    TomatoClockViewWithPath tomatoClock;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.edit)
    EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        EventBus.getDefault().register(MainActivity.this);

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
        EventBus.getDefault().unregister(MainActivity.this);
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.SECOND, 10);
//        Intent intent = new Intent(MainActivity.this, TimerService.class);
//        intent.putExtra("alarmTime", cal.getTimeInMillis());
//        startService(intent);
//
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(10, 0);
//        valueAnimator.setDuration(10 * 1000);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                textView.setText(String.valueOf(animation.getAnimatedValue()));
//            }
//        });
//        valueAnimator.start();

            //測試tomatoClock
//        if(!TextUtils.isEmpty(edit.getText().toString())){
//            int workMin = Integer.parseInt(edit.getText().toString());
//            tomatoClock.startWork(0,workMin,0);
//        }
        showEditDialog();

    }

    //publish from GetTimeService
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void subscribeGetTimeEvent(GetTimeEvent event) {
        if (tomatoClock != null) {
            tomatoClock.setCurrentTime();
        }
    }

    //publish from TimerService
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void subscribeTimerEvent(TimerEvent event) {
        log("timerTimeOut");
        Toast.makeText(MainActivity.this, "倒數結束拉", Toast.LENGTH_SHORT).show();
    }

    public void showEditDialog()
    {
        ClockDialogFragment editNameDialog = new ClockDialogFragment();
        editNameDialog.show(getSupportFragmentManager(), "EditNameDialog");
    }

}
