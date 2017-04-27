package com.shenke.digest.entity;

import io.realm.RealmList;
import io.realm.RealmObject;

public class TweetRealm extends RealmObject {

    private String uuid;
    private String item_id;
    private RealmList<TweetItemRealm> tweets;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public RealmList<TweetItemRealm> getTweets() {
        return tweets;
    }

    public void setTweets(RealmList<TweetItemRealm> tweets) {
        this.tweets = tweets;
    }

    @Override
    public String toString() {
        return "TweetRealm{" +
                "uuid='" + uuid + '\'' +
                ", item_id='" + item_id + '\'' +
                ", tweets=" + tweets +
                '}';
    }
}
