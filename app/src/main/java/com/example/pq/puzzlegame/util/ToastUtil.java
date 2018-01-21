package com.example.pq.puzzlegame.util;


import android.content.Context;
import android.widget.Toast;

public class ToastUtil {


    public static void shortToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

}
