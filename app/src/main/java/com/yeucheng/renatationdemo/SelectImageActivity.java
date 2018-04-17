package com.yeucheng.renatationdemo;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yeucheng.renatationdemo.inject.Click;
import com.yeucheng.renatationdemo.inject.FindView;
import com.yeucheng.renatationdemo.inject.ViewUtils;
import com.yeucheng.renatationdemo.permission.PermissionHelper;
import com.yeucheng.renatationdemo.permission.PermissionSuccess;
import com.yeucheng.renatationdemo.wight.DividerDecoration;
import com.yeucheng.renatationdemo.wight.GridDivider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/31.
 */

public class SelectImageActivity extends AppCompatActivity {
    //相机
    public static final String EXTRA_SHOW_CAMERA = "EXTRA_SHOW_CAMERA";
    //选择张数
    public static final String EXTRA_SELECT_COUNT = "EXTRA_SELECT_COUNT";
    //选择的模式
    public static final String EXTRA_SELECT_MODE = "EXTRA_SELECT_MODE";
    //原始的图片路径
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "EXTRA_DEFAULT_SELECTED_LIST";
    //单选或多选
    private int mMode = MODE_MULTI;
    //多选
    public static final int MODE_MULTI = 0x0011;
    //单选
    public static final int MODE_SINGLE = 0x0012;
    private static final int PHOTO_CAMERA = 1002;
    private static final int REQUEST_CAMERA = 0x0022;
    @FindView(R.id.recycler)
    private RecyclerView mRecyclerView;//图片列表
    @FindView(R.id.preview)
    private TextView mPreview;//预览按钮
    @FindView(R.id.confir)
    private TextView mConfir;//确认按钮
    @FindView(R.id.select_num)
    private TextView mSelectNum;//显示图片数目lab
    private File mTempFile;
    //选择图片的张数
    private int mMaxCount;
    //是否显示拍照按钮
    private boolean mShowCamera = true;
    //选择图片的list
    private List<String> mResultList;
    private SelectImageAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_selectimage_layout, null);
        setContentView(view);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        //接收intent数据
        getIntentData();
        //设置适配器
        setAdapter();
        //初始化本地数据
        initImageList();
        //改变显示
        exchangViewShow(mResultList.size());
    }

    private void setAdapter() {
        mAdapter = new SelectImageAdapter(SelectImageActivity.this,
                mMaxCount, R.layout.item_selectimageadapter_layout);
        mRecyclerView.setLayoutManager(new GridLayoutManager(SelectImageActivity.this, 4));
        //设置分割线
//        GridDivider gridDivider = new GridDivider(this,2,Color.parseColor("#FF4081"));
        DividerDecoration gridDivider = new DividerDecoration( Color.parseColor("#FF4081"),2);
        mRecyclerView.addItemDecoration(gridDivider);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnSelectImageListener(new SelectImageListener() {
            @Override
            public void select(int number) {
                exchangViewShow(number);
            }
        });
        //拍照回调
        mAdapter.setTakePhotoListener(new TakePhotoListener() {
            @Override
            public void takePhoto() {
                //动态申请权限
                PermissionHelper.with(SelectImageActivity.this).
                        requestPermission(new String[]{Manifest.permission.CAMERA}).
                        requestCode(PHOTO_CAMERA).
                        request();
            }
        });
    }

    private void exchangViewShow(int numder) {
        if (numder > 0) {
            mPreview.setTextColor(Color.parseColor("#FF4081"));
            mConfir.setTextColor(Color.parseColor("#FF4081"));
            mPreview.setEnabled(true);
            mPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转至删除或者预览页面
                    Intent intent = new Intent(SelectImageActivity.this, PreViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("choicePosition", 0);
                    bundle.putSerializable("choiceList", (ArrayList) mResultList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        } else {
            mPreview.setTextColor(Color.parseColor("#ffffff"));
            mConfir.setTextColor(Color.parseColor("#ffffff"));
            mPreview.setEnabled(false);
            mPreview.setOnClickListener(null);
        }
        mSelectNum.setText(numder + "/" + mMaxCount);
    }


    private static final int LOADER_TYPE = 0x0021;

    private void initImageList() {
        getLoaderManager().initLoader(LOADER_TYPE, null, mLoaderCallBack);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallBack = new LoaderManager.LoaderCallbacks<Cursor>() {
        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID,

        };

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            CursorLoader cursorLoader = new CursorLoader(SelectImageActivity.this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, IMAGE_PROJECTION[4] + ">0" +
                    " AND " + IMAGE_PROJECTION[3] + "=? OR " + IMAGE_PROJECTION[3] + "=?", new
                    String[]{"image/jpeg", "image/png"}, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            //如果有数据变化
            if (cursor != null && cursor.getCount() > 0) {
                List<String> imgaeList = new ArrayList<>();
                //如果要显示拍照的图片,就添加一条空数据.
                if (mShowCamera) {
                    imgaeList.add("");
                }
                //不断遍历
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow
                            (IMAGE_PROJECTION[0]));
                    imgaeList.add(path);
                }
                //显示列表数据
                showListData(imgaeList);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private void showListData(List<String> imgaeList) {
        mAdapter.loadData(imgaeList,mResultList);
    }

    @PermissionSuccess(requestCode = PHOTO_CAMERA)
    public void takePhoto() {
        mTempFile = new File(getInnerSDCardPath(), "imagephoto" + SystemClock
                .currentThreadTimeMillis() + ".jpg");
        if (mTempFile.getParentFile().exists()) {
            mTempFile.getParentFile().mkdirs();
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            //Android7.0 com.yeucheng.renatationdemo替换成自己的包名
            Uri imageUri = FileProvider.getUriForFile(SelectImageActivity.this, "com.yeucheng.renatationdemo.fileprovider", mTempFile);//通过FileProvider创建一个content类型的Uri
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //第一个要把图片添加到集合
        //调用Click方法
        //通知系统本地有图片改变了  下次进来可以找到这张图片
        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_CAMERA){
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTempFile)));
                mResultList.add(mTempFile.getAbsolutePath());
                setResults();
            }
        }
    }

    private void setResults() {
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
//        bundle.putSerializable(EXTRA_RESULT,mResultList);//EXTRA_DEFAULT_SELECTED_LIST
        bundle.putSerializable(EXTRA_DEFAULT_SELECTED_LIST,(ArrayList)mResultList);
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        //关闭当前页面
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.requestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * 获取内置SD卡路径
     *
     * @return
     */
    public String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        mMode = intent.getIntExtra(EXTRA_SELECT_MODE, mMode);
        mMaxCount = intent.getIntExtra(EXTRA_SELECT_COUNT, mMaxCount);
        mShowCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, mShowCamera);
        mResultList = (ArrayList<String>) intent.getSerializableExtra(EXTRA_DEFAULT_SELECTED_LIST);
        if (mResultList == null) {
            mResultList = new ArrayList<>();
        }
    }

    //返回按钮点击事件不需要网络监察
    @Click({R.id.back,R.id.confir})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.confir:
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                bundle.putSerializable(SelectImageActivity.EXTRA_DEFAULT_SELECTED_LIST,
                        (ArrayList)mResultList);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }
}
