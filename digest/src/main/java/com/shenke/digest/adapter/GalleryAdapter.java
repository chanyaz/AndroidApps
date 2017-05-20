package com.shenke.digest.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shenke.digest.R;
import com.shenke.digest.entity.NewsDigest;
import com.shenke.digest.entity.SlideItem;

import java.util.ArrayList;
import java.util.List;


public class GalleryAdapter extends BaseRecyclerViewAdapter<NewsDigest.NewsItem.SlideShow> {
    public Context mContext;
    public static NewsDigest.NewsItem.SlideShow mSlideShow;
    public NewsDigest.NewsItem.SlideShow slideShow;
    private List<SlideItem> data = new ArrayList<SlideItem>();
    public SlideItem slideItem;

    public GalleryAdapter(Context mContext, NewsDigest.NewsItem.SlideShow mSlideShow) {
        this.mContext = mContext;
        this.mSlideShow = mSlideShow;
    }

    public GalleryAdapter(Context mContext) {
        this.mContext = mContext;

    }

    @Override
    public RecyclerView.ViewHolder createHeaderViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public RecyclerView.ViewHolder createFooterViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        mContext = parent.getContext();
        return new GalleryViewHolder(view);

    }

    @Override
    public void bindItemView(RecyclerView.ViewHolder holder, final int position) {
        final GalleryViewHolder galleryViewHolder = (GalleryViewHolder) holder;
        slideShow = mSlideShow;
        if (slideShow != null) {
            if (slideShow.photos.total != 0) {
                galleryViewHolder.url = slideShow.photos.elements.get(position).images.originalUrl;
                Glide.with(galleryViewHolder.itemView.getContext())
                        .load(galleryViewHolder.url)
                        .crossFade()
                        .centerCrop()
                        .into(galleryViewHolder.imageView);
            }
        }

        galleryViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onItemClickListenerList.isEmpty()) {
                    for (OnItemClickListener onItemClickListener : onItemClickListenerList) {
                        onItemClickListener.onItemClick(galleryViewHolder, position);
                    }

                }
            }
        });
    }
@Override
public int getItemCount(){
    if(mSlideShow.photos.elements.size() == 0 || mSlideShow.photos.elements.size() == 1){
        return 0;
    }else{
        return mSlideShow.photos.elements.size();
    }
}
    @Override
    public void bindHeaderView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void bindFooterView(RecyclerView.ViewHolder holder, int position) {

    }

    public void addItem(SlideItem slideItem) {
        this.slideItem = slideItem;
    }


    public static class GalleryViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;
        final View itemView;
        public String url;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.imageView = (ImageView) itemView.findViewById(R.id.photo);
        }
    }

}
