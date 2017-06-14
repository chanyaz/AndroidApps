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
import com.shenke.digest.entity.NewsDigest;
import com.shenke.digest.fragment.NewsListFragment;
import com.shenke.digest.http.RetrofitSingleton;
import com.shenke.digest.util.Helper;
import com.shenke.digest.util.IntentUtil;
import com.shenke.digest.util.LogUtil;
import com.shenke.digest.util.StatusBarCompat;

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

public class NewsListActivity extends BaseActivity implements DigestLoadDialog.OnNewsLoadInActivityListener {
    private static final String TAG = "NewsListActivity";
    private Subscription subscriptionInstall;
    private Subscription subscriptionSave;
    private DigestLoadDialog digestLoadDialog;
    private MyReceiver myReceiver;
    private int taskCount = 0;
    public static Bitmap bitmap = null;
    private Observer<NewsDigest> observer;
    private int mSection;
    private String mDate;
    private String mLang;
    private String date;
    private int digest_edition = 0;
    public static String PREFERENCES_SETTINS = "PREFERENCES_SETTINS";
    public static String UPDATE_SETTINS = "UPDATE_SETTINS";
    public static String ITEM_IS_CHECKED = "IS_CHECKED";
    private boolean first;//是否第一次打开APP
    private int cachetime; //缓存保留时长

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
        SharedPreferences p_settings = getSharedPreferences(PREFERENCES_SETTINS, 0);
        String strdate = Helper.format(new Date());
        String nowdate = strdate.trim().substring(10, 14) + "-" + strdate.trim().substring(4, 6) + "-" + strdate.trim().substring(7, 9);
        mDate = p_settings.getString("DATE", nowdate);
        mSection = p_settings.getInt("DIGEST_EDITION", 2);
        mLang = p_settings.getString("LANGUAGE", Helper.LanguageEdtion(3));
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
                // digestLoadDialog.onLoadSuccess();
                NewsListFragment mNewsListFragment = new NewsListFragment();
                Bundle bundle = new Bundle();
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
        Map<String, String> parameters = getParameters();
        final String lang = parameters.get("LANGUAGE");
        final String str = parameters.get("DATE");
        final String digest_edition = parameters.get("DIGEST_EDITION");
        try {
            mNewsDigest = (NewsDigest) aCache.getAsObject(lang + "-NewsDigestData-" + str + "-" + digest_edition);
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
        Map<String, String> parameters = getParameters();
        int create_time = Integer.valueOf(parameters.get("CREAT_ETIME"));
        String timezone = parameters.get("TIMEZONE");
        final String date = parameters.get("DATE");
        final String lang = parameters.get("LANGUAGE");
        String region_edition = parameters.get("REGION_EDITION");
        final int digest_edition = Integer.valueOf(parameters.get("DIGEST_EDITION"));
        String more_stories = parameters.get("MORE_STORY");
        SharedPreferences pre_settings = getSharedPreferences(PREFERENCES_SETTINS, 0);
        SharedPreferences.Editor editor = pre_settings.edit();
        editor.putString("DATE", date);
        editor.putString("LANGUAGE", lang);
        editor.putInt("DIGEST_EDITION", digest_edition);
        editor.commit();
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
                        digestLoadDialog.onLoadSuccess();
                        cachetime = Helper.getCacheSaveTime(lang, digest_edition, "08:00:00", "18:00:00");
                        aCache.put(mLang + "-NewsDigestData-" + date + "-" + String.valueOf(digest_edition), mNewsDigest, cachetime);//有新内容时缓存失效
                    }
                })
                .subscribe(observer);

    }

    private Map<String, String> getParameters() {
        Map<String, String> parameters = new HashMap<String, String>();
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
        if (mDate != null && CheckUpdate()) {
            date = mDate;
        } else {
            date = Helper.getDigestDate(lang);
        }
        if (mSection == 1 && CheckUpdate()) {
            digest_edition = 1;
        } else if (mSection == 0 && CheckUpdate()) {
            digest_edition = 0;
        } else {
            digest_edition = Helper.getDigestEdition(lang);
        }
        parameters.put("CREAT_ETIME", String.valueOf(create_time));
        parameters.put("TIMEZONE", timezone);
        parameters.put("DATE", date);
        parameters.put("LANGUAGE", lang);
        parameters.put("REGION_EDITION", region_edition);
        parameters.put("DIGEST_EDITION", String.valueOf(digest_edition));
        parameters.put("MORE_STORY", more_stories);
        return parameters;
    }

    /**
     * 检查是否最新内容，false不用更新，true需要更新
     * @return
     */
    private  Boolean CheckUpdate(){
        Map<String, String> para = Helper.isRequestedLatest(mLang);
        String latest_date = para.get("DATE");
        int latest_edition = Integer.valueOf(para.get("DIGEST_EDITION"));
        SharedPreferences latest_update = getSharedPreferences(PREFERENCES_SETTINS, 0);
        String strdate = Helper.format(new Date());
        String nowdate = strdate.trim().substring(10, 14) + "-" + strdate.trim().substring(4, 6) + "-" + strdate.trim().substring(7, 9);
        String first_date = latest_update.getString("DATE", nowdate);
        int first_edition = latest_update.getInt("DIGEST_EDITION", Helper.getDigestEdition(Helper.LanguageEdtion(3)));
        if (latest_date == first_date && latest_edition == first_edition) {
            return false;
        }else{
            return true;
        }
    }

    private Subscription checkInstall() {

        return rx.Observable
                .create(new Observable.OnSubscribe<Boolean>() {

                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        try {
                            SharedPreferences settings = getSharedPreferences(EditionDialog.PREFS_NAME, 0);
                            first = settings.getBoolean(EditionDialog.OPENED, false);
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
