package com.shenke.digest.http;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.shenke.digest.api.RequestAddress;
import com.shenke.digest.core.NewsListActivity;
import com.shenke.digest.entity.Digest;
import com.shenke.digest.entity.Item;
import com.shenke.digest.entity.Items;
import com.shenke.digest.util.LogUtil;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.shenke.digest.api.RequestAddress.YAHOO_NEWS_DIGEST;

public class RxNewsParser {

    public static String TAG = RxNewsParser.class.getSimpleName();

    public static Observable<Digest> getNewsDigest(String baseUrl, final int create_time, final String timezone, final String date,
                                                   final String lang, final String region_edition, final String digest_edition,
                                                   final int more_stories) {
        if (TextUtils.isEmpty(baseUrl)) {
            return null;
        }
        return Observable.just(baseUrl).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                String digestUrl = YAHOO_NEWS_DIGEST + "digest?create_time=" + create_time + "&timezone=" + timezone +
                        "&date=" + date + "&lang=" + lang + "&region_edition=" + region_edition + "&digest_edition=" +
                        digest_edition + "&more_stories=" + more_stories;
                return digestUrl;
            }
        }).map(new Func1<String, Digest>() {
            @Override
            public Digest call(String s) {
                Gson gson = new Gson();
                Digest digest = gson.fromJson(s, Digest.class);
                if (digest != null) {
                    return digest;
                }
                return null;
            }
        });
    }

    public static Observable<List<Item>> getNewsList(String baseUrl, final String newsListUrl) {
        if (TextUtils.isEmpty(baseUrl)) {
            return null;
        }
        return Observable.just(baseUrl).map(new Func1<String, String>() {
            @Override
            public String call(String srcUrl) {

                String contentUrl = NewsListActivity.webpageUrl;

                return newsListUrl + contentUrl;

            }
        }).map(new Func1<String, String>() {

            @Override
            public String call(String url) {
                return get(url);
            }
        }).map(new Func1<String, List<Item>>() {
            @Override
            public List<Item> call(String json) {
                Gson gson = new Gson();
                Items item = gson.fromJson(json, Items.class);
                if (item != null && item.getItems() != null) {
                    return item.getItems();
                }
                return null;
            }
        });

    }


    public static Observable<List<Item>> getNewsContent(String uuid) {
        if (!TextUtils.isEmpty(uuid)) {
            return Observable.just(uuid).map(new Func1<String, String>() {
                @Override
                public String call(String s) {
                    String url = RequestAddress.NEWS_DETAIL_URL + s;
                    return get(url);
                }
            }).map(new Func1<String, List<Item>>() {
                @Override
                public List<Item> call(String json) {
                    Gson gson = new Gson();
                    Items item = gson.fromJson(json, Items.class);
                    if (item != null && item.getItems() != null) {
                        return item.getItems();
                    }
                    return null;
                }
            }).first();

        }
        return null;

    }


    public static String get(String url) {
        String content = null;
        try {
            content = OkHttpSingleton.getInstance().get(url);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "" + e.getMessage());
        }
        return content;
    }

}
