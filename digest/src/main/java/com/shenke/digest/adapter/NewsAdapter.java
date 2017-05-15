package com.shenke.digest.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shenke.digest.R;
import com.shenke.digest.entity.NewsDigest;
import com.shenke.digest.view.CircleLayout;
import com.shenke.digest.view.CircularRevealView;
import com.shenke.digest.view.DonutProgress;


public class NewsAdapter extends BaseRecyclerViewAdapter<NewsDigest.NewsItem> {

    private int editon;
    private int section;
    private String date;
    private boolean allChecked;
    public Context mContext;
    private OnItemClickListener onItemClickListener;
    private FragmentManager fm;
    private final String TAG = "NewsAdapter";
    public static Bitmap bitmap;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public NewsAdapter(Context mContext) {
        this.mContext = mContext;

    }

    public NewsAdapter(Context mContext, FragmentManager fm) {
        this.mContext = mContext;
        this.fm = fm;

    }

    @Override
    public RecyclerView.ViewHolder createHeaderViewHolder(ViewGroup parent, int viewType) {
        return new HeaderViewHolder(getHeaderView());
    }

    @Override
    public RecyclerView.ViewHolder createFooterViewHolder(ViewGroup parent, int viewType) {
        return new FooterViewHolder(getFooterView());
    }

    @Override
    public RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void bindItemView(RecyclerView.ViewHolder srcHolder1, final int position) {
        ViewHolder holder = (ViewHolder) srcHolder1;

    }

