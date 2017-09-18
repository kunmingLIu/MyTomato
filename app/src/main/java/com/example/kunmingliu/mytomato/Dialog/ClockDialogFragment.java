package com.example.kunmingliu.mytomato.Dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.kunmingliu.mytomato.R;
import com.example.kunmingliu.mytomato.Utils.Util;
import com.example.kunmingliu.mytomato.View.ClockView;
import com.example.kunmingliu.mytomato.View.ClockViewWithPath;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by kunming.liu on 2017/9/15.
 */

public class ClockDialogFragment extends DialogFragment {
    @BindView(R.id.txtDivide)
    TextView txtDivide;
    @BindView(R.id.txtHour)
    TextView txtHour;
    @BindView(R.id.txtMin)
    TextView txtMin;
    @BindView(R.id.txtAM)
    TextView txtAM;
    @BindView(R.id.txtPM)
    TextView txtPM;
    @BindView(R.id.clock)
    ClockViewWithPath clock;
    @BindView(R.id.txtOK)
    TextView txtOK;
    @BindView(R.id.txtCancel)
    TextView txtCancel;
    private Unbinder unbinder;
    private int clickedColor ;
    private int unclickedColor ;
    private Calendar calendar ;
    private int am_pm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_login_dialog, container);
        unbinder = ButterKnife.bind(this, view);

        unclickedColor = ContextCompat.getColor(getContext(),R.color.white);
        clickedColor = ContextCompat.getColor(getContext(),R.color.aqua);

        calendar = Calendar.getInstance();
        txtHour.setText(formatTimeText(calendar.get(Calendar.HOUR)));

        txtMin.setText(formatTimeText(calendar.get(Calendar.MINUTE)));

        am_pm = calendar.get(Calendar.AM_PM);
        if(am_pm == 0){
            txtAM.setTextColor(clickedColor);
            txtPM.setTextColor(unclickedColor);
        }else{
            txtAM.setTextColor(unclickedColor);
            txtPM.setTextColor(clickedColor);
        }
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.txtHour, R.id.txtMin, R.id.txtAM, R.id.txtPM, R.id.txtOK, R.id.txtCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txtHour:
                txtMin.setTextColor(unclickedColor);
                txtHour.setTextColor(clickedColor);
                clock.setHour(true);
                break;
            case R.id.txtMin:
                txtHour.setTextColor(unclickedColor);
                txtMin.setTextColor(clickedColor);
                clock.setHour(false);
                break;
            case R.id.txtAM:
                txtPM.setTextColor(unclickedColor);
                txtAM.setTextColor(clickedColor);
                break;
            case R.id.txtPM:
                txtAM.setTextColor(unclickedColor);
                txtPM.setTextColor(clickedColor);
                break;
            case R.id.txtOK:
                break;
            case R.id.txtCancel:
                this.dismiss();
                break;
        }
    }
    private String formatTimeText(int time){
        if(time < 10){
            return "0"+String.valueOf(time);
        }
        return  String.valueOf(time);
    }
}
