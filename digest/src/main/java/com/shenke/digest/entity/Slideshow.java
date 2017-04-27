package com.shenke.digest.entity;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Slideshow extends RealmObject {
    private int total;
    private String layout;
    private RealmList<Photo> photos;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public RealmList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(RealmList<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public String toString() {
        return "Slideshow{" +
                "total=" + total +
                ", layout='" + layout + '\'' +
                ", photos=" + photos +
                '}';
    }
}