    @Override
    public void bindHeaderView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void bindFooterView(RecyclerView.ViewHolder footViewHolder, int position) {

        final FooterViewHolder holder = (FooterViewHolder) footViewHolder;
        final int readColor = holder.revealView.getContext().getResources().getColor(R.color.read_color);
        Typeface typeface = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface ty = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        holder.bigTitle.setTypeface(typeface);
        holder.smallTitle.setTypeface(typeface);
        holder.urd.setTypeface(ty);
        holder.textView.setTypeface(ty);
        final int count = getAllItems().size();

        holder.textView.setText(holder.circleLayout.getActiveCount() + " of " + count);

        int index = 1;
        if (!allChecked) {
            for (int i = 0; i < count; i++) {
               // int activeColor = getItem(i).color;
              //  holder.circleLayout.addItem("" + index, activeColor);
                index++;
            }

            for (int i = 0; i < count; i++) {
              /*  if (getItem(i).isChecked()) {
                    holder.circleLayout.activeItem(i);
                }*/
            }
        }
        if (holder.circleLayout.getActiveCount() == count) {
            allChecked = true;
            holder.toggleButton.setTextColor(android.graphics.Color.WHITE);
            holder.readIndicator.setVisibility(View.GONE);
            holder.circleLayout.setVisibility(View.GONE);
            holder.toggleButton.setTextColor(android.graphics.Color.WHITE);
            Drawable bottom = holder.readIndicator.getContext().getResources().getDrawable(R.mipmap.extranews_arrow_down_w);
            holder.toggleButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, bottom);
            holder.doYouKnow.setVisibility(View.VISIBLE);
            /**
             * TODO 由Did you Konw...? 跳转ExtraFrtagment
             */
            holder.doYouKnow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.circleLayout.setClickable(false);
            holder.circleLayout.setEnabled(false);
            holder.revealView.setBackgroundColor(readColor);

        } else {

            holder.readIndicator.setVisibility(View.VISIBLE);
            holder.doYouKnow.setVisibility(View.INVISIBLE);
            holder.circleLayout.setOnChildViewClickListener(new CircleLayout.OnChildViewClickListener() {
                @Override
                public void onChildViewClick(View childView, int index) {
                    holder.circleLayout.activeItem(index);
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(null, index);

                    }
                    holder.textView.setText(holder.circleLayout.getActiveCount() + " of " + count);
                }
            });
            holder.circleLayout.setCircleLayoutAnimationListener(new CircleLayout.CircleLayoutAnimationListener() {
                @Override
                public void onAnimationMarqueeStart() {

                }

                @Override
                public void onAnimationMarqueeEnd() {

                }

                @Override
                public void onAnimationShrinkStart(Animator animation) {

                }

                @Override
                public void onAnimationShrinkEnd(Animator animation) {
                    //final int cl = android.graphics.Color.parseColor("#00AA00");
                    holder.readIndicator.setVisibility(View.GONE);
                    holder.circleLayout.setVisibility(View.GONE);
                    holder.circleLayout.setClickable(false);
                    holder.circleLayout.setEnabled(false);
                    holder.revealView.reveal(holder.revealView.getMeasuredWidth() / 2 - 10, holder.revealView.getMeasuredHeight() / 2 - 10, readColor, 20, 500, new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);

                            holder.toggleButton.setTextColor(android.graphics.Color.WHITE);
                            Drawable bottom = holder.readIndicator.getContext().getResources().getDrawable(R.mipmap.extranews_arrow_down_w);
                            holder.toggleButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, bottom);
                            holder.doYouKnow.setVisibility(View.VISIBLE);
                            allChecked = true;
                        }
                    });
                }
            });
        }


    }

    public void activationItem(int index) {
       // getItem(index).setChecked(true);
        notifyDataSetChanged();
    }


    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final View itemView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final View view;
        final View imgArea;
        final View placeHolder;
        final DonutProgress donutProgress;
        final TextView label;
        final TextView title;
        final ImageView img;
        final TextView sources;
        final LinearLayout images;
        final View sectionArea;
        final TextView date;
        final TextView section;
        //public DetailItem itemRealm;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imgArea = itemView.findViewById(R.id.imgArea);
            placeHolder = itemView.findViewById(R.id.placeHolder);
            img = (ImageView) itemView.findViewById(R.id.img);
            donutProgress = (DonutProgress) itemView.findViewById(R.id.index);
            label = (TextView) itemView.findViewById(R.id.label);
            title = (TextView) itemView.findViewById(R.id.title);
            sources = (TextView) itemView.findViewById(R.id.sources);
            images = (LinearLayout) itemView.findViewById(R.id.order);
            sectionArea = itemView.findViewById(R.id.sectionArea);
            date = (TextView) itemView.findViewById(R.id.date);
            section = (TextView) itemView.findViewById(R.id.section);
        }

    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public final View itemView;
        final CircleLayout circleLayout;
        final CircularRevealView revealView;
        final TextView textView;
        final TextView bigTitle;
        final TextView smallTitle;
        final View readIndicator;
        final View doYouKnow;
        final TextView toggleButton;
        final TextView urd;

        public FooterViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.circleLayout = (CircleLayout) itemView.findViewById(R.id.circleLayout);
            this.revealView = (CircularRevealView) itemView.findViewById(R.id.revalView);
            this.textView = (TextView) itemView.findViewById(R.id.read);
            this.bigTitle = (TextView) itemView.findViewById(R.id.bigTitle);
            this.smallTitle = (TextView) itemView.findViewById(R.id.smallTitle);
            this.readIndicator = itemView.findViewById(R.id.readIndicator);
            this.doYouKnow = itemView.findViewById(R.id.know);
            this.toggleButton = (TextView) itemView.findViewById(R.id.toggleButton);
            this.urd = (TextView) itemView.findViewById(R.id.uread);

        }
    }


    public interface OnItemClickListener {
        void onItemClick(ViewHolder holder, int position);
    }

    /**
     * 更新NewsListDigest 页面的date 和section，
     *
     * @param edition
     * @param section
     * @param date
     */
    public void resetArea(int edition, int section, String date) {
        this.editon = edition;
        this.section = section;
        this.date = date;
        notifyDataSetChanged();
    }

    public boolean isAllChecked() {
        return allChecked;
    }
}
