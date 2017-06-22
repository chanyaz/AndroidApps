package com.shenke.digest.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.SupportMapFragment;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer.util.Util;
import com.shenke.digest.R;
import com.shenke.digest.adapter.BaseRecyclerViewAdapter;
import com.shenke.digest.adapter.GalleryAdapter;
import com.shenke.digest.core.ExtraNewsListActivity;
import com.shenke.digest.core.LocationActivity;
import com.shenke.digest.core.MediaPlayerActivity;
import com.shenke.digest.core.NewsDetailActivity;
import com.shenke.digest.core.NewsListActivity;
import com.shenke.digest.core.SlideShowActivity;
import com.shenke.digest.db.DigestStatus;
import com.shenke.digest.dialog.SettingsDialog;
import com.shenke.digest.dialog.ShareDialog;
import com.shenke.digest.entity.NewsDigest;
import com.shenke.digest.entity.SlideItem;
import com.shenke.digest.selector.OnSelectListener;
import com.shenke.digest.selector.SelectableTextHelper;
import com.shenke.digest.util.DimensionUtil;
import com.shenke.digest.util.LogUtil;
import com.shenke.digest.util.ReferenceUtil;
import com.shenke.digest.util.URLSpanNoUnderline;
import com.shenke.digest.view.DonutProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.shenke.digest.selector.SelectableTextHelper.tts;

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
    private static final String SOURCE = "source";
    private Subscription subscription;
    private Subscription event;
    private String uuid;
    private int color;
    private int index;
    private String newssource;

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
    private Subscription updateIndexSubscription;
    private int distance;
    public static Bitmap bitmap;
    public NewsDigest mNewsDigest;
    private SelectableTextHelper mSelectableTextHelper;
    private String speakContent = "";
    private String SumAndQuoteText = "";

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

    public static NewsDetailFragment newInstance(String uuid, int color, int index, boolean checked, String source, NewsDigest mNewsDigest) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putString(UUID, uuid);
        args.putInt(COLOR, color);
        args.putInt(INDEX, index);
        args.putBoolean("CHECKED", checked);
        args.putString(SOURCE, source);
        args.putSerializable("NewsDigestData", mNewsDigest);
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
            newssource = getArguments().getString(SOURCE, "Yahoo News Digest");
            mNewsDigest = (NewsDigest) getArguments().getSerializable("NewsDigestData");

        }
        InitTtsEngine();
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
                bundle.putString(ShareDialog.LINK, mNewsDigest.items.get(index).webpageUrl);
                bundle.putString(ShareDialog.SOURCE, newssource);
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
                            case R.id.listen:
                                ListenDigest();
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
      if (mNewsDigest.more_stories .equals("1")) {
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
        initItem(mNewsDigest);

        addSelectableTextHelper(title);
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

    private void ListenDigest() {
        speakContent = title.getText().toString().trim() + "."+ SumAndQuoteText;
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
            tts.speak(speakContent, TextToSpeech.QUEUE_ADD, null);
        } else {
            tts.speak(speakContent, TextToSpeech.QUEUE_ADD, null);
        }

    }

    /**
     * 初始化语音引擎
     */
    private void InitTtsEngine() {
        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //如果装载TTS引擎成功
                if (status == TextToSpeech.SUCCESS) {
                    //设置使用美式英语朗读
                    int result = tts.setLanguage(Locale.US);
                    tts.setSpeechRate(0.8f);//设置播放速率
                    tts.setPitch(1.2f);//设置语音的声高
                    //tts.setVoice();//设置文字语音转化的声音
                    //setOnUtteranceProgressListener(UtteranceProgressListener listener)//设置监听播放进度的回调
                    //如果不支持设置的语言
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE) {
                        // Toast.makeText(MainActivity.this, "TTS暂时不支持这种语言朗读", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void activeItem() {

        rx.Observable
                .create(new rx.Observable.OnSubscribe<Boolean>() {

                    @Override
                    public void call(final Subscriber<? super Boolean> subscriber) {
                        DigestStatus digestStatus =new DigestStatus();
                        digestStatus.uuid = uuid;
                        digestStatus.isChecked = 1;
                        NewsListActivity.mgr.updateStatus(digestStatus);
                        subscriber.onNext(true);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            if (!mNewsDigest.more_stories.equals("1")) {

                                donutProgress.setInnerBackgroundColor(color);
                                donutProgress.setTextColor(Color.WHITE);
                                donutProgress.setVisibility(View.VISIBLE);
                                if (getActivity() instanceof NewsDetailActivity) {
                                    ((NewsDetailActivity) getActivity()).updateIndex(index);
                                }
                            } else {
                                donutProgress.setVisibility(View.GONE);
                            }
                        } else {
                            LogUtil.e(TAG, "active  the data failed");
                        }
                    }
                });


    }


    private void initItem(NewsDigest mNewsDigest) {

        if (mNewsDigest != null && mNewsDigest.items.get(index).multiSummary != null) {

            if (!mNewsDigest.more_stories .equals("1" )) {
                donutProgress.setVisibility(View.VISIBLE);
                donutProgress.setInnerBackgroundColor(color);
                donutProgress.setTextColor(Color.WHITE);
            } else if (mNewsDigest.more_stories.equals("1")) {
                donutProgress.setVisibility(View.GONE);
            }
            //label
            lable.setText(mNewsDigest.items.get(index).categories.get(0).name);
            lable.setVisibility(View.VISIBLE);
            lable.setTag(mNewsDigest.items.get(index).sources.get(0).publisher);
            Glide.with(banner.getContext()).load(mNewsDigest.items.get(index).images.originalUrl).crossFade().into(banner);
            //title
            title.setText("" + mNewsDigest.items.get(index).title);
            // title.setTag("" + itemRealm.getLink());
            title.setVisibility(View.VISIBLE);
            //quote
            addQuote(mNewsDigest.items.get(index));

            summaryEdition.setVisibility(View.VISIBLE);

            //statDetail
            addStatDetail(mNewsDigest.items.get(index));


            //infographs

            addInfographs(mNewsDigest.items.get(index));


            //longreads
            addLongreads(mNewsDigest.items.get(index));

            //locations
            addLocations(mNewsDigest.items.get(index));

            //slideshow

            addSlideShow(mNewsDigest.items.get(index).slideshow);

            //videos
            addVideos(mNewsDigest.items.get(index));

            //wiki
            addWiki(mNewsDigest.items.get(index));

            //tweet
            // addTweet(mNewsDigest.items.get(index));
            //reference
            addReference(mNewsDigest.items.get(index));
            line.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
            if (getActivity() instanceof NewsDetailActivity) {
                int pageIndex = ((NewsDetailActivity) getActivity()).getCurrentIndex();
                if (pageIndex == index && !mNewsDigest.items.get(index).isChecked()) {
                    // LogUtil.e(TAG, "Fragment index:" + (index - 1) + ";viewpager index:" + pageIndex);
                    activeItem();
                }
            }
            if (!mNewsDigest.more_stories .equals("1")) {
                event = getEvent(mNewsDigest.items.get(index));
            }
        } else {
            error.setVisibility(View.VISIBLE);
        }
    }

    private void addSelectableTextHelper(TextView view) {
        String selectcolor = mNewsDigest.items.get(index).colors.get(0).hexcode;
        int colorrr = android.graphics.Color.parseColor("#44" + selectcolor.substring(1));
        mSelectableTextHelper = new SelectableTextHelper.Builder(view)
                .setSelectedColor(colorrr)
                .setCursorHandleSizeInDp(24)
                .setCursorHandleColor(color)//"#00B108"
                .build();
        mSelectableTextHelper.setSelectListener(new OnSelectListener() {
            @Override
            public void onTextSelected(CharSequence content) {

            }
        });
    }

    private void addReference(NewsDigest.NewsItem newsItem) {


        List<NewsDigest.NewsItem.Source> sources = newsItem.sources;


        if (sources != null && sources.size() > 0) {
            anchorArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (references.getVisibility() == View.GONE) {
                        references.setVisibility(View.VISIBLE);
                        toggleImage.setImageResource(R.drawable.reference_open);
                        DrawableCompat.setTint(toggleImage.getDrawable(), color);
                        references.post(new Runnable() {
                            @Override
                            public void run() {
                                nestedScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                    } else if (references.getVisibility() == View.VISIBLE) {
                        references.setVisibility(View.GONE);
                        toggleImage.setImageResource(R.drawable.reference_close);
                        DrawableCompat.setTint(toggleImage.getDrawable(), color);
                    }

                }
            });
        } else {
            anchorArea.setVisibility(View.GONE);
            referCount.setVisibility(View.GONE);
        }
        if (sources.size() == 1) {
            anchorArea.setVisibility(View.VISIBLE);
            ShapeDrawable shapeDrawableAnchor = new ShapeDrawable(new OvalShape());
            shapeDrawableAnchor.getPaint().setColor(color);
            shapeDrawableAnchor.getPaint().setStyle(Paint.Style.STROKE);
            shapeDrawableAnchor.getPaint().setStrokeWidth(DimensionUtil.dp2px(getResources(), 1f));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                anchor.setBackground(shapeDrawableAnchor);
                // toggleImage.setBackground(shapeDrawableAnchor);
            }
            DrawableCompat.setTint(anchor.getDrawable(), color);
            DrawableCompat.setTint(toggleImage.getDrawable(), color);

            referCount.setText("1 Reference");

            final View referencesItemView =
                    LayoutInflater.from(references.getContext()).inflate(R.layout.item_source, references, false);
            TextView publisher = $(referencesItemView, R.id.publisher);
            //publisher.setTypeface(typefaceBold);
            publisher.setText(sources.get(0).publisher);
            ViewGroup titleContainer = $(referencesItemView, R.id.titleContainer);

            View referencestitleItemView =
                    LayoutInflater.from(titleContainer.getContext()).inflate(R.layout.item_source_title, titleContainer, false);
            View view = $(referencestitleItemView, R.id.dot);
            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
            shapeDrawable.getPaint().setColor(color);
            shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(shapeDrawable);
            }
            TextView sourceTitle = $(referencestitleItemView, R.id.sourceTitle);
            sourceTitle.setTypeface(typefaceLight);
            StringBuilder sb = new StringBuilder();
            sb.append("<a href=\"").append(sources.get(0).url).append("\">").append(sources.get(0).title).append("</a>");
            sourceTitle.setText(Html.fromHtml(sb.toString()));
            sourceTitle.setLinkTextColor(Color.BLACK);
            sourceTitle.setMovementMethod(LinkMovementMethod.getInstance());
            URLSpanNoUnderline.stripUnderlines(sourceTitle);
            titleContainer.addView(referencestitleItemView);

            references.addView(referencesItemView);

        } else {
            anchorArea.setVisibility(View.VISIBLE);
            ShapeDrawable shapeDrawableAnchor = new ShapeDrawable(new OvalShape());
            shapeDrawableAnchor.getPaint().setColor(color);
            shapeDrawableAnchor.getPaint().setStyle(Paint.Style.STROKE);
            shapeDrawableAnchor.getPaint().setStrokeWidth(DimensionUtil.dp2px(getResources(), 1f));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                anchor.setBackground(shapeDrawableAnchor);
                // toggleImage.setBackground(shapeDrawableAnchor);
            }
            DrawableCompat.setTint(anchor.getDrawable(), color);
            DrawableCompat.setTint(toggleImage.getDrawable(), color);

            referCount.setText(sources.size() + " References");

            ReferenceUtil.
                    groupSource(sources)
                    .subscribe(new Subscriber<NewsDigest.NewsItem.Source>() {
                        String lastPublisher = "the elder";
                        List<List<NewsDigest.NewsItem.Source>> bucketList = new ArrayList<>();
                        List<NewsDigest.NewsItem.Source> bucket = null;

                        @Override
                        public void onCompleted() {

                            bucketList.add(bucket);

                            for (List<NewsDigest.NewsItem.Source> list : bucketList) {

                                final View referencesItemView =
                                        LayoutInflater.from(references.getContext()).inflate(R.layout.item_source, references, false);
                                TextView publisher = $(referencesItemView, R.id.publisher);
                                publisher.setText(list.get(0).publisher);
                                // publisher.setTypeface(typefaceBold);
                                ViewGroup titleContainer = $(referencesItemView, R.id.titleContainer);
                                //title
                                for (NewsDigest.NewsItem.Source source : list) {
                                    View referencestitleItemView =
                                            LayoutInflater.from(titleContainer.getContext()).inflate(R.layout.item_source_title, titleContainer, false);
                                    View view = $(referencestitleItemView, R.id.dot);
                                    ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
                                    shapeDrawable.getPaint().setColor(color);
                                    shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        view.setBackground(shapeDrawable);
                                    }
                                    TextView sourceTitle = $(referencestitleItemView, R.id.sourceTitle);
                                    sourceTitle.setTypeface(typefaceLight);
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("<a href=\"").append(source.url).append("\">").append(source.title).append("</a>");
                                    sourceTitle.setText(Html.fromHtml(sb.toString()));
                                    sourceTitle.setLinkTextColor(Color.BLACK);
                                    sourceTitle.setMovementMethod(LinkMovementMethod.getInstance());
                                    URLSpanNoUnderline.stripUnderlines(sourceTitle);
                                    titleContainer.addView(referencestitleItemView);
                                    // LogUtil.d(TAG, "---------onCompleted getPublisher: " + source.getPublisher() + " -----");
                                }
                                //LogUtil.d(TAG, "---------onCompleted round-----");

                                references.addView(referencesItemView);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(NewsDigest.NewsItem.Source source) {
                            if (!lastPublisher.equalsIgnoreCase(source.publisher)) {
                                if (bucket == null) {
                                    bucket = new ArrayList<NewsDigest.NewsItem.Source>();
                                    bucket.add(source);
                                } else {
                                    bucketList.add(bucket);
                                    bucket = new ArrayList<NewsDigest.NewsItem.Source>();
                                    bucket.add(source);
                                }
                                lastPublisher = source.publisher;
                            } else {
                                bucket.add(source);
                            }

                        }
                    });
        }
    }

    private void addWiki(NewsDigest.NewsItem newsItem) {
        wikis.removeAllViews();
        List<NewsDigest.NewsItem.Wiki> wikiList = newsItem.wikis;
        if (wikiList != null && !wikiList.isEmpty()) {

            for (NewsDigest.NewsItem.Wiki wiki : wikiList) {

                View wikiItemView =
                        LayoutInflater.from(wikis.getContext()).inflate(R.layout.item_wiki, wikis, false);

                TextView wikiTitle = $(wikiItemView, R.id.wikiTitle);
                wikiTitle.setText("" + wiki.title);
                // wikiTitle.setTypeface(typefaceBold);
                TextView wikiText = $(wikiItemView, R.id.wikiText);
                wikiText.setText("" + wiki.text);
                wikiText.setTypeface(typefaceLight);
                addSelectableTextHelper(wikiText);//添加文字选择
                ImageView wikiSearch = $(wikiItemView, R.id.wikiSearch);
                DrawableCompat.setTint(wikiSearch.getDrawable(), color);

                TextView searchTerms = $(wikiItemView, R.id.searchTerms);
                StringBuilder sb = new StringBuilder();
                for (NewsDigest.NewsItem.Wiki.Term term : wiki.searchTerms) {
                    sb.append(term.term).append("");
                }

                searchTerms.setText("learn more:" + sb.toString());

                searchTerms.setTextColor(color);
                searchTerms.setTypeface(typefaceLight);
                if (!TextUtils.isEmpty(wiki.url)) {
                    final String wikiUrl = wiki.url;
                    wikiItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse(wikiUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                }

                wikis.addView(wikiItemView);

            }
        }
    }

    private void addVideos(NewsDigest.NewsItem newsItem) {
        videos.removeAllViews();
        List<NewsDigest.NewsItem.Video> videoList = newsItem.videos;
        if (videoList != null && !videoList.isEmpty()) {
            for (NewsDigest.NewsItem.Video video : videoList) {

                View videoItemView =
                        LayoutInflater.from(videos.getContext()).inflate(R.layout.item_video, videos, false);
                TextView title = $(videoItemView, R.id.title);
                title.setText("" + video.title);
                //title.setTypeface(typefaceBold);
                ImageView playIcon = $(videoItemView, R.id.playIcon);
                ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
                shapeDrawable.getPaint().setColor(color);
                shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    playIcon.setBackground(shapeDrawable);
                }

                ImageView imageView = $(videoItemView, R.id.thumbnail);
                Glide.with(imageView.getContext()).load(video.thumbnail).centerCrop().crossFade().into(imageView);
                List<NewsDigest.NewsItem.Video.Stream> streams = video.streams;
                if (streams != null && streams.size() > 0) {
                    String url = null;
                    for (NewsDigest.NewsItem.Video.Stream stream : streams) {
                        if (!TextUtils.isEmpty(stream.url)
                                && !TextUtils.isEmpty(stream.mime_type)
                                && !"application/vnd.apple.mpegurl".equalsIgnoreCase(stream.mime_type)
                                ) {
                            url = stream.url;
                            break;
                        }
                    }
                    if (url != null) {
                        final String src = url;
                        videoItemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               Intent mpdIntent = new Intent(v.getContext(), MediaPlayerActivity.class);
                                mpdIntent.setData(Uri.parse(src));
                                mpdIntent.putExtra(MediaPlayerActivity.CONTENT_TYPE_EXTRA, Util.TYPE_OTHER);
                                mpdIntent.putExtra(MediaPlayerActivity.CONTENT_ID_EXTRA, src);
                                mpdIntent.putExtra(MediaPlayerActivity.PROVIDER_EXTRA, "");
                                //Intent mpdIntent = new Intent(v.getContext(), NativeVideoAdActivity.class);//视频AD
                                startActivity(mpdIntent);
                            }
                        });

                    }
                }

                videos.addView(videoItemView);


            }

        }
    }

    private void addInfographs(NewsDigest.NewsItem newsItem) {
        infographs.removeAllViews();
        List<NewsDigest.NewsItem.Infograph> infographList = newsItem.infographs;
        if (infographList != null && infographList.size() > 0) {
            for (NewsDigest.NewsItem.Infograph infograph : infographList) {

                View infographItemView =
                        LayoutInflater.from(infographs.getContext()).inflate(R.layout.item_infograph, infographs, false);

                TextView infographTitle = $(infographItemView, R.id.infographTitle);
                infographTitle.setText(infograph.title);

                TextView infographCaption = $(infographItemView, R.id.infographCaption);
                infographCaption.setText(infograph.caption);

                ImageView infographImg = $(infographItemView, R.id.infographImg);

                //String src = EntityHelper.getImageSrc(infograph.getImages());
                String src = infograph.images.originalUrl;
                Glide.with(infographImg.getContext()).load(src).crossFade().into(infographImg);

                infographs.addView(infographItemView);
            }
        }
    }

    private void addStatDetail(NewsDigest.NewsItem newsItem) {
        statDetail.removeAllViews();
        List<NewsDigest.NewsItem.StatDetail> statDetails = newsItem.statDetail;
        if (statDetails != null && !statDetails.isEmpty()) {

            for (NewsDigest.NewsItem.StatDetail stat : statDetails) {

                View staticItemView =
                        LayoutInflater.from(statDetail.getContext()).inflate(R.layout.item_statics, statDetail, false);
                TextView statDetailTitle = $(staticItemView, R.id.statDetailTitle);
                statDetailTitle.setText(stat.title.text);
                statDetailTitle.setTextColor(color);
                addSelectableTextHelper(statDetailTitle);//添加文字选择

                TextView statDetailValue = $(staticItemView, R.id.statDetailValue);
                statDetailValue.setText(stat.value.text);
                statDetailValue.setTypeface(typefaceLight);

                TextView statDetailUnits = $(staticItemView, R.id.statDetailUnits);
                statDetailUnits.setText(stat.units.text);
                statDetailUnits.setTypeface(typefaceLight);

                TextView statDetailDescription = $(staticItemView, R.id.statDetailDescription);
                statDetailDescription.setText(stat.description.text);
                statDetailDescription.setTypeface(typefaceLight);
                statDetail.addView(staticItemView);
                addSelectableTextHelper(statDetailDescription);//添加文字选择
            }

        }
    }

    private void addQuote(NewsDigest.NewsItem newsItem) {
        summary.removeAllViews();
        List<NewsDigest.NewsItem.Summary> summaries = newsItem.multiSummary;
        String speakstr = "";
        if (summaries != null && !summaries.isEmpty()) {
            for (NewsDigest.NewsItem.Summary item : summaries) {
                View summaryItemView =
                        LayoutInflater.from(summary.getContext()).inflate(R.layout.item_summary, summary, false);
                TextView textView = $(summaryItemView, R.id.summaryText);
                textView.setTypeface(typefaceLight);
                addSelectableTextHelper(textView);//添加文字选择
                ViewGroup quoteContainer = $(summaryItemView, R.id.quoteContainer);
                textView.setText(item.text);
                if (item.quote == null || item.quote.text == null) {
                    speakstr = speakstr + item.text;
                    quoteContainer.removeAllViews();
                } else {
                    NewsDigest.NewsItem.Summary.Quote quote = item.quote;
                    TextView quoteSymbol = $(quoteContainer, R.id.quoteSymbol);
                    quoteSymbol.setTextColor(color);
                    TextView quoteText = $(quoteContainer, R.id.quoteText);
                    quoteText.setTextColor(color);
                    addSelectableTextHelper(quoteText);//添加文字选择
                    // quoteText.setTypeface(typefaceLight);
                    quoteText.setText(quote.text);
                    TextView quoteSource = $(quoteContainer, R.id.quoteSource);
                    quoteSource.setText(quote.source);
                    addSelectableTextHelper(quoteSource);//添加文字选择
                    //quoteSource.setTypeface(typefaceBold);
                    View verticalLine = $(quoteContainer, R.id.verticalLine);
                    verticalLine.setBackgroundColor(color);
                    speakstr = speakstr + item.text + item.quote.text;
                }
                SumAndQuoteText = speakstr;
                summary.addView(summaryItemView);
            }
        }
    }

    private void addLongreads(NewsDigest.NewsItem newsItem) {
        longreads.removeAllViews();
        List<NewsDigest.NewsItem.Longread> longReads = newsItem.longreads;
        if (longReads != null && !longReads.isEmpty()) {
            for (NewsDigest.NewsItem.Longread longRead : longReads) {

                View longReadItemView =
                        LayoutInflater.from(longreads.getContext()).inflate(R.layout.item_topic_in_depth, longreads, false);


                // Image image = longRead.getImages();

                //String src = getImageSource(image);
                String src = longRead.images.originalUrl;
                if (src != null && src.length() > 0) {
                    ImageView longreadImg = $(longReadItemView, R.id.longreadImg);

                    Glide.with(longreads.getContext()).load(src).centerCrop().crossFade().into(longreadImg);
                }


                TextView longreadTitle = $(longReadItemView, R.id.longreadTitle);

                longreadTitle.setText(longRead.title);
                longreadTitle.setTextColor(color);
                addSelectableTextHelper(longreadTitle);//添加文字选择
                TextView longreadPublisher = $(longReadItemView, R.id.longreadPublisher);
                longreadPublisher.setText(longRead.publisher);


                TextView longreadDescription = $(longReadItemView, R.id.longreadDescription);
                longreadDescription.setText(longRead.description);
                longreadDescription.setTypeface(typefaceLight);
                addSelectableTextHelper(longreadDescription);//添加文字选择
                if (!TextUtils.isEmpty(longRead.url)) {
                    final String depthUrl = longRead.url;
                    longReadItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse(depthUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });


                }

                longreads.addView(longReadItemView);

            }
        }
    }

    private void addLocations(NewsDigest.NewsItem newsItem) {
        locations.removeAllViews();
        List<NewsDigest.NewsItem.Location> locationList = newsItem.locations;
        if (locationList != null && !locationList.isEmpty()) {
            for (NewsDigest.NewsItem.Location location : locationList) {

                View locationItemView =
                        LayoutInflater.from(locations.getContext()).inflate(R.layout.item_location, locations, false);
                TextView caption = $(locationItemView, R.id.caption);
                caption.setText(location.caption);
                addSelectableTextHelper(caption);//添加文字选择
                final double latitude = Double.valueOf(TextUtils.isEmpty(location.latitude) ? "0" : location.latitude);
                final double longitude = Double.valueOf(TextUtils.isEmpty(location.longtitude) ? "0" : location.longtitude);
                final int zoomLevel = Integer.valueOf(TextUtils.isEmpty(location.zoonLevel) ? "0" : location.zoonLevel);
                AMapOptions aMapOptions = new AMapOptions();
                aMapOptions.scaleControlsEnabled(false).scrollGesturesEnabled(false);
                SupportMapFragment supportMapFragment = SupportMapFragment.newInstance(aMapOptions);

                LatLng latLng = new LatLng(latitude, longitude);
                final ImageView errorTag = $(locationItemView, R.id.errorTag);
                final ImageView locationImg = $(locationItemView, R.id.locationImg);
                AMap aMap = supportMapFragment.getMap();
                aMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        latLng, zoomLevel, 0, 0));
                aMap.moveCamera(cameraUpdate);
                aMap.getUiSettings().setAllGesturesEnabled(false);
                aMap.getUiSettings().setZoomControlsEnabled(false);
                aMap.getUiSettings().setZoomGesturesEnabled(false);
                aMap.getUiSettings().setMyLocationButtonEnabled(false);
                aMap.setMapLanguage(AMap.ENGLISH);
                aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                    @Override
                    public void onMapLoaded() {
                        errorTag.setVisibility(View.GONE);
                        locationImg.setVisibility(View.GONE);
                    }
                });
                getChildFragmentManager().beginTransaction().add(R.id.mapContainer, supportMapFragment).commit();
                final String captionStr = location.caption;
                final String name = location.name;
                $(locationItemView, R.id.mask).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), LocationActivity.class);
                        intent.putExtra(LocationActivity.LATITUDE, latitude);
                        intent.putExtra(LocationActivity.LONGITUDE, longitude);
                        intent.putExtra(LocationActivity.ZOOM_LEVER, zoomLevel);
                        intent.putExtra(LocationActivity.CAPTION, captionStr);
                        intent.putExtra(LocationActivity.NAME, name);
                        startActivity(intent);
                    }
                });
                // caption.setTypeface(typefaceBold);
                locations.addView(locationItemView);
            }
        }
    }

    private void addSlideShow(NewsDigest.NewsItem.SlideShow slideShow) {
        List<NewsDigest.NewsItem.SlideShow.Photos.Element> elements = slideShow.photos.elements;
        final ArrayList<SlideItem> slideItems = new ArrayList<SlideItem>();

        for (NewsDigest.NewsItem.SlideShow.Photos.Element element : elements) {
            SlideItem slideItem = new SlideItem();
            slideItem.caption = element.caption;
            slideItem.headline = element.headline;
            slideItem.provider_name = element.provider_name;
            slideItem.url = element.images.originalUrl;
            slideItems.add(slideItem);

        }
        if (slideItems.isEmpty()) {
            singleImage.setVisibility(View.GONE);
            gallery.setVisibility(View.GONE);
        } else if (slideItems.size() == 1) {
            singleImage.setVisibility(View.VISIBLE);
            gallery.setVisibility(View.GONE);
            String src = elements.get(0).images.originalUrl;
            Glide.with(singleImage.getContext()).load(src).centerCrop().crossFade().into(singleImage);
            singleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), SlideShowActivity.class);
                    intent.putParcelableArrayListExtra(SlideShowActivity.DATA, slideItems);
                    intent.putExtra(SlideShowActivity.CURRENT_INDEX, 0);
                    startActivity(intent);
                }
            });

        } else {
            singleImage.setVisibility(View.GONE);
            gallery.setVisibility(View.VISIBLE);
            galleryAdapter = new GalleryAdapter(getContext(), slideItems);
            gallery.setAdapter(galleryAdapter);
            galleryAdapter.addItemClickListenr(new BaseRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                    Intent intent = new Intent(gallery.getContext(), SlideShowActivity.class);
                    intent.putParcelableArrayListExtra(SlideShowActivity.DATA, slideItems);
                    intent.putExtra(SlideShowActivity.CURRENT_INDEX, position);
                    startActivity(intent);
                }
            });


        }
    }

    private Subscription getEvent(final NewsDigest.NewsItem newsItem) {
        if (getActivity() instanceof NewsDetailActivity) {
            return ((NewsDetailActivity) getActivity())
                    .getRxBus()
                    .toObserverable(Integer.class)
                    .onBackpressureBuffer()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Integer>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Integer integer) {
                            if (index == integer) {
                                // LogUtil.e(TAG, "Fragment index:" + (index - 1) + ";viewpager index:" + integer);
                                if (!newsItem.isChecked()) {
                                    activeItem();
                                }
                            }

                        }
                    });
        }
        return null;
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
        SelectableTextHelper.ShutSpeech();
    }
}