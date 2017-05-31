package com.shenke.digest.core;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.shenke.digest.BuildConfig;
import com.shenke.digest.R;
import com.shenke.digest.api.DigestApi;
import com.shenke.digest.dialog.DigestLoadDialog;
import com.shenke.digest.dialog.EditionDialog;
import com.shenke.digest.dialog.ProductGuideDialog;
import com.shenke.digest.entity.Cache;
import com.shenke.digest.entity.NewsDigest;
import com.shenke.digest.fragment.NewsListFragment;
import com.shenke.digest.http.RetrofitSingleton;
import com.shenke.digest.util.DateUtil;
import com.shenke.digest.util.Helper;
import com.shenke.digest.util.IntentUtil;
import com.shenke.digest.util.LogUtil;
import com.shenke.digest.util.StatusBarCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.shenke.digest.dialog.MoreDigestDialog.SECTION_EVENING;
import static com.shenke.digest.dialog.MoreDigestDialog.SECTION_MORNING;

public class NewsListActivity extends BaseActivity implements DigestLoadDialog.OnNewsLoadInActivityListener {
    private static final String TAG = "NewsListActivity";
    private Subscription subscriptionInstall;
    private Subscription subscriptionSave;
    private DigestLoadDialog digestLoadDialog;
    private MyReceiver myReceiver;
    private int taskCount;
    public static Bitmap bitmap = null;
    private Observer<NewsDigest> observer;
    public Cache mCache;
    private int mSection;
    private int mEdition;
    private String mDate;
    private String mLang;
    private String date = null;
    private int digest_edition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.showSystemUI(this);

