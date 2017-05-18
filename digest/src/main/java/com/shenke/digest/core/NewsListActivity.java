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
import com.shenke.digest.util.IntentUtil;
import com.shenke.digest.util.LogUtil;
import com.shenke.digest.util.StatusBarCompat;

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
    private Subscription subscription;
    private Subscription subscriptionInstall;
    private Subscription subscriptionSave;
    private DigestLoadDialog digestLoadDialog;
    private MyReceiver myReceiver;
    private int taskCount;
    public static Bitmap bitmap = null;
    private Observer<NewsDigest>observer;
    public Cache mCache;
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
                bundle.putSerializable("NewsDigestData",mNewsDigest);
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
        // TODO:參數從Settings中取
        RetrofitSingleton.getApiService(this)
                .GetDigestList(
                        0,"8","2017-05-18","en-AA", "AA", 0, "0"
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<DigestApi, Boolean>() {
                    @Override
                    public Boolean call(DigestApi mDigestApi) {
                        return mDigestApi.result.lang.equals("en-AA");
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
                       // digestLoadDialog.onLoadError();

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }




    @Override
    protected void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
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
            if (subscription != null) {
                subscription.unsubscribe();
            }

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
