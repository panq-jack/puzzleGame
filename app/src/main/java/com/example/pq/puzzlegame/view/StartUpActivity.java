package com.example.pq.puzzlegame.view;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pq.puzzlegame.R;
import com.example.pq.puzzlegame.view.view.CustomIndicator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by pq on 2018/3/17.
 */
public class StartUpActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager viewPager;
    private TextView enterView,ruleView;
    private CustomIndicator customIndicator;
    private AlertDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        enterView=(TextView)findViewById(R.id.enter);
        ruleView=(TextView)findViewById(R.id.rule);
        customIndicator=(CustomIndicator)findViewById(R.id.indices);

        initViewPager();
        enterView.setOnClickListener(this);
        ruleView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.rule:
                if (!isFinishing()){
                    getIntroDialog().show();
                }
                break;
            case R.id.enter:
                startActivity(new Intent(StartUpActivity.this,GuideActivity.class));
                finish();
                break;

            default:
                break;
        }
    }

    private void initViewPager(){
        //获取图片
        TypedArray typedArray = getResources().obtainTypedArray(R.array.pics_startup);
        Bitmap[] bitmaps = new Bitmap[typedArray.length()];
        int[] mResPicIds = new int[typedArray.length()];
        customIndicator.initData(typedArray.length());
        customIndicator.setSelected(0);
        for (int i = 0; i < bitmaps.length; i++) {
            mResPicIds[i] = typedArray.getResourceId(i, 0);
            bitmaps[i] = BitmapFactory.decodeResource(
                    getResources(), mResPicIds[i]);
        }
        typedArray.recycle();

        final List<Bitmap> bitmapList= Arrays.asList(bitmaps);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return bitmapList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==(View)object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView=new ImageView(container.getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageBitmap(bitmapList.get(position));
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View)object);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                customIndicator.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private AlertDialog getIntroDialog(){
        if (null==dialog){
            View root= LayoutInflater.from(this).inflate(R.layout.view_introduce,null);
            TextView sureView=(TextView)root.findViewById(R.id.sure);
            sureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isFinishing() && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            });
            dialog=new AlertDialog.Builder(this)
                    .setTitle("规则")
                    .setView(root)
                    .setCancelable(true)
                    .create();
        }
        return dialog;
    }
}
