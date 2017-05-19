package com.shenke.digest.core;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.shenke.digest.R;
import com.shenke.digest.api.DigestApi;
import com.shenke.digest.entity.Cache;
import com.shenke.digest.entity.NewsDigest;
import com.shenke.digest.fragment.ExtraNewsListFragment;
import com.shenke.digest.http.RetrofitSingleton;
import com.shenke.digest.util.StatusBarCompat;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ExtraNewsListActivity extends BaseActivity {
    public static final String ALL_CHECKED = "all_checked";
    private Observer<NewsDigest>observer;
    public Cache mCache;
    public boolean allChecked;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.showSystemUI(this);
        setContentView(R.layout.extra_news_activity);
       allChecked = getIntent().getBooleanExtra(ALL_CHECKED, false);
        fetchData();


    }
    private void fetchData() {
        observer = new Observer<NewsDigest>() {
            @Override
            public void onCompleted() {
                Log.i("fetchData", "onCompleted");
            }


            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(NewsDigest mNewsDigest) {
                Log.i("fetchData", "onNext");
                Fragment fragment = new ExtraNewsListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("ExtraNewsDigestData",mNewsDigest);
                bundle.putBoolean(ALL_CHECKED, allChecked);
                fragment.setArguments(bundle);
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, fragment, "extraList")
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
            mNewsDigest = (NewsDigest) mCache.getAsObject("ExtraNewsDigestData");
        } catch (Exception e) {
            Log.e("ExtraNewsDigestData", e.toString());
        }

        if (mNewsDigest != null) {
            Observable.just(mNewsDigest).distinct().subscribe(observer);
        } else {
            fetchDataByNetWork(observer);
        }
    }
    private void fetchDataByNetWork(Observer<NewsDigest> observer) {
        int create_time = 0;
        String timezone = "8";
        String date = "2017-05-19";
        String lang = "en-AA";
        String region_edition = "AA";
        int digest_edition = 0;
        String more_stories = "1";
        RetrofitSingleton.getApiService(this)
                .GetDigestList(
                        create_time,timezone,date,lang, region_edition, digest_edition, more_stories
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<DigestApi, Boolean>() {
                    @Override
                    public Boolean call(DigestApi mDigestApi) {
                        return mDigestApi.result.more_stories.equals("1");
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
                        Log.i("ExtraNewsDigestData", mNewsDigest.toString());
                        aCache.put("ExtraNewsDigestData", mNewsDigest, 3600);//默认一小时后缓存失效
                    }
                })
                .subscribe(observer);

    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.move_in1, R.anim.move_out1);
    }
}
