package com.example.pq.puzzlegame.util;

import android.util.Log;

import com.example.pq.puzzlegame.BuildConfig;

/**
 * Created by pq on 2018/1/21.
 */
public class LogUtil {

    public static void d(String tag,String msg){
        if (BuildConfig.DEBUG){
            Log.d(tag,msg);
        }
    }
}
