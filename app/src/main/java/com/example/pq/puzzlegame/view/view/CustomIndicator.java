package com.example.pq.puzzlegame.view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.pq.puzzlegame.R;

/**
 * Created by pq on 2018/3/17.
 */
public class CustomIndicator extends LinearLayout {

    private static LinearLayout.LayoutParams roundIndicatorParam;
    static {
        roundIndicatorParam=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        roundIndicatorParam.leftMargin=3;
        roundIndicatorParam.rightMargin=3;
    }
    public CustomIndicator(Context context) {
        super(context);
        initView();
    }

    public CustomIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CustomIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
    }


    public void initData(int totalNum){
        for (int i=0;i<totalNum;++i){
            CustomRoundView roundView=new CustomRoundView(getContext());
            addView(roundView,roundIndicatorParam);
        }
    }


    public void setSelected(int pos){
        int totalNum=getChildCount();
        if (pos<0 || pos>=totalNum)return;
        for (int i=0;i<totalNum;++i){
            CustomRoundView view=(CustomRoundView) getChildAt(i);
            view.setSelected(i==pos);
        }
    }

    public static class CustomRoundView extends ImageView{
        public CustomRoundView(Context context) {
            super(context);
            setBackgroundResource(R.drawable.shape_indicator);
        }

    }



}
