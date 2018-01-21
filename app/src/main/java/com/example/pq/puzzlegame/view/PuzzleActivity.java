package com.example.pq.puzzlegame.view;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pq.puzzlegame.R;
import com.example.pq.puzzlegame.adapter.GridViewPuzzleAdapter;
import com.example.pq.puzzlegame.bean.ItemBean;
import com.example.pq.puzzlegame.util.ConstantUtil;
import com.example.pq.puzzlegame.util.DPIUtil;
import com.example.pq.puzzlegame.util.GameUtil;
import com.example.pq.puzzlegame.util.LogUtil;
import com.example.pq.puzzlegame.util.ToastUtil;
import com.example.pq.puzzlegame.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PuzzleActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "PuzzleActivity";

    private final static int CLICK_COOL_TIME=2000;

    private final static int MSG_WHAT_TIMER=1;
    private final static int MSG_WHAT_COUNTERDOWN=2;
    private final static int MSG_WHAT_IMGCLICK=3;
    // 拼图完成时显示的最后一个图片
    public static Bitmap mLastBitmap;
    //待处理的图片
    private Bitmap mSelectedBitmap;
    // 切图后的图片
    private List<Bitmap> mBitmapItemLists = new ArrayList<Bitmap>();
    private List<ItemBean> mItemBeanLists = new ArrayList<>();
    //intent
    private int mCount;
    private int mDifficulty;
    private int mResId;
    private String mPicPath;

    // 步数显示
    public static int COUNT_INDEX = 0;
    // 计时显示
    public static int TIMER_INDEX = 0;

    //view
    private Button mBtnImage, mBtnReset, mBtnRecord;
    private TextView mTvStepCounter, mTvTimer;
    private GridView mGridView;
    private ImageView mOriginImgView;
    private FrameLayout mOriginImgViewContainer;

    // GridView适配器
    private GridViewPuzzleAdapter mAdapter;

    //
    AlertDialog mExitDialog;

    private boolean isOriginImgShown=false;
    // 冷却时间--显示隐藏原图按钮
    private boolean canClickImgBtn=true;
    //  计时器是否可以计时
    private boolean canTimerRunning=true;


    /**
     * UI更新Handler
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_TIMER:
                    // 更新计时器
                    if (!canTimerRunning || isOriginImgShown)return;
                    TIMER_INDEX++;
                    mTvTimer.setText(getString(R.string.puzzle_timer, TIMER_INDEX));
                    sendEmptyMessageDelayed(MSG_WHAT_TIMER,1000);
                    break;
                case MSG_WHAT_COUNTERDOWN:
                    //更新倒计时
                    if (!canTimerRunning || isOriginImgShown)return;
                    TIMER_INDEX--;

                    break;
                case MSG_WHAT_IMGCLICK:
                    canClickImgBtn=true;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        handleIntent();
        //首先处理传过来的图片
        handleSelectedImg();
        initView();
        initData();


    }

    private void handleIntent() {
        // 选择默认图片还是自定义图片
        mResId = getIntent().getExtras().getInt("picSelectedID");
        mPicPath = getIntent().getExtras().getString("mPicPath");

        mCount = getIntent().getExtras().getInt("mCount", Util.type2count(ConstantUtil.TYPE_N.TYPE_2));
        mDifficulty = getIntent().getExtras().getInt("mDifficulty", ConstantUtil.DIFFICUILTY.NORMAL);
    }

    private void handleSelectedImg() {
        Bitmap selectedImgTemp = null;
        if (0 != mResId) {
            selectedImgTemp = BitmapFactory.decodeResource(getResources(), mResId);
        } else {
            selectedImgTemp = BitmapFactory.decodeFile(mPicPath);
        }
        // 将图片放大到固定尺寸
        int screenWidth = DPIUtil.getWidth(this);
        int screenHeight = DPIUtil.getHeight(this);
        mSelectedBitmap = Util.resizeBitmap(
                screenWidth * 0.8f, screenHeight * 0.6f, selectedImgTemp);
    }

    private void initView() {
        // Button
        mBtnRecord = (Button) findViewById(R.id.btn_puzzle_record);
        mBtnImage = (Button) findViewById(R.id.btn_puzzle_origin_img);
        mBtnReset = (Button) findViewById(R.id.btn_puzzle_reset);
        // GridView
        mGridView = (GridView) findViewById(
                R.id.gv_puzzle);

        RelativeLayout.LayoutParams gridParams = (RelativeLayout.LayoutParams) mGridView.getLayoutParams();
        if (null != gridParams) {
            gridParams.width = mSelectedBitmap.getWidth();
            gridParams.height = mSelectedBitmap.getHeight();
        }
        // 水平居中
        gridParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        // Grid显示
        mGridView.setLayoutParams(gridParams);
        mGridView.setHorizontalSpacing(0);
        mGridView.setVerticalSpacing(0);
        // TV步数
        mTvStepCounter = (TextView) findViewById(
                R.id.tv_puzzle_stepcount);
        mTvStepCounter.setText(getResources().getString(R.string.puzzle_stepCounter, COUNT_INDEX));
        // TV计时器
        mTvTimer = (TextView) findViewById(R.id.tv_puzzle_timer);
        mTvTimer.setText(getResources().getString(R.string.puzzle_timer, TIMER_INDEX));

        // 点击事件
        mBtnRecord.setOnClickListener(this);
        // 显示原图按钮点击事件
        mBtnImage.setOnClickListener(this);
        // 重置按钮点击事件
        mBtnReset.setOnClickListener(this);

        //gridView点击事件
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LogUtil.d(TAG, "mGridView -----> itemClick  : " + i + "    " + l);
                // 判断是否可移动
                if (GameUtil.getInstance().isMoveable(i)) {
                    // 交换点击Item与空格的位置
                    GameUtil.getInstance().swapItems(GameUtil.getInstance().getItemBeans().get(i),
                            GameUtil.getInstance().getBlankItemBean());
                    // 重新获取图片
                    recreateData();
                    // 通知GridView更改UI
                    mAdapter.notifyDataSetChanged();
                    // 更新步数
                    COUNT_INDEX++;
                    mTvStepCounter.setText(getString(R.string.puzzle_stepCounter, COUNT_INDEX));
                    // 判断是否成功
                    if (GameUtil.getInstance().isSuccess()) {
                        // 将最后一张图显示完整
                        recreateData();
                        mBitmapItemLists.remove(mCount * mCount - 1);
                        mBitmapItemLists.add(mLastBitmap);
                        // 通知GridView更改UI
                        mAdapter.notifyDataSetChanged();
                        ToastUtil.longToast(PuzzleActivity.this, "拼图成功!");
                        mGridView.setEnabled(false);
                        pauseTimer();
                    }
                }
            }
        });
        showOrHideOriginImgView();
    }

    private void initData() {
        // 设置为N*N显示
        mGridView.setNumColumns(mCount);

        // 生成游戏数据
        generateGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cleanConfig();
//        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_puzzle_origin_img:
            case R.id.puzzle_img_container:
                if (!canClickImgBtn)return;
                canClickImgBtn=false;
                mHandler.sendEmptyMessageDelayed(MSG_WHAT_IMGCLICK,CLICK_COOL_TIME);
                showOrHideOriginImgView();
                break;
            case R.id.btn_puzzle_reset:
                cleanConfig();
                resetTimer();
                generateGame();
                recreateData();
                // 通知GridView更改UI
                mTvStepCounter.setText(getString(R.string.puzzle_stepCounter, COUNT_INDEX));
                mAdapter.notifyDataSetChanged();
                mGridView.setEnabled(true);
                break;
            case R.id.btn_puzzle_record:

                break;
            default:
                break;
        }
    }

    private void generateGame() {
        // 切图 获取初始拼图数据 正常顺序
        Util.createInitBitmaps(PuzzleActivity.this, mCount, mSelectedBitmap);
        // 生成随机数据
        GameUtil.getInstance().getPuzzleGenerator();
        // 获取Bitmap集合
        for (ItemBean temp : GameUtil.getInstance().getItemBeans()) {
            mBitmapItemLists.add(temp.getBitmap());
        }
        // 数据适配器
        mAdapter = new GridViewPuzzleAdapter(this, mBitmapItemLists);
        mGridView.setAdapter(mAdapter);


    }

    /**
     * 重新获取图片
     */
    private void recreateData() {
        mBitmapItemLists.clear();
        for (ItemBean temp : GameUtil.getInstance().getItemBeans()) {
            mBitmapItemLists.add(temp.getBitmap());
        }
    }

    private void startOrResumeTimer() {
        canTimerRunning=true;
        mHandler.sendEmptyMessageDelayed(MSG_WHAT_TIMER,1000);
    }

    private void pauseTimer() {
        canTimerRunning=false;
    }

    private void resetTimer(){
        pauseTimer();
        mTvTimer.setText(getString(R.string.puzzle_timer,0));
        startOrResumeTimer();
    }

    /**
     *
     */
    private void showOrHideOriginImgView(){
        if (null==mOriginImgViewContainer){
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(
                    R.id.rl_puzzle_main_main_layout);
            mOriginImgViewContainer=new FrameLayout(this);
            mOriginImgViewContainer.setId(R.id.puzzle_img_container);
            mOriginImgViewContainer.setBackgroundColor(Color.parseColor("#A0727272"));
            RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)(mSelectedBitmap.getHeight()+DPIUtil.dp2px(this,100)));
            relativeLayout.addView(mOriginImgViewContainer,layoutParams);

            mOriginImgView=new ImageView(this);
            mOriginImgView.setImageBitmap(mSelectedBitmap);
            FrameLayout.LayoutParams layoutParams1=new FrameLayout.LayoutParams(
                    (int)(mSelectedBitmap.getWidth()*.9f),(int)(mSelectedBitmap.getHeight()*.9f));
            layoutParams1.gravity= Gravity.CENTER;
            mOriginImgViewContainer.addView(mOriginImgView,layoutParams1);
            mOriginImgViewContainer.setOnClickListener(this);
        }
        if (!isOriginImgShown){
            Animation animShow= AnimationUtils.loadAnimation(this,R.anim.image_show_anim);
            mOriginImgViewContainer.startAnimation(animShow);
            mOriginImgViewContainer.setVisibility(View.VISIBLE);
            isOriginImgShown=true;
            mBtnImage.setText(getString(R.string.puzzle_hide_origin_img));
            pauseTimer();

        }else {
            Animation animHide=AnimationUtils.loadAnimation(this,R.anim.image_hide_anim);
            mOriginImgViewContainer.startAnimation(animHide);
            mOriginImgViewContainer.setVisibility(View.GONE);
            isOriginImgShown=false;
            mBtnImage.setText(getString(R.string.puzzle_show_origin_img));
            startOrResumeTimer();
        }
    }

    /**
     * 清空相关参数设置
     */
    private void cleanConfig() {
        // 清空相关参数设置
        GameUtil.getInstance().clear();
        // 停止计时器
        mHandler.removeCallbacksAndMessages(null);
        COUNT_INDEX = 0;
        TIMER_INDEX = 0;
        // 清除拍摄的照片
        if (mPicPath != null) {
            // 删除照片
            File file = new File(mPicPath);
            if (file.exists()) {
                if (file.delete()) {
                    GuideActivity.TEMP_IMAGE_PATH = null;
                }
            }
        }
    }

    private void showExitdialog(){
        if (null==mExitDialog){
            mExitDialog=new AlertDialog.Builder(this)
                    .setMessage("你正在游戏中，确定要退出吗")
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!isFinishing()){
                                finish();
                            }
                        }
                    })
                    .setNegativeButton("暂不", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!isFinishing()){
                                mExitDialog.dismiss();
                            }
                        }
                    })
                    .create();
        }
        if (!isFinishing()){
            mExitDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (COUNT_INDEX>0){
            showExitdialog();
        }else {
            super.onBackPressed();
        }
    }
}