        setContentView(R.layout.activity_news_list);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyReceiver.ACTION_TASK_COUNT);
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, intentFilter);
        subscriptionInstall = checkInstall();
        digestLoadDialog = new DigestLoadDialog();
        mSection = getIntent().getIntExtra("SECTION", 2);
        mDate = getIntent().getStringExtra("DATE");
        mLang = LanguageEdtion(getIntent().getIntExtra("LANGUAGE", 3));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, digestLoadDialog, "loading")
                .commit();
        fetchData();
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

    }

    private void fetchData() {
        observer = new Observer<NewsDigest>() {
            @Override
            public void onCompleted() {
                Log.i("fetchData", "onCompleted");
            }


            @Override
            public void onError(Throwable e) {
                digestLoadDialog.onLoadError();
            }

            @Override
            public void onNext(NewsDigest mNewsDigest) {
                Log.i("fetchData", "onNext");
                digestLoadDialog.onLoadSuccess();
                NewsListFragment mNewsListFragment = new NewsListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("DATE", date);
                bundle.putInt("SECTION", digest_edition);
                bundle.putString("LANGUAGE", mLang);
                bundle.putSerializable("NewsDigestData", mNewsDigest);
                mNewsListFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, mNewsListFragment, "list")
                        .commit();

            }
        };

        fetchDataByCache(observer);
    }


    /**
     * 从本地緩存中获取
     */
    private void fetchDataByCache(Observer<NewsDigest> observer) {

        NewsDigest mNewsDigest = null;
        try {
            mNewsDigest = (NewsDigest) mCache.getAsObject("NewsDigestData");

        } catch (Exception e) {
            Log.e("DigestNews", e.toString());
        }

        if (mNewsDigest != null) {
            Observable.just(mNewsDigest).distinct().subscribe(observer);
        } else {
            fetchDataByNetWork(observer);
        }
    }
    /**
     * 网络获取并存入缓存，再从缓存中取出
     */
    private void fetchDataByNetWork(Observer<NewsDigest> observer) {
        Map<String,String > parameters = getParameters();
       int create_time = Integer.valueOf( parameters.get("CREAT_ETIME"));
        String timezone = parameters.get("TIMEZONE");
        String date = parameters.get("DATE");
        String lang = parameters.get("LANGUAGE");
        String region_edition = parameters.get("REGION_EDITION");
        int digest_edition = Integer.valueOf( parameters.get("DIGEST_EDITION"));
        String more_stories = parameters.get("MORE_STORY");

        RetrofitSingleton.getApiService(this)
                .GetDigestList(
                        create_time, timezone, date, lang, region_edition, digest_edition, more_stories
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<DigestApi, Boolean>() {
                    @Override
                    public Boolean call(DigestApi mDigestApi) {
                        return mDigestApi.result.more_stories.equals("0");
                    }
                })
                .map(new Func1<DigestApi, NewsDigest>() {
                    @Override
                    public NewsDigest call(DigestApi mDigestApi) {
                        return mDigestApi.result;
                    }
                })
                .doOnNext(new Action1<NewsDigest>() {
                    @Override
                    public void call(NewsDigest mNewsDigest) {
                        Log.i("NewsDigestData", mNewsDigest.toString());
                        aCache.put("NewsDigestData", mNewsDigest, 3600);//默认一小时后缓存失效
                    }
                })
                .subscribe(observer);

    }
    private  Map<String,String > getParameters(){
        Map<String,String > parameters = new HashMap<String ,String >();
        int create_time = 0;
        String lang = "";
        if (mLang == null) {
            lang = "en-AA";
        } else {
            lang = mLang;
        }
        String timezone = Helper.getTimeZone(lang).trim().substring(3);
        String region_edition = lang.trim().substring(3, 5);
        String more_stories = "0";
        if (mDate != null) {
            date = mDate.trim().substring(10, 14) + "-" + mDate.trim().substring(4, 6) + "-" + mDate.trim().substring(7, 9);

        } else {
            final String nowtime = Helper.getGlobalTime(lang);
            try {  SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String ymd = sdf1.format(sdf2.parse(Helper.getGlobalTime(lang)));
                Date morning = sdf2.parse(ymd + " 08:00:00");
                Date night = sdf2.parse(ymd + " 00:00:00");
                Date present = sdf2.parse(Helper.getGlobalTime(lang));
                if (present.before(morning) && present.after(night)) {
                    String str =  Helper.format(DateUtil.getPreDay(new Date())) ;
                    date = str.trim().substring(10, 14) + "-" + str.trim().substring(4, 6) + "-" + str.trim().substring(7, 9);
                }else{
                    date = nowtime.trim().substring(0, 10);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        if (mSection == 1) {
            digest_edition = 1;
        } else if (mSection == 0) {
            digest_edition = 0;
        } else {
            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String ymd = sdf1.format(sdf2.parse(Helper.getGlobalTime(lang)));
                Date morning = sdf2.parse(ymd + " 08:00:00");
                Date evening = sdf2.parse(ymd + " 18:00:00");
                Date present = sdf2.parse(Helper.getGlobalTime(lang));
                if (present.before(morning) || present.after(evening)) {
                    digest_edition = SECTION_EVENING;
                } else {
                    digest_edition = SECTION_MORNING;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        parameters.put("CREAT_ETIME",String.valueOf(create_time));
        parameters.put("TIMEZONE",timezone);
        parameters.put("DATE",date);
        parameters.put("LANGUAGE",lang);
        parameters.put("REGION_EDITION",region_edition);
        parameters.put("DIGEST_EDITION",String.valueOf(digest_edition));
        parameters.put("MORE_STORY",more_stories);
       /* SharedPreferences pre_settings = getSharedPreferences("PREFERENCES_SETTINS", 0);
        SharedPreferences.Editor editor = pre_settings.edit();
        editor.putStringSet("PARAMETRES",new HashSet<String>(parameters.values()));
       editor.commit();*/
        return parameters;
    }
    private Subscription checkInstall() {

        return rx.Observable
                .create(new Observable.OnSubscribe<Boolean>() {

                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        try {
                            SharedPreferences settings = getSharedPreferences(EditionDialog.PREFS_NAME, 0);
                            boolean first = settings.getBoolean(EditionDialog.OPENED, false);
                            subscriber.onNext(first);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        digestLoadDialog.onLoadError();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (!aBoolean) {
                            ProductGuideDialog productGuideDialog = new ProductGuideDialog();
                            productGuideDialog.show(getSupportFragmentManager(), "productGuideDialog");
                            subscriptionSave = saveInstall();
                        }

                    }
                });

    }

    private Subscription saveInstall() {
        return rx.Observable
                .create(new Observable.OnSubscribe<Boolean>() {

                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        try {
                            SharedPreferences settings = getSharedPreferences(EditionDialog.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(EditionDialog.OPENED, true);
                            subscriber.onNext(editor.commit());
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                         digestLoadDialog.onLoadError();

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }


    @Override
    protected void onDestroy() {

        if (subscriptionInstall != null) {
            subscriptionInstall.unsubscribe();
        }
        if (subscriptionSave != null) {
            subscriptionSave.unsubscribe();
        }
        unregisterReceiver(myReceiver);
        super.onDestroy();


    }

    public String LanguageEdtion(int language) {
        String mlang = "";
        if (language == 0) {
            mlang = "en-CA";
        } else if (language == 1) {
            mlang = "en-GB";
        } else if (language == 2) {
            mlang = "en-US";
        } else {
            mlang = "en-AA";
        }
        return mlang;
    }

    @Override
    public void onLoad() {
        if (IntentUtil.isNetworkConnected(NewsListActivity.this)) {
            fetchData();
        } else {
            digestLoadDialog.onLoadError();
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        public static final String ACTION_TASK_COUNT = "com.shenke.digest.core.action.TASK_COUNT";
        public static final String TASK_COUNT = "task_count";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_TASK_COUNT.equals(action)) {
                    int count = intent.getIntExtra(TASK_COUNT, 0);
                    if (count == taskCount && count > 0) {
                        LogUtil.d(TAG, " all news loaded");
                        digestLoadDialog.onLoadSuccess();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, new NewsListFragment(), "list")
                                .commit();
                    } else {
                        LogUtil.d(TAG, count + " news loaded");
                    }

                }
            }
        }
    }
}
