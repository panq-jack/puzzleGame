package com.example.pq.puzzlegame.util;

import android.content.Context;
import android.util.TypedValue;

public class DPIUtil {


    public static float dp2px(Context context,float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources().getDisplayMetrics());
    }

    public static float sp2px(Context context,float sp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,context.getResources().getDisplayMetrics());
    }

    public static float px2dp(Context context,float px){
        float density=context.getResources().getDisplayMetrics().density;
        return px/density+.5f;
    }

    public static float px2sp(Context context,float px){
        float scale=context.getResources().getDisplayMetrics().scaledDensity;
        return px/scale+.5f;
    }

    public static int getWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
