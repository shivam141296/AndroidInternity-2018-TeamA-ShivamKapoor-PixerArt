package com.bridge.android.pixerart.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bridge.android.pixerart.Model.BucketItem;
import com.bridge.android.pixerart.utils.PixerActivity;
import com.bridge.android.pixerart.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class PicsBucketAdapter extends RecyclerView.Adapter<PicsBucketAdapter.ViewHolder>  {
    private Context mContext;
    private List<BucketItem> mList;

    public PicsBucketAdapter(FragmentActivity activity, List<BucketItem> bucketItems) {
        this.mContext = activity;
        this.mList = bucketItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.bucket_item);
        }
    }

    @Override
    public PicsBucketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bucketitems, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(PicsBucketAdapter.ViewHolder holder, int position) {
        final BucketItem item = mList.get(position);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PixerActivity.class);
                intent.putExtra("item", item);
                mContext.startActivity(intent);
            }
        });

        Glide.with(mContext)
                .load(item.getUrl())
                .thumbnail(0.5f)
                .into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addAll(List<BucketItem> newList) {
        mList.addAll(newList);
    }

    public void add(BucketItem item) {
        mList.add(item);
    }

    public void clear() {
        mList.clear();
    }
}
