package com.example.pq.puzzlegame.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.example.pq.puzzlegame.BuildConfig;
import com.example.pq.puzzlegame.R;
import com.example.pq.puzzlegame.adapter.GridViewResPicAdapter;
import com.example.pq.puzzlegame.util.ConstantUtil;
import com.example.pq.puzzlegame.util.LogUtil;
import com.example.pq.puzzlegame.util.Util;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {
    private final static String TAG = "GuideActivity";


    private static final int REQUEST_CODE_PERMISSION_CAMERA = 100;
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 200;
    private static final int REQUEST_CODE_SETTING = 300;
    // 返回码：系统图库
    private static final int RESULT_IMAGE = 1;
    // 返回码：相机
    private static final int RESULT_CAMERA = 2;
    // IMAGE TYPE
    private static final String IMAGE_TYPE = "image/*";

    private static final String TEMP_PIC_NAME = "temp.png";
    // Temp照片路径
    public static String TEMP_IMAGE_PATH;
    // GridView 显示图片
    private GridView mGridView;
    private Spinner mSpinner1, mSpinner2;
    private AlertDialog mChoosePicDialog;


    private List<Bitmap> mPicBitmaps;
    // 主页图片资源ID
    private int[] mResPicIds;


    // 游戏类型N*N
    private int mType = ConstantUtil.TYPE_N.TYPE_2;
    private int mDifficulty = ConstantUtil.DIFFICUILTY.NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // 初始化Views
        initViews();
        initDatas();
        TEMP_IMAGE_PATH = Util.getAvailableFile(this, TEMP_PIC_NAME).getPath();
    }

    /**
     *
     */
    private void initViews() {
        mSpinner1 = (Spinner) findViewById(R.id.sp_guide_type1);
        mSpinner2 = (Spinner) findViewById(R.id.sp_guide_type2);

        mGridView = (GridView) findViewById(
                R.id.gv_guide_pic_list);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                if (position == mResPicIds.length - 1) {
                    // 选择本地图库 相机
                    showChoosePicDialog();
                } else {
                    // 选择默认图片
                    Intent intent = new Intent(GuideActivity.this, PuzzleActivity.class);
                    intent.putExtra("picSelectedID", mResPicIds[position]);
                    intent.putExtra("mCount", Util.type2count(mType));
                    intent.putExtra("mDifficulty", mDifficulty);
                    startActivity(intent);
                }
            }
        });

        mSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "mSpinner1-->onItemSelected  i: " + i + "     l: " + l);
                mType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                ToastUtil.shortToast(GuideActivity.this, "onNothingSelected: ");
            }
        });

        mSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "mSpinner2-->onItemSelected  i: " + i + "     l: " + l);
                mDifficulty = i;
                if (ConstantUtil.DIFFICUILTY.CHALLENAGE == mDifficulty) {
                    mSpinner1.setSelection(ConstantUtil.TYPE_N.TYPE_5);
                    mSpinner1.setEnabled(false);
                } else if (ConstantUtil.DIFFICUILTY.NORMAL == mDifficulty) {
                    mSpinner1.setSelection(ConstantUtil.TYPE_N.TYPE_2);
                    mSpinner1.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                ToastUtil.shortToast(GuideActivity.this, "onNothingSelected: ");
            }
        });

    }

    private void initDatas() {
        // 初始化spinner数据
        String[] item1 = getResources().getStringArray(R.array.chooser_type1);
        ArrayAdapter<String> spinner1Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, item1);
//        spinner1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner1.setAdapter(spinner1Adapter);

        String[] item2 = getResources().getStringArray(R.array.chooser_type2);
        ArrayAdapter<String> spinner2Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, item2);
        mSpinner2.setAdapter(spinner2Adapter);


        // 初始化Bitmap数据
        if (null == mPicBitmaps) {
            mPicBitmaps = new ArrayList<Bitmap>();
        }
