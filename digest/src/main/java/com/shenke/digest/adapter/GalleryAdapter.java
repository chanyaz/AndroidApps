package com.shenke.digest.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shenke.digest.R;
import com.shenke.digest.entity.NewsDigest;


public class GalleryAdapter extends BaseRecyclerViewAdapter<NewsDigest.NewsItem.SlideShow> {
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
        return new GalleryViewHolder(view);
    }

    @Override
    public void bindItemView(RecyclerView.ViewHolder holder, final int position) {
        final GalleryViewHolder galleryViewHolder = (GalleryViewHolder) holder;
      //  galleryViewHolder.url = NewsDigest.NewsItem.SlideShow.Photos.Element.Images.Resolution.;
        Glide.with(galleryViewHolder.itemView.getContext())
                .load(galleryViewHolder.url)
                .crossFade()
                .centerCrop()
                .into(galleryViewHolder.imageView);
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
    public void bindHeaderView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void bindFooterView(RecyclerView.ViewHolder holder, int position) {

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
