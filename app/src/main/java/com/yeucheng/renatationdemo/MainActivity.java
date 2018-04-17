package com.yeucheng.renatationdemo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.yeucheng.renatationdemo.inject.Click;
import com.yeucheng.renatationdemo.inject.FindView;
import com.yeucheng.renatationdemo.inject.ViewUtils;
import com.yeucheng.renatationdemo.permission.PermissionHelper;
import com.yeucheng.renatationdemo.permission.PermissionSuccess;
import com.yeucheng.renatationdemo.wight.DividerDecoration;
import com.yeucheng.renatationdemo.wight.GridDivider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @FindView(R.id.recycler)
    private RecyclerView mRecyclerView;
    private MainAdapter mAdapter;
    private int mMaxChoice = 20;
    private final int SELECT_IMAGE_REQUEST = 0x0011;
    private List<String> mResultList = new ArrayList<>();
    private static final int PHOTO_CAMERA = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        mAdapter = new MainAdapter(this, mMaxChoice);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
        mAdapter.setHasStableIds(true);
        //设置分割线
        DividerDecoration gridDivider = new DividerDecoration( Color.parseColor("#FF4081"),2);
        mRecyclerView.addItemDecoration(gridDivider);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.loadData(mResultList);
        mAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Object obj, int position) {

                if (mResultList.size() == position) {
                    //动态申请权限
                    PermissionHelper.requestPermisson(MainActivity.this, PHOTO_CAMERA, new
                            String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
                } else {
                    //跳转至删除或者预览页面
                    Intent intent = new Intent(MainActivity.this, PreViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("choicePosition", position);
                    bundle.putSerializable("choiceList", (ArrayList) mResultList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        mAdapter.setOnDeleteClickListener(new DeleteListener() {
            @Override
            public void onDeleteClick(Object obj, int position) {
                removeList(position);
            }
        });

    }

    private void removeList(int position) {
        mResultList.remove(position);
        mAdapter.nodfiyData(mResultList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE_REQUEST && data != null) {
                mResultList = (ArrayList<String>) data.getSerializableExtra(SelectImageActivity.EXTRA_DEFAULT_SELECTED_LIST);
                Log.d("MainActivity", mResultList.size() + "");
                mAdapter.nodfiyData(mResultList);
            }
        }
    }

    @PermissionSuccess(requestCode = PHOTO_CAMERA)
    private void toSelectImageActivity() {
//        Toast.makeText(this, "Priview", Toast.LENGTH_SHORT).show();
        ImageSelector
                .create()
                .count(mMaxChoice)
                .multi()
                .showCamera(true)
                .origin(mResultList)
                .start(MainActivity.this, SELECT_IMAGE_REQUEST);
    }

    //回退按钮点击不需要检查网络状态
    @Click({R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.requestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