//        mResPicIds = getResources().getIntArray(R.array.pics_ids);
        //获取图片
        TypedArray typedArray = getResources().obtainTypedArray(R.array.pics_ids);
        Bitmap[] bitmaps = new Bitmap[typedArray.length()];
        mResPicIds = new int[typedArray.length()];
        for (int i = 0; i < bitmaps.length; i++) {
            mResPicIds[i] = typedArray.getResourceId(i, 0);
            bitmaps[i] = BitmapFactory.decodeResource(
                    getResources(), mResPicIds[i]);
            mPicBitmaps.add(bitmaps[i]);
        }
        typedArray.recycle();
        mGridView.setAdapter(new GridViewResPicAdapter(
                this, mPicBitmaps));
    }

    private void showChoosePicDialog() {
        if (null == mChoosePicDialog) {
            String[] items = getResources().getStringArray(R.array.chooser_pic);
            AlertDialog.Builder builder = new AlertDialog.Builder(GuideActivity.this)
                    .setTitle("请选择:")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (0 == i) {
                                // 本地图册
                                AndPermission.with(GuideActivity.this)
                                        .requestCode(REQUEST_CODE_PERMISSION_STORAGE)
                                        .permission(Permission.STORAGE)
                                        .callback(GuideActivity.this)
                                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                                        // 这样避免用户勾选不再提示，导致以后无法申请权限。
                                        // 你也可以不设置。
                                        .rationale(new RationaleListener() {
                                            @Override
                                            public void showRequestPermissionRationale(int i, Rationale rationale) {
                                                AndPermission.rationaleDialog(GuideActivity.this, rationale).show();
//                                                rationale.resume();
                                            }
                                        })
                                        .start();

                            } else if (1 == i) {
                                {
                                    // 系统相机 权限申请 6.0
//                                    if (!hasPermission(Manifest.permission.CAMERA)) {
//                                        requestPermission(Manifest.permission.CAMERA, PERMISSION_REQUEST_CODE_CAMERA);
//                                    } else {
//                                        openCamera();
//                                    }
                                    AndPermission.with(GuideActivity.this)
                                            .requestCode(REQUEST_CODE_PERMISSION_CAMERA)
                                            .permission(Permission.CAMERA)
                                            .callback(GuideActivity.this)
                                            // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                                            // 这样避免用户勾选不再提示，导致以后无法申请权限。
                                            // 你也可以不设置。
                                            .rationale(new RationaleListener() {
                                                @Override
                                                public void showRequestPermissionRationale(int i, Rationale rationale) {
                                                    AndPermission.rationaleDialog(GuideActivity.this, rationale).show();
                                                }
                                            })
                                            .start();
                                }
                            }
                        }
                    });
            mChoosePicDialog = builder.create();
        }
        if (!GuideActivity.this.isFinishing()) {
            mChoosePicDialog.show();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent(
                Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_TYPE);
        startActivityForResult(intent, RESULT_IMAGE);
    }

    /**
     * 打开相机  兼容7.0手机
     */
    private void openCamera() {
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            photoUri = FileProvider.getUriForFile(GuideActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider",
                    new File(TEMP_IMAGE_PATH));
        } else {
            photoUri = Uri.fromFile(new File(TEMP_IMAGE_PATH));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, RESULT_CAMERA);
    }

//    private void getTempPhotoLocation(String fileName) {
//        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            TEMP_IMAGE_PATH = Util.getAvailableFile(this, fileName).getPath();
//        } else {
//            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQUEST_CODE_STORAGE);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_IMAGE && data != null) {
                // 相册
                LogUtil.d(TAG, "onActivityResult -->RESULT_IMAGE");
                Cursor cursor = this.getContentResolver().query(
                        data.getData(), null, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(
                        cursor.getColumnIndex("_data"));
                cursor.close();
                Intent intent = new Intent(GuideActivity.this, PuzzleActivity.class);
                intent.putExtra("mPicPath", imagePath);
                intent.putExtra("mCount", Util.type2count(mType));
                intent.putExtra("mDifficulty", mDifficulty);
                startActivity(intent);
            } else if (requestCode == RESULT_CAMERA) {
                // 相机
                LogUtil.d(TAG, "onActivityResult -->RESULT_CAMERA");
                Intent intent = new Intent(GuideActivity.this, PuzzleActivity.class);
                intent.putExtra("mPicPath", TEMP_IMAGE_PATH);
                intent.putExtra("mCount", Util.type2count(mType));
                intent.putExtra("mDifficulty", mDifficulty);
                startActivity(intent);
            }
        }
        if (requestCode == REQUEST_CODE_SETTING) {
//            ToastUtil.shortToast(this, "back from settings");
        }
    }

//    private boolean hasPermission(String permission) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
//        }
//        return true;
//    }

//    public void requestPermission(String permission, int requestCode) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            boolean flag=shouldShowRequestPermissionRationale(permission);
//            Log.d(TAG,"shouldShowRequestPermissionRationale: "+permission+"    "+flag);
//            ToastUtil.longToast(this,"shouldShowRequestPermissionRationale: "+permission+"    "+flag);
//            requestPermissions(new String[]{permission}, requestCode);
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (PERMISSION_REQUEST_CODE_STORAGE == requestCode) {
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQUEST_CODE_STORAGE);
//            } else {
//                getTempPhotoLocation(TEMP_PIC_NAME);
//            }
//            return;
//        } else if (PERMISSION_REQUEST_CODE_CAMERA == requestCode) {
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                requestPermission(Manifest.permission.CAMERA, PERMISSION_REQUEST_CODE_CAMERA);
//            } else {
//                openCamera();
//            }
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    @PermissionYes(REQUEST_CODE_PERMISSION_CAMERA)
    private void getCameraPermissionGranted(@NonNull List<String> grantedPermissions) {
//        ToastUtil.shortToast(this, "Camera permission granted");
        openCamera();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_CAMERA)
    private void getCameraPermissionDenied(@NonNull List<String> deniedPermissions) {
//        ToastUtil.shortToast(this, "Camera permission denied");

        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING).show();
        }
    }


    @PermissionYes(REQUEST_CODE_PERMISSION_STORAGE)
    private void getStoragePermissionGranted(@NonNull List<String> grantedPermissions) {
//        ToastUtil.shortToast(this, "storage permission granted");
        openAlbum();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_STORAGE)
    private void getStoragePermissionDenied(@NonNull List<String> deniedPermissions) {
//        ToastUtil.shortToast(this, "storage permission denied");
        if (AndPermission.hasAlwaysDeniedPermission(this,deniedPermissions)){
            AndPermission.defaultSettingDialog(this,REQUEST_CODE_SETTING).show();
        }
    }


}
