package com.yeucheng.renatationdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yeucheng.renatationdemo.wight.SquareImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/31.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private List<String> mImageList;
    private Context mContext;
    private int mMaxNum;

    public MainAdapter(Context context, int maxNum) {
        super();
        this.mContext = context;
        this.mMaxNum = maxNum;
        if (null == mImageList) mImageList = new ArrayList<>();
    }
    public void nodfiyData(List<String> list){
        if(list!=null)
        {
            for (String str:list
                 ) {
                Log.d("MainAdapter",str);
            }
            this.mImageList.clear();
            this.mImageList.addAll(list);
        }
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_mainadapter_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position == mImageList.size()) {
            holder.mImgDelete.setVisibility(View.GONE);
            Glide.with(mContext).load(R.drawable.icon_addpic_unfocused).centerCrop().into(holder
                    .mImageView);
            if (position == mMaxNum) {
                holder.mImageView.setVisibility(View.GONE);
            }
        } else {
            holder.mImgDelete.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(mImageList.get(position)).centerCrop().into(holder
                    .mImageView);
        }
        //条目点击事件
        if (mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == mMaxNum) return;
                    if (mImageList.size() == position) {
                        mItemClickListener.onItemClick("", position);
                    } else {
                        mItemClickListener.onItemClick(mImageList.get(position), position);
                    }
                }
            });
        }
        if (mItemDeleteListener != null) {
            holder.mImgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemDeleteListener.onDeleteClick(mImageList.get(position), position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mImageList.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void loadData(List<String> imageList) {
        this.mImageList = imageList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        SquareImageView mImageView;
        ImageView mImgDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image);
            mImgDelete = itemView.findViewById(R.id.delete);
        }
    }

    //使用接口回调点击事件
    private ItemClickListener mItemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }
    public void setOnDeleteClickListener(DeleteListener itemDeleteListener){
        this.mItemDeleteListener=itemDeleteListener;
    }
    //使用接口回调点击事件
    private DeleteListener mItemDeleteListener;

}
