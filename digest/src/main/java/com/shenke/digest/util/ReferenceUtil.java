package com.shenke.digest.util;

import com.shenke.digest.entity.NewsDigest;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.observables.GroupedObservable;

/**
 * Created by Cloud on 2017/5/19.
 */

public class ReferenceUtil {
    public static Observable<NewsDigest.NewsItem.Source> groupSource(List<NewsDigest.NewsItem.Source> sources) {

        Observable<GroupedObservable<String, NewsDigest.NewsItem.Source>> groupedSources = Observable
                .from(sources)
                .groupBy(new Func1<NewsDigest.NewsItem.Source, String>() {
                    @Override
                    public String call(NewsDigest.NewsItem.Source source) {
                        return source.publisher;
                    }
                });

        return Observable.concat(groupedSources);
    }
}
