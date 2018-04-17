package com.yeucheng.renatationdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.yeucheng.renatationdemo.inject.Click;
import com.yeucheng.renatationdemo.inject.FindView;
import com.yeucheng.renatationdemo.inject.ViewUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PreViewActivity extends AppCompatActivity {
    public static final String INTENT_IMAGESIZE = "imagesize";
    public static final String INTENT_POSITION = "position";
    @FindView(R.id.viewpager)
    private ViewPager mViewPager;
    private List<String> choiceList;
    public ImageSize imageSize;
    @FindView(R.id.guideGroup)
    private LinearLayout guideGroup;
    private int startPos;
    private List<View> guideViewList = new ArrayList<View>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_layout);
        ViewUtils.inject(this);
        getIntentData();
        ImageAdapter mAdapter = new ImageAdapter(this);
        mAdapter.setDatas(choiceList);
        mAdapter.setImageSize(imageSize);
        mViewPager.setAdapter(mAdapter);
        mAdapter.addOnPicClickListener(new ImageAdapter.OnPicClickCallback() {
            @Override
            public void onClicked() {
                PreViewActivity.this.finish();
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < guideViewList.size(); i++) {
                    guideViewList.get(i).setSelected(i == position ? true : false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(startPos);

        addGuideView(guideGroup, startPos, choiceList);
    }

    private void addGuideView(LinearLayout guideGroup, int startPos, List<String> choiceList) {
        if (choiceList != null && choiceList.size() > 0) {
            guideViewList.clear();
            for (int i = 0; i < choiceList.size(); i++) {
                View view = new View(this);
                view.setBackgroundResource(R.drawable.selector_guide_bg);
                view.setSelected(i == startPos ? true : false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.gudieview_width),
                        getResources().getDimensionPixelSize(R.dimen.gudieview_heigh));
                layoutParams.setMargins(10, 0, 0, 0);
                guideGroup.addView(view, layoutParams);
                guideViewList.add(view);
            }
        }
    }

    private void getIntentData() {
        startPos = getIntent().getIntExtra(INTENT_POSITION, 0);
        choiceList = (List<String>) getIntent().getSerializableExtra("choiceList");
        if(choiceList==null){
            choiceList=new ArrayList<>();
        }
        imageSize = (ImageSize) getIntent().getSerializableExtra(INTENT_IMAGESIZE);
    }

    @Click({R.id.back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
    private static class ImageAdapter extends PagerAdapter {

        private List<String> datas = new ArrayList<String>();
        private LayoutInflater inflater;
        private Context context;
        private ImageSize imageSize;
        private ImageView smallImageView = null;

        public void setDatas(List<String> datas) {
            if (datas != null)
                this.datas = datas;
        }

        public void setImageSize(ImageSize imageSize) {
            this.imageSize = imageSize;
        }

        public ImageAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (datas == null) return 0;
            return datas.size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = new FrameLayout(context);
            ImageView imageView= new ImageView(context);
            ((FrameLayout)view).addView(imageView);
            if (view != null) {
                if (imageSize != null) {
                    //预览imageView
                    smallImageView = new ImageView(context);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageSize.getWidth(), imageSize.getHeight());
                    layoutParams.gravity = Gravity.CENTER;
                    smallImageView.setLayoutParams(layoutParams);
                    smallImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ((FrameLayout) view).addView(smallImageView);
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onClicked();
                    }
                });
                //loading
//                final ProgressBar loading = new ProgressBar(context);
//                FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT);
//                loadingLayoutParams.gravity = Gravity.CENTER;
//                loading.setLayoutParams(loadingLayoutParams);
//                ((FrameLayout) view).addView(loading);

                final String imgurl = datas.get(position);

                Glide.with(context).load(imgurl).fitCenter().into(imageView);

                container.addView(view, 0);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        public interface OnPicClickCallback{
            void onClicked();
        }
        private  OnPicClickCallback mCallback;
        public void addOnPicClickListener(OnPicClickCallback callback){
            this.mCallback = callback;
        }
    }
    public static class ImageSize implements Serializable {

        private int width;
        private int height;

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }
    }
}
