package com.shenke.digest.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shenke.digest.R;
import com.shenke.digest.adapter.GalleryAdapter;
import com.shenke.digest.core.ExtraNewsListActivity;
import com.shenke.digest.dialog.SettingsDialog;
import com.shenke.digest.dialog.ShareDialog;
import com.shenke.digest.util.DimensionUtil;
import com.shenke.digest.util.IntentUtil;
import com.shenke.digest.util.LogUtil;
import com.shenke.digest.view.DonutProgress;

import rx.Subscription;

/**
 * A simple {@link BaseFragment} subclass.
 * Use the {@link NewsDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsDetailFragment extends BaseFragment {
    private static final String TAG = NewsDetailFragment.class.getSimpleName();
    private final String TAG0 = "NewsDetailFragment";
    private static final String UUID = "uuid";
    private static final String COLOR = "color";
    private static final String INDEX = "index";
    private Subscription subscription;
    private Subscription event;
    private String uuid;
    private int color;
    private int index;



    private ImageButton back;
    private ImageButton share;
    private ImageButton menu;
    private ImageView banner;
    private DonutProgress donutProgress;
    private TextView summaryEdition;
    private View line;
    private TextView lable;
    private TextView title;
    private ViewGroup summary;
    private ViewGroup statDetail;
    private ViewGroup infographs;
    private ViewGroup longreads;
    private ViewGroup locations;
    private ViewGroup slideshow;
    private ViewGroup videos;
    private ViewGroup wikis;
    private ViewGroup tweets;
    private ViewGroup anchorArea;
    private ViewGroup references;
    private RecyclerView gallery;
    private ImageView singleImage;
    private TextView referCount;
    private ImageView toggleImage;
    private ImageView anchor;
    private NestedScrollView nestedScrollView;
    private View functionBar;
    private Typeface typefaceBold;
    private Typeface typefaceLight;
    private Typeface typefaceThin;
    private GalleryAdapter galleryAdapter;
    private TextView error;
    //private Context mContext;
    private Subscription updateIndexSubscription;
    private int distance;
    public static Bitmap bitmap;

    public NewsDetailFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uuid  news' uuid
     * @param color news' color.
     * @param index whether we jump to this page from extra news list. if its value is -1,it indicates we jump from  {@link ExtraNewsListActivity} page.
     * @return A new instance of fragment NewsDetailFragment.
     */

    public static NewsDetailFragment newInstance(String uuid, int color, int index) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putString(UUID, uuid);
        args.putInt(COLOR, color);
        args.putInt(INDEX, index);
        fragment.setArguments(args);
        LogUtil.d(TAG, "uuid:" + uuid);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        distance = (int) DimensionUtil.dp2px(getResources(), 350);
        if (getArguments() != null) {
            uuid = getArguments().getString(UUID);
            color = getArguments().getInt(COLOR);
            index = getArguments().getInt(INDEX);
        }

    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail_news;
    }

    @Override
    protected void initView(View rootView) {
        back = $(rootView, R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        share = $(rootView, R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog shareDialog = new ShareDialog();
                Bundle bundle = new Bundle();
                bundle.putString(ShareDialog.TITLE, title.getText().toString());
                bundle.putString(ShareDialog.LINK, title.getTag().toString());
                bundle.putString(ShareDialog.SOURCE, lable.getTag().toString());
                shareDialog.setArguments(bundle);
                shareDialog.show(getChildFragmentManager(), "share");
            }
        });
        menu = $(rootView, R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = v.getRootView();
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                bitmap = view.getDrawingCache();
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.news_detail_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.send_feedback:
                                sendEmail();
                                return true;
                            case R.id.settings:
                                Bundle bundle = new Bundle();
                                bundle.putString("fragment", TAG0);
                                SettingsDialog settingsDialog = new SettingsDialog();
                                settingsDialog.setArguments(bundle);
                                settingsDialog.show(getChildFragmentManager(), "setting");
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();
            }
        });

        nestedScrollView = $(rootView, R.id.nestedScrollView);
        functionBar = $(rootView, R.id.functionBar);
        banner = $(rootView, R.id.banner);
        donutProgress = $(rootView, R.id.index);
        if (index == -1) {
            donutProgress.setVisibility(View.GONE);
        } else {
            donutProgress.setText("" + (index + 1));
            donutProgress.setTextColor(color);
            donutProgress.setInnerBackgroundColor(Color.TRANSPARENT);
            donutProgress.setFinishedStrokeColor(color);
            donutProgress.setUnfinishedStrokeColor(color);
        }
        lable = $(rootView, R.id.label);
        lable.setTextColor(color);
        typefaceBold = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/Roboto-Bold.ttf");
        typefaceLight = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        typefaceThin = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/Roboto-Thin.ttf");
        lable.setTypeface(typefaceBold);
        title = $(rootView, R.id.title);
        title.setTypeface(typefaceLight);
        summary = $(rootView, R.id.summary);
        summary.removeAllViews();
        statDetail = $(rootView, R.id.statDetail);
        statDetail.removeAllViews();
        infographs = $(rootView, R.id.infographs);
        infographs.removeAllViews();
        longreads = $(rootView, R.id.longreads);
        longreads.removeAllViews();
        locations = $(rootView, R.id.locations);
        locations.removeAllViews();
        videos = $(rootView, R.id.videos);
        videos.removeAllViews();
        wikis = $(rootView, R.id.wikis);
        wikis.removeAllViews();
        tweets = $(rootView, R.id.tweets);
        tweets.removeAllViews();
        references = $(rootView, R.id.sources);
        references.removeAllViews();
        anchorArea = $(rootView, R.id.anchorArea);
        anchor = $(rootView, R.id.anchor);
        toggleImage = $(rootView, R.id.toggleImage);
        referCount = $(rootView, R.id.referCount);
        summaryEdition = $(rootView, R.id.summaryEdition);
        line = $(rootView, R.id.line);
        // referCount.setTypeface(typefaceThin);
        singleImage = $(rootView, R.id.singleImage);
        error = $(rootView, R.id.error);
        gallery = $(rootView, R.id.gallery);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rootView.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        gallery.setLayoutManager(linearLayoutManager);
        gallery.setItemViewCacheSize(2);
        galleryAdapter = new GalleryAdapter();
        gallery.setAdapter(galleryAdapter);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                float fraction = 1.0f * scrollY / distance;

                if (0.75f * fraction <= 1.0f) {
                    banner.setTranslationY(0.75f * fraction * scrollY);
                } else {
                    banner.setTranslationY(distance);
                }
                if (fraction < 0.8f) {
                    if (functionBar.getVisibility() == View.GONE) {
                        functionBar.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (functionBar.getVisibility() == View.VISIBLE) {
                        functionBar.setVisibility(View.GONE);
                    }
                }

            }
        });

    }

    private void sendEmail() {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        String[] addresses = {"yifanfeng@outlook.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        String subject = "Please replace this text with your issues";
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Your Phone does not install email application.", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadNetworkData() {
        if (IntentUtil.isNetworkConnected(getActivity())) {
            if (subscription != null) {
                subscription.unsubscribe();
                subscription = null;
            }
        } else {
            Toast.makeText(getActivity(), "please connect the network", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
        if (updateIndexSubscription != null) {
            updateIndexSubscription.isUnsubscribed();
            updateIndexSubscription = null;
        }
        if (event != null) {
            event.unsubscribe();
            event = null;
        }
    }
}
