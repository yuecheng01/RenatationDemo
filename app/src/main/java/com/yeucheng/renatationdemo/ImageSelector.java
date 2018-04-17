package com.yeucheng.renatationdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/31.
 */

public class ImageSelector {
    //选择图片张数
    private int mMaxCount = 9;
    //选择图片的模式
    private int mMode = SelectImageActivity.MODE_MULTI;
    //是否显示拍照的相机
    private boolean mShowCamera = true;
    //原始的图片
    private List<String> mOriginData;


    private ImageSelector() {
    }

    public static ImageSelector create() {
        return new ImageSelector();
    }

    /**
     * 单选模式
     *
     * @return
     */
    public ImageSelector single() {
        mMode = SelectImageActivity.MODE_SINGLE;
        return this;
    }

    /**
     * 多选模式
     *
     * @return
     */
    public ImageSelector multi() {
        mMode = SelectImageActivity.MODE_MULTI;
        return this;
    }

    /**
     * 设置可以选择的图片张数
     *
     * @param count
     * @return
     */
    public ImageSelector count(int count) {
        mMaxCount = count;
        return this;
    }

    /**
     * 设置是否显示相机
     *
     * @param showCamera
     * @return
     */
    public ImageSelector showCamera(boolean showCamera) {
        mShowCamera = showCamera;
        return this;
    }

    /**
     * 已经选择好的照片
     *
     * @param originData
     * @return
     */
    public ImageSelector origin(List<String> originData) {
        mOriginData = originData;
        return this;
    }

    public void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SelectImageActivity.class);
        addParmasByIntent(intent);
        activity.startActivityForResult(intent, requestCode);
    }

    private void addParmasByIntent(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putInt(SelectImageActivity.EXTRA_SELECT_COUNT, mMaxCount);
        bundle.putInt(SelectImageActivity.EXTRA_SELECT_MODE, mMode);
        bundle.putBoolean(SelectImageActivity.EXTRA_SHOW_CAMERA, mShowCamera);
        if (mOriginData != null && mMode == SelectImageActivity.MODE_MULTI) {
            bundle.putSerializable(SelectImageActivity.EXTRA_DEFAULT_SELECTED_LIST, (ArrayList)mOriginData);
        }
        intent.putExtras(bundle);
    }
}
