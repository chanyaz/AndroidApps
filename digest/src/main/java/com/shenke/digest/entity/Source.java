package com.shenke.digest.entity;


import io.realm.RealmObject;

public class Source extends RealmObject {

    private String published;
    private String title;
    private String publisher;
    private String uuid;
    private String url;

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Source{" +
                "published='" + published + '\'' +
                ", title='" + title + '\'' +
                ", publisher='" + publisher + '\'' +
                ", uuid='" + uuid + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
