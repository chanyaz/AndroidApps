package com.shenke.digest.util;


import com.shenke.digest.entity.Source;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.observables.GroupedObservable;

public class ReferenceUtil {

    public static Observable<Source> groupSource(List<Source> sources) {

        Observable<GroupedObservable<String, Source>> groupedSources = Observable
                .from(sources)
                .groupBy(new Func1<Source, String>() {
                    @Override
                    public String call(Source source) {
                        return source.getPublisher();
                    }
                });

        return Observable.concat(groupedSources);
    }
}
