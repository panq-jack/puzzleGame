package com.example.pq.puzzlegame.util;

import android.graphics.Bitmap;

import com.example.pq.puzzlegame.bean.ItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 拼图工具类：实现拼图的交换与生成算法
 *
 * @author xys
 */
public class GameUtil {

    // 游戏信息单元格Bean
    private List<ItemBean> mItemBeans;
    // 空格单元格
    private ItemBean mBlankItemBean;

    private int mType;


    private GameUtil(){
        mItemBeans = new ArrayList<ItemBean>();
        mType=ConstantUtil.TYPE_N.TYPE_2;
//        mBlankItemBean = new ItemBean();
    }



    /**
     * 判断点击的Item是否可移动
     *
     * @param position position
     * @return 能否移动
     */
    public boolean isMoveable(int position) {
        // 获取空格Item
        int blankId = mBlankItemBean.getItemId() - 1;
        // 不同行 相差为type
        if (Math.abs(blankId - position) == mType) {
            return true;
        }
        // 相同行 相差为1
        if ((blankId / mType == position / mType) &&
                Math.abs(blankId - position) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 交换空格与点击Item的位置 包括bitmap,bitmapId
     *
     * @param from  交换图
     * @param blank 空白图
     */
    public void swapItems(ItemBean from, ItemBean blank) {
        ItemBean tempItemBean = new ItemBean();
        // 交换BitmapId
        tempItemBean.setBitmapId(from.getBitmapId());
        from.setBitmapId(blank.getBitmapId());
        blank.setBitmapId(tempItemBean.getBitmapId());
        // 交换Bitmap
        tempItemBean.setBitmap(from.getBitmap());
        from.setBitmap(blank.getBitmap());
        blank.setBitmap(tempItemBean.getBitmap());
        // 设置新的Blank
         mBlankItemBean= from;
    }

    /**
     * 生成随机的Item
     */
    public void getPuzzleGenerator() {
        int index = 0;
        // 随机打乱顺序
        for (int i = 0; i < mItemBeans.size(); i++) {
            index = (int) (Math.random() * mType*mType);
            swapItems(mItemBeans.get(index), mBlankItemBean);
        }
        List<Integer> data = new ArrayList<Integer>();
        for (int i = 0; i < mItemBeans.size(); i++) {
            data.add(mItemBeans.get(i).getBitmapId());
        }
        // 判断生成是否有解
        if (canSolve(data)) {
            return;
        } else {
            getPuzzleGenerator();
        }
    }

    /**
     * 是否拼图成功
     *
     * @return 是否拼图成功
     */
    public  boolean isSuccess() {
        for (ItemBean tempBean : mItemBeans) {
            if (tempBean.getBitmapId() != 0 &&
                    (tempBean.getItemId()) == tempBean.getBitmapId()) {
                continue;
            } else if (tempBean.getBitmapId() == 0 &&
                    tempBean.getItemId() == mType*mType) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 该数据是否有解
     *
     * @param data 拼图数组数据
     * @return 该数据是否有解
     */
    private boolean canSolve(List<Integer> data) {
        // 获取空格Id
        int blankId = mBlankItemBean.getItemId();
        // 可行性原则
        if (data.size() % 2 == 1) {
            return getInversions(data) % 2 == 0;
        } else {
            // 从底往上数,空格位于奇数行
            if (((blankId - 1) / mType) % 2 == 1) {
                return getInversions(data) % 2 == 0;
            } else {
                // 从底往上数,空位位于偶数行
                return getInversions(data) % 2 == 1;
            }
        }
    }

    /**
     * 计算倒置和算法
     *
     * @param data 拼图数组数据
     * @return 该序列的倒置和
     */
    public static int getInversions(List<Integer> data) {
        int inversions = 0;
        int inversionCount = 0;
        for (int i = 0; i < data.size(); i++) {
            for (int j = i + 1; j < data.size(); j++) {
                int index = data.get(i);
                if (data.get(j) != 0 && data.get(j) < index) {
                    inversionCount++;
                }
            }
            inversions += inversionCount;
            inversionCount = 0;
        }
        return inversions;
    }

    public static GameUtil getInstance(){
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder{
        private final static GameUtil INSTANCE=new GameUtil();
    }

    public List<ItemBean> getItemBeans(){
        return mItemBeans;
    }
    public void setBlankItemBean(ItemBean blankBean){
        this.mBlankItemBean=blankBean;
    }
    public ItemBean getBlankItemBean(){
        return mBlankItemBean;
    }
    public void setType(int type){
        this.mType=type;
    }

    public void clear(){
        this.mItemBeans.clear();
        this.mBlankItemBean=null;
    }
}
