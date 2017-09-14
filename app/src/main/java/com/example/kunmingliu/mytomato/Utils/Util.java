package com.example.kunmingliu.mytomato.Utils;

import android.os.Build;
import android.util.Log;

import com.example.kunmingliu.mytomato.BuildConfig;

/**
 * Created by kunming.liu on 2017/9/14.
 */

public class Util {
    public static void log(String message, Object... formatArgs) {
        if(BuildConfig.DEBUG){
            if (formatArgs != null && formatArgs.length > 0) {
                Log.e("MyTomato", String.format(message, formatArgs));
            } else {
                Log.e("MyTomato", message);
            }
        }
    }
}
