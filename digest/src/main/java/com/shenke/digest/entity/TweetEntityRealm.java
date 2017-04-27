package com.shenke.digest.entity;


import io.realm.RealmList;
import io.realm.RealmObject;

public class TweetEntityRealm extends RealmObject {

    private RealmList<TweetUrls> urls;

    private RealmList<TweetMedia> media;

    private RealmList<StringRealmWrapper> hashtags;

    private RealmList<StringRealmWrapper> symbols;

    public RealmList<TweetUrls> getUrls() {
        return urls;
    }

    public void setUrls(RealmList<TweetUrls> urls) {
        this.urls = urls;
    }

    public RealmList<TweetMedia> getMedia() {
        return media;
    }

    public void setMedia(RealmList<TweetMedia> media) {
        this.media = media;
    }

    public RealmList<StringRealmWrapper> getHashtags() {
        return hashtags;
    }

    public void setHashtags(RealmList<StringRealmWrapper> hashtags) {
        this.hashtags = hashtags;
    }

    public RealmList<StringRealmWrapper> getSymbols() {
        return symbols;
    }

    public void setSymbols(RealmList<StringRealmWrapper> symbols) {
        this.symbols = symbols;
    }

    @Override
    public String toString() {
        return "TweetEntityRealm{" +
                "urls=" + urls +
                ", media=" + media +
                ", hashtags=" + hashtags +
                ", symbols=" + symbols +
                '}';
    }
}
