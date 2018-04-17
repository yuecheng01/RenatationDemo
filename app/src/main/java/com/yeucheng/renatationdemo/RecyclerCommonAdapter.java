package com.yeucheng.renatationdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/31.
 */

public abstract class RecyclerCommonAdapter<DATA> extends RecyclerView.Adapter<ViewHolder> {

    //条目布局
    private int mLayoutId;
    private List<DATA> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private MulitiType mTypeSupport;

    public RecyclerCommonAdapter(Context context, int layoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mLayoutId = layoutId;
        /*if (this.mData != null) {
            this.mData.addAll(data);
            return;
        }
        this.mData = new ArrayList<>();*/
        this.mData = new ArrayList<DATA>();
    }

    public void loadData( List<DATA> data){
        if(mData!=null)
        {
            this.mData.addAll(data);
            notifyDataSetChanged();
        }
    }
    //需要多布局
    public RecyclerCommonAdapter(Context context, MulitiType typeSupport) {
        this(context, -1);
        this.mTypeSupport = typeSupport;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mTypeSupport != null) {
            //需要多布局
            mLayoutId = viewType;
        }
        View view = mInflater.inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        //多布局问题
        if (mTypeSupport != null) {
            return mTypeSupport.getLayoutId(mData.get(position));
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mData.size() <= 0) {
            return;
        }
        //绑定数据
        bindData(holder, mData.get(position), position);
        //条目点击事件
        if (mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mData.size() <= 0) {
                        mItemClickListener.onItemClick("", position);
                    } else {
                        mItemClickListener.onItemClick(mData.get(position), position);
                    }
                }
            });
        }
        if (mItemLoogClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return mItemLoogClickListener.onItemLongClick(holder, mData.get(position), position);
                }
            });
        }
    }

    /**
     * 将必要参数传递出去
     *
     * @param holder
     * @param data
     * @param position
     */
    protected abstract void bindData(ViewHolder holder, DATA data, int position);

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //使用接口回调点击事件
    private ItemClickListener mItemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    //使用接口回调点击事件
    private ItemLongClickListener mItemLoogClickListener;

    public void setOnItemLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mItemLoogClickListener = itemLongClickListener;
    }
}
