package com.example.pq.puzzlegame.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Environment;

import com.example.pq.puzzlegame.R;
import com.example.pq.puzzlegame.bean.ItemBean;
import com.example.pq.puzzlegame.view.PuzzleActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pq on 2018/1/20.
 */
public class Util {

    public static File getAvailableFile(Context context, String fileName) {
        File file = null;
//        if ( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
//        } else {
        file = new File(context.getExternalCacheDir() + File.separator + fileName);
//        }
        return file;
    }

    /**
     * 切图、初始状态（正常顺序）
     *
     * @param type        游戏种类
     * @param picSelected 选择的图片
     * @param context     context
     */
    public static void createInitBitmaps(Context context, int type, Bitmap picSelected) {
        Bitmap bitmapTemp = null;
        ItemBean beanTemp = null;
        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        // 每个Item的宽高
        int itemWidth = picSelected.getWidth() / type;
        int itemHeight = picSelected.getHeight() / type;
        for (int i = 1; i <= type; i++) {
            for (int j = 1; j <= type; j++) {
                bitmapTemp = Bitmap.createBitmap(
                        picSelected,
                        (j - 1) * itemWidth,
                        (i - 1) * itemHeight,
                        itemWidth,
                        itemHeight);
                bitmaps.add(bitmapTemp);
                beanTemp = new ItemBean(
                        (i - 1) * type + j,
                        (i - 1) * type + j,
                        bitmapTemp);
                GameUtil.getInstance().getItemBeans().add(beanTemp);
            }
        }


        // 保存最后一个图片在拼图完成时填充
        PuzzleActivity.mLastBitmap = bitmaps.get(type * type - 1);
        // 设置最后一个为空Item
        bitmaps.remove(type * type - 1);
        GameUtil.getInstance().getItemBeans().remove(type * type - 1);
        Bitmap blankBitmap = BitmapFactory.decodeResource(
                context.getResources(), R.mipmap.blank);
        blankBitmap = Bitmap.createBitmap(
                blankBitmap, 0, 0, itemWidth, itemHeight);

        bitmaps.add(blankBitmap);
        GameUtil.getInstance().getItemBeans().add(new ItemBean(type * type, 0, blankBitmap));
        GameUtil.getInstance().setBlankItemBean(GameUtil.getInstance().getItemBeans().get(type * type - 1));
        GameUtil.getInstance().setType(type);
    }


    /**
     * 处理图片 放大、缩小到合适位置
     *
     * @param newWidth  缩放后Width
     * @param newHeight 缩放后Height
     * @param bitmap    bitmap
     * @return bitmap
     */
    public static Bitmap resizeBitmap(float newWidth, float newHeight, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(
                newWidth / bitmap.getWidth(),
                newHeight / bitmap.getHeight());
        Bitmap newBitmap = Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix, true);
        return newBitmap;
    }

    /**
     * 类型 n*n  转换为 具体的行列数
     *
     * @return
     */
    public static int type2count(int type) {
        int count;
        switch (type) {
            case ConstantUtil.TYPE_N.TYPE_3:
                count = 3;
                break;
            case ConstantUtil.TYPE_N.TYPE_4:
                count = 4;
                break;
            case ConstantUtil.TYPE_N.TYPE_5:
                count = 5;
                break;
            case ConstantUtil.TYPE_N.TYPE_2:
            default:
                count = 2;
                break;
        }
        return count;
    }


}
