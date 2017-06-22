package com.shenke.digest.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shenke.digest.R;
import com.shenke.digest.core.NewsListActivity;
import com.shenke.digest.db.DigestStatus;
import com.shenke.digest.dialog.MoreDigestDialog;
import com.shenke.digest.entity.NewsDigest;
import com.shenke.digest.util.DateUtil;
import com.shenke.digest.view.CircleLayout;
import com.shenke.digest.view.CircularRevealView;
import com.shenke.digest.view.DonutProgress;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.shenke.digest.dialog.MoreDigestDialog.SECTION_EVENING;
import static com.shenke.digest.dialog.MoreDigestDialog.SECTION_MORNING;


public class NewsAdapter extends BaseRecyclerViewAdapter<NewsDigest.NewsItem> {

    private int editon;
    private int section;
    private String date;
    private boolean allChecked;
    private boolean isChecked;
    public Context mContext;
    public static NewsDigest mNewsDigest;
    public NewsDigest.NewsItem newsItem;
    private OnItemClickListener onItemClickListener;
    private FragmentManager fm;
    private final String TAG = "NewsAdapter";
    public static Bitmap bitmap;
    public static  String newssource;
    public List<DigestStatus> digests = NewsListActivity.mgr.query();
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public NewsAdapter(Context mContext, NewsDigest mNewsDigest ) {
        this.mContext = mContext;
        this.mNewsDigest = mNewsDigest;
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
        newsItem = mNewsDigest.items.get(position);
        if (newsItem != null) {

            if (position == 0) {
                holder.imgArea.setVisibility(View.VISIBLE);
                holder.img.setVisibility(View.VISIBLE);
                holder.placeHolder.setVisibility(View.VISIBLE);
                holder.section.setVisibility(View.VISIBLE);
                holder.date.setVisibility(View.VISIBLE);
                holder.sectionArea.setVisibility(View.VISIBLE);
                Glide.with((holder.itemView.getContext())).load(mNewsDigest.poster.images.originalUrl).crossFade() .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.img);
                String ed;
                if (mNewsDigest.regionEdition.equals("AA")) {
                    ed = "Intl.";
                } else if (mNewsDigest.regionEdition.equals("CA")) {
                    ed = "Canada";
                } else if (mNewsDigest.regionEdition.equals("GB")) {
                    ed = "UK";
                } else {
                    ed = mNewsDigest.regionEdition;
                }
                holder.date.setText(DateUtil.MonthFormat(mNewsDigest.date.substring(5, 7).trim()) + " " + mNewsDigest.date.substring(8, 10).trim());
                if (mNewsDigest.edition == 1) {
                    section = SECTION_EVENING;
                } else {
                    section = SECTION_MORNING;
                }
                try {
                    String str = DateUtil.DayforWeek(mNewsDigest.date);

                    holder.section.setText(DateUtil.DayforWeek(mNewsDigest.date) + (section == SECTION_MORNING ? " morning" : " evening")
                            + " | " + ed);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                holder.imgArea.setVisibility(View.GONE);
                holder.img.setVisibility(View.GONE);
                holder.placeHolder.setVisibility(View.GONE);
                holder.section.setVisibility(View.GONE);
                holder.date.setVisibility(View.GONE);
                holder.sectionArea.setVisibility(View.GONE);

            }

            /**
             * tap to see moredigest
             */
            holder.sectionArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = v.getRootView();
                    view.setDrawingCacheEnabled(true);
                    view.buildDrawingCache();
                    bitmap = view.getDrawingCache();
                    FragmentActivity activity = (FragmentActivity) (mContext);
                    fm = activity.getSupportFragmentManager();
                    MoreDigestDialog mMoreDigestDialog = new MoreDigestDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("fragment", TAG);
                    mMoreDigestDialog.setArguments(bundle);
                    mMoreDigestDialog.show(fm, "mMoreDigestDialog");
                }
            });
            //index
            holder.donutProgress.setText("" + (position + 1));
            //label "Politics"
            Typeface typeFaceLabel = Typeface.createFromAsset(holder.view.getContext().getAssets(), "fonts/Roboto-Bold.ttf");
            holder.label.setTypeface(typeFaceLabel);
            holder.label.setText(newsItem.categories.get(0).name);

            //title
            Typeface typeFaceTitle = Typeface.createFromAsset(holder.view.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            holder.title.setTypeface(typeFaceTitle);
            holder.title.setText("" + newsItem.title);


            //press "Yahoo News"
            Typeface typeFacePress = Typeface.createFromAsset(holder.view.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            holder.sources.setTypeface(typeFacePress);
            if (newsItem.sources.size() == 0) {
                holder.sources.setVisibility(View.INVISIBLE);
            } else if (newsItem.sources.size() == 1) {
                holder.sources.setText(newsItem.sources.get(0).publisher);
            } else if (newsItem.sources.size() >= 2) {
                ArrayList<String> publishers = new ArrayList<String>();
                for (int i = 0; i < newsItem.sources.size(); i++) {
                    publishers.add(newsItem.sources.get(i).publisher);
                }
                //  不改变顺序去重
                Set set = new HashSet();
                List newList = new ArrayList();
                for (Iterator iter = publishers.iterator(); iter.hasNext(); )
                {
                    Object element = iter.next();
                    if (set.add(element)) newList.add(element);
                }
                publishers.clear();
                publishers.addAll(newList);
                if(publishers.size() == 1){
                    holder.sources.setText(publishers.get(0));
                }else if(publishers.size() == 2){
                    holder.sources.setText(publishers.get(0) + ","+ publishers.get(1));
                }else if(publishers.size() > 2){
                    holder.sources.setText(publishers.get(0) + ","+ publishers.get(1) + " + "+(publishers.size() - 2)+" more" );
                }
            }
            newssource = holder.sources.getText().toString();
            final ViewHolder holder1 = holder;
            final int position1 = position;
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(holder1, position1);

                    }

                }
            });


            int stateColor = android.graphics.Color.parseColor(newsItem.colors.get(0).hexcode);
            //index
            //DigestStatus digestStatus = NewsListActivity.mgr.queryItemStatus(newsItem.uuid);
            //isChecked = digestStatus.isChecked>0;
           for(int i =0;i<digests.size();i++){
               if(newsItem.uuid.equals(digests.get(i).uuid)){
                   isChecked = digests.get(i).isChecked>0;
                   if (isChecked) {
                       holder.donutProgress.setFinishedStrokeColor(stateColor);
                       holder.donutProgress.setUnfinishedStrokeColor(stateColor);
                       holder.donutProgress.setInnerBackgroundColor(stateColor);
                       holder.donutProgress.setTextColor(android.graphics.Color.WHITE);

                   } else {
                       holder.donutProgress.setFinishedStrokeColor(stateColor);
                       holder.donutProgress.setUnfinishedStrokeColor(stateColor);
                       holder.donutProgress.setTextColor(stateColor);
                       holder.donutProgress.setInnerBackgroundColor(android.graphics.Color.TRANSPARENT);
                   }
               }
           }


            //label
            holder.label.setTextColor(stateColor);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                StateListDrawable stateListDrawable = new StateListDrawable();
                stateListDrawable.addState(new int[]{android.R.attr.state_empty},
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                int cl = android.graphics.Color.parseColor("#55" + newsItem.colors.get(0).hexcode.substring(1));
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed},
                        new ColorDrawable(cl));
                holder.view.setBackground(stateListDrawable);
            }
            if (newsItem.order != null) {
                if (!(newsItem.wikis.size()>0)) {
                    holder.images.findViewById(R.id.wiki).setVisibility(View.GONE);
                }
                if (!(newsItem.locations.size()>0)) {
                    holder.images.findViewById(R.id.map).setVisibility(View.GONE);
                }
                if (!(newsItem.videos.size()>0)) {
                    holder.images.findViewById(R.id.video).setVisibility(View.GONE);
                }
                if (!(newsItem.slideshow.photos.elements.size()>0)) {
                    holder.images.findViewById(R.id.images).setVisibility(View.GONE);
                }
                if (!(newsItem.statDetail.size()>0)) {
                    holder.images.findViewById(R.id.stats).setVisibility(View.GONE);
                }
                if (!(newsItem.stats.size()>0)) {
                    holder.images.findViewById(R.id.diagram).setVisibility(View.GONE);
                }
                if (!(newsItem.tweetKeywords.size()>0)) {
                    holder.images.findViewById(R.id.twitter).setVisibility(View.GONE);
                }
            }


        }
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
       /* if (mNewsDigest.edition == 1) {
            holder.ll_newslist_footer.setBackgroundResource(R.color.black);
            holder.urd.setTextColor(Color.BLACK);
            holder.read.setTextColor(Color.BLACK);
            holder.bigTitle.setTextColor(Color.BLACK);
            holder.textView.setTextColor(Color.BLACK);
            holder.foot_view.setBackgroundResource(R.color.countdown_text_evening_light);
            holder.toggleButton.setTextColor(Color.WHITE);
            Drawable downDrawable = holder.toggleButton.getContext().getResources().getDrawable(R.mipmap.extranews_arrow_down_w);
            downDrawable.setBounds(0, 0, downDrawable.getMinimumWidth(), downDrawable.getMinimumHeight());
            holder.toggleButton.setCompoundDrawables(null, null, null, downDrawable);
        }*/
        holder.bigTitle.setTypeface(typeface);
        holder.smallTitle.setTypeface(typeface);
        holder.urd.setTypeface(ty);
        holder.textView.setTypeface(ty);
        // final int count = getAllItems().size();
        final int count = mNewsDigest.items.size();
        holder.textView.setText(holder.circleLayout.getActiveCount() + " of " + count);

        int index = 1;
        if (!allChecked) {
            for (int i = 0; i < count; i++) {
                int activeColor = android.graphics.Color.parseColor(mNewsDigest.items.get(i).colors.get(0).hexcode);
                holder.circleLayout.addItem("" + index, activeColor);
                index++;
            }
            for (int i = 0; i < count; i++) {
                if (digests.get(i).isChecked>0) {
                    holder.circleLayout.activeItem(i);
                }
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
                        activationItem(index);
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
        DigestStatus digestStatus =new DigestStatus();
        digestStatus.uuid = mNewsDigest.items.get(index).uuid;
        digestStatus.isChecked = 1;
        NewsListActivity.mgr.updateStatus(digestStatus);
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
        final ImageView triangle_background;
        final RelativeLayout rl_news_list;
        //public NewsDigest.NewsItem newsItem;

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
            triangle_background = (ImageView) itemView.findViewById(R.id.triangle_background);
            rl_news_list = (RelativeLayout) itemView.findViewById(R.id.rl_news_list);
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
        final LinearLayout ll_newslist_footer;
        final View foot_view;

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
            this.ll_newslist_footer = (LinearLayout) itemView.findViewById(R.id.ll_newslist_footer);
            this.foot_view = itemView.findViewById(R.id.footer_view);
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
