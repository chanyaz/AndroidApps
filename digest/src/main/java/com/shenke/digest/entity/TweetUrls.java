package com.shenke.digest.entity;


import io.realm.RealmObject;

public class TweetUrls extends RealmObject {
    private String expanded_url;
    private String url;

    public String getExpanded_url() {
        return expanded_url;
    }

    public void setExpanded_url(String expanded_url) {
        this.expanded_url = expanded_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "TweetUrls{" +
                "expanded_url='" + expanded_url + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
