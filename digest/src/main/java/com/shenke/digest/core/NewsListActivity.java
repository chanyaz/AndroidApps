package com.shenke.digest.core;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.shenke.digest.BuildConfig;
import com.shenke.digest.R;
import com.shenke.digest.api.RequestAddress;
import com.shenke.digest.db.EntityHelper;
import com.shenke.digest.dialog.DigestLoadDialog;
import com.shenke.digest.dialog.EditionDialog;
import com.shenke.digest.dialog.ProductGuideDialog;
import com.shenke.digest.entity.DigestResult;
import com.shenke.digest.entity.ExtraDigestResult;
import com.shenke.digest.entity.ExtraNewsDigestBean;
import com.shenke.digest.entity.Item;
import com.shenke.digest.entity.ItemRealm;
import com.shenke.digest.entity.NewsDigestBean;
import com.shenke.digest.fragment.NewsListFragment;
import com.shenke.digest.http.RxNewsParser;
import com.shenke.digest.service.BatchLoadNewsIntentService;
import com.shenke.digest.util.DateUtil;
import com.shenke.digest.util.IntentUtil;
import com.shenke.digest.util.LogUtil;
import com.shenke.digest.util.StatusBarCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * TODO: 动态添加参数获取digest
 */
public class NewsListActivity extends AppCompatActivity implements DigestLoadDialog.OnNewsLoadInActivityListener {
    private static final String TAG = "NewsListActivity";
    private Subscription subscription;
    private Subscription subscriptionInstall;
    private Subscription subscriptionSave;
    private Realm realm;
    private RealmAsyncTask asyncTransaction;
    private DigestLoadDialog digestLoadDialog;
    private MyReceiver myReceiver;
    private int taskCount;
    public static Bitmap bitmap = null;
    private final int GET_DIGEST_URL = 1;
    public static String originalUrl = "";
    public static String webpageUrl = "";
    public static String bonus_text = "";
    public static ExtraNewsDigestBean mNewsDigestBean;
    public String strdate = "";
    public static Date nowdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.showSystemUI(this);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_news_list);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyReceiver.ACTION_TASK_COUNT);
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, intentFilter);
        Message message = new Message();
        message.what = GET_DIGEST_URL;
        mHandler.sendMessage(message);
        subscription = checkDataBase();
        subscriptionInstall = checkInstall();
        digestLoadDialog = new DigestLoadDialog();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, digestLoadDialog, "loading")
                .commit();

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

    }


    /**
     * 一般http请求获取digesturl
     */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            nowdate = new Date();
            strdate = DateUtil.format(nowdate, "yyyy-MM-dd");
            String digest_edition = "";
            try {
                GregorianCalendar g = new GregorianCalendar();
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                String ymd = sdf1.format(g.getTime());
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                /**
                 *   此处可自定义每天起始时间
                 */
                Date morning = sdf2.parse(ymd + " 08:00:00");
                Date evening = sdf2.parse(ymd + " 18:00:00");
                Date present = g.getTime();

                if (present.before(morning) || present.after(evening)) {
                    digest_edition = "1";

                } else {
                    digest_edition = "0";

                }
            } catch (ParseException e) {

                e.printStackTrace();
            }
            final String digestedition = digest_edition;
            switch (msg.what) {
                case GET_DIGEST_URL:
                    final String timeZone = String.valueOf(nowdate).substring(30, 31);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getDigestUrl(0, timeZone, strdate, "en-AA", "AA", digestedition, 0);
                            getExtraDigestUrl(0, timeZone, strdate, "en-AA", "AA", digestedition, 1);
                        }
                    }).start();
                    break;
            }

        }

    };


    public String getDigestUrl(int create_time, String timezone, String date, String lang, String region_edition, String digest_edition, int more_stories) {
        String Url = RequestAddress.YAHOO_NEWS_DIGEST + "digest?create_time=" + create_time + "&timezone=" + timezone + "&date=" + date + "&lang=" + lang + "&region_edition=" + region_edition + "&digest_edition=" + digest_edition + "&more_stories=" + more_stories;
        String result = "";
        try {
            URL url = new URL(Url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);

            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                result += readLine;
            }
            in.close();
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        DigestResult mDigestResult = gson.fromJson(result, DigestResult.class);
        if (mDigestResult.mresult != null) {
            NewsDigestBean mNewsDigestBean = mDigestResult.mresult;
            if (mNewsDigestBean != null) {
                originalUrl = mNewsDigestBean.poster.images.originalUrl;
                webpageUrl = mNewsDigestBean.items.get(0).webpageUrl;
                bonus_text = mNewsDigestBean.bonus.get(0).text + "\n" + "\n-" + mNewsDigestBean.bonus.get(0).source;
                return mNewsDigestBean.toString();
            }
        }
        return result;
    }

    public String getExtraDigestUrl(int create_time, String timezone, String date, String lang, String region_edition, String digest_edition, int more_stories) {
        String Url = RequestAddress.YAHOO_NEWS_DIGEST + "digest?create_time=" + create_time + "&timezone=" + timezone + "&date=" + date + "&lang=" + lang + "&region_edition=" + region_edition + "&digest_edition=" + digest_edition + "&more_stories=" + more_stories;
        String result = "";
        try {
            URL url = new URL(Url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);

            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                result += readLine;
            }
            in.close();
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        ExtraDigestResult mDigestResult = gson.fromJson(result, ExtraDigestResult.class);
        if (mDigestResult.mresult != null) {
            mNewsDigestBean = mDigestResult.mresult;
            return mNewsDigestBean.toString();
        }
        return result;
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

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }

    /**
     * checkDataBase from realm database
     *
     * @return
     */
    private Subscription checkDataBase() {

        return realm.asObservable()
                .map(new Func1<Realm, Boolean>() {
                    @Override
                    public Boolean call(Realm realm) {
                        String str = DateUtil.format(DateUtil.getPreDay(nowdate), "yyyy-MM-dd");
                        //从Realm数据库中条件查询
                        RealmResults<ItemRealm> list = realm.where(ItemRealm.class).contains("published", str).findAll();
                        return list != null && list.size() > 0;
                    }
                })
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG, "  checkDataBase from database onCompleted called");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        LogUtil.e(TAG, "  checkDataBase from database onError called");
                    }

                    @Override
                    public void onNext(Boolean hasLocalData) {
                        LogUtil.d(TAG, "  checkDataBase from database onNext called");
                        if (!hasLocalData) {
                            if (subscription != null && !subscription.isUnsubscribed()) {
                                subscription.unsubscribe();
                                subscription = null;
                            }
                            if (IntentUtil.isNetworkConnected(NewsListActivity.this)) {
                                subscription = newsListSubscription();
                            } else {
                                digestLoadDialog.onLoadError();
                            }
                        } else {
                            digestLoadDialog.onLoadSuccess();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, new NewsListFragment(), "list")
                                    .commit();

                        }
                    }
                });

    }

    /**
     * load data from internet
     *
     * @return
     */
    public Subscription newsListSubscription() {
        return RxNewsParser
                .getNewsList(RequestAddress.BASE_URL, RequestAddress.NEWS_LIST_URL)
                .onBackpressureBuffer()
                .map(new Func1<List<Item>, RealmList<ItemRealm>>() {
                    @Override
                    public RealmList<ItemRealm> call(List<Item> items) {
                        RealmList<ItemRealm> list = new RealmList<ItemRealm>();
                        for (Item item : items) {
                            list.add(EntityHelper.convert(item));
                        }
                        return list;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RealmList<ItemRealm>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG, " load data from internet onCompleted called");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(TAG, " load data from internet onError called");
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(RealmList<ItemRealm> itemRealms) {
                        LogUtil.d(TAG, " load data from internet onNext called");
                        cancelAsyncTransaction();
                        final RealmList<ItemRealm> data = itemRealms;
                        asyncTransaction = realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(data);
                                LogUtil.d("executeTransactionAsync", "writing data into database");
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                LogUtil.d(TAG, "writing data into database successfully");
                                taskCount = data.size();
                                for (ItemRealm itemRealm : data) {
                                    Intent intent = new Intent(NewsListActivity.this, BatchLoadNewsIntentService.class);
                                    intent.setAction(BatchLoadNewsIntentService.ACTION_BATCH_LOAD);
                                    intent.putExtra(BatchLoadNewsIntentService.UUID, itemRealm.getId());
                                    startService(intent);
                                }


                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                LogUtil.e(TAG, " writing data into database unsuccessfully");
                                error.printStackTrace();

                            }
                        });
                    }
                });
    }

    /**
     * 取消异步事务
     */
    private void cancelAsyncTransaction() {
        if (asyncTransaction != null && !asyncTransaction.isCancelled()) {
            asyncTransaction.cancel();
            asyncTransaction = null;
        }
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
        cancelAsyncTransaction();
        realm.close();
        unregisterReceiver(myReceiver);
        super.onDestroy();


    }


    @Override
    public void onLoad() {
        if (IntentUtil.isNetworkConnected(NewsListActivity.this)) {
            if (subscription != null) {
                subscription.unsubscribe();
            }
            subscription = newsListSubscription();

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
