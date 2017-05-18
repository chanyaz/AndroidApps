package com.shenke.digest.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shenke.digest.R;
import com.shenke.digest.adapter.BaseRecyclerViewAdapter;
import com.shenke.digest.core.ExtraNewsListActivity;
import com.shenke.digest.core.NewsDetailActivity;
import com.shenke.digest.entity.NewsDigest;
import com.shenke.digest.util.LogUtil;

import java.util.ArrayList;

import rx.Subscription;




public class ExtraNewsListFragment extends BaseFragment {
    private final String TAG = "ExtraNewsListFragment";

    private Subscription subscription;
    private RecyclerView recyclerView;
    private ExtraNewsAdapter adapter;
    private boolean allChecked;
    public static NewsDigest mNewsDigest;
    private ArrayList<NewsDigest.NewsItem> list = new ArrayList<NewsDigest.NewsItem>();
    public NewsDigest.NewsItem newsItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  subscription = load();
        allChecked = getArguments().getBoolean(ExtraNewsListActivity.ALL_CHECKED, false);
        mNewsDigest = (NewsDigest) getArguments().getSerializable("ExtraNewsDigestData");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_extra_news_list;
    }

    @Override
    protected void initView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);


        TextView back = (TextView) rootView.findViewById(R.id.back);
        if (allChecked) {
            back.setTextColor(android.graphics.Color.WHITE);
            back.setBackgroundColor(getResources().getColor(R.color.read_color));
            Drawable bottom = getResources().getDrawable(R.mipmap.extranews_arrow_up_w);
            back.setCompoundDrawablesWithIntrinsicBounds(null, null, null, bottom);
        } else {
            back.setTextColor(android.graphics.Color.BLACK);
            back.setBackgroundColor(android.graphics.Color.WHITE);
            Drawable bottom = getResources().getDrawable(R.mipmap.extranews_arrow_up);
            back.setCompoundDrawablesWithIntrinsicBounds(null, null, null, bottom);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        View headerView = LayoutInflater.from(rootView.getContext()).inflate(R.layout.extra_news_header_view, recyclerView, false);
        TextView textView = (TextView) headerView.findViewById(R.id.extra);
        Typeface typeFaceLabel = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        textView.setTypeface(typeFaceLabel);
        adapter = new ExtraNewsAdapter();
        adapter.setHeaderView(headerView);
        adapter.addItemClickListenr(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent(getContext(), NewsDetailActivity.class);
                intent.putExtra(NewsDetailActivity.INDEX, position);
                LogUtil.d(TAG, "position:" + position);
                intent.putExtra(NewsDetailActivity.MORE, false);
                //intent.putParcelableArrayListExtra(NewsDetailActivity.DATA, list);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        if (adapter != null) {
            adapter.clear();
            list.clear();
            list.addAll(mNewsDigest.items);
        }
        addFooterView();
    }


    private void addFooterView() {
        View footerView = LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.extra_news_footer_view, recyclerView, false);
        adapter.setFooterView(footerView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (subscription != null) {
            subscription.unsubscribe();
        }
    }


    public class ExtraNewsAdapter extends BaseRecyclerViewAdapter<NewsDigest.NewsItem> {

        public ExtraNewsAdapter() {
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
                    .inflate(R.layout.extra_news_item, parent, false);
            return new ItemViewHolder(view);
        }
        @Override
        public int getItemCount() {
            return 2 + mNewsDigest.items.size();
        }
        @Override
        public void bindItemView(RecyclerView.ViewHolder srcHolder, final int position) {
            final ItemViewHolder holder = (ItemViewHolder) srcHolder;
            newsItem = mNewsDigest.items.get(position);
            if (newsItem != null) {
                {
                    Typeface typeFaceLabel = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "fonts/Roboto-Bold.ttf");
                    holder.label.setTypeface(typeFaceLabel);
                    holder.label.setText(newsItem.categories.get(0).name);
                    //title
                    Typeface typeFaceTitle = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
                    holder.title.setTypeface(typeFaceTitle);
                    holder.title.setText("" + newsItem.title);
                    //press
                    Typeface typeFacePress = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
                    holder.sources.setTypeface(typeFacePress);
                    holder.sources.setText(newsItem.sources.get(0).publisher);
                    holder.label.setTextColor(android.graphics.Color.parseColor(newsItem.colors.get(0).hexcode));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                        StateListDrawable stateListDrawable = new StateListDrawable();
                        stateListDrawable.addState(new int[]{android.R.attr.state_empty},
                                new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        holder.itemView.setBackground(stateListDrawable);
                    }
                    final int index = position;
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onItemClickListenerList != null) {
                                for (OnItemClickListener onItemClickListener : onItemClickListenerList) {
                                    onItemClickListener.onItemClick(holder, index);
                                }
                            }
                        }
                    });
                }

            }

        }

        @Override
        public void bindHeaderView(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public void bindFooterView(RecyclerView.ViewHolder holder, int position) {

        }
        public class HeaderViewHolder extends RecyclerView.ViewHolder {
            public final View itemView;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
            }
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {
            public final View itemView;

            public FooterViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
            }
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            public final View itemView;
            public final TextView label;
            public final TextView title;
            public final TextView sources;

            public ItemViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                label = (TextView) itemView.findViewById(R.id.label);
                title = (TextView) itemView.findViewById(R.id.title);
                sources = (TextView) itemView.findViewById(R.id.sources);
            }

        }
    }
}