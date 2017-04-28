package com.shenke.digest.entity;

import io.realm.RealmObject;

/**
 * Created by Cloud on 2017/4/28.
 */

public class Poster extends RealmObject {
    private Image images;

    public Image getImages() {
        return images;
    }

    public void setImages(Image images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Poster{" +
                "images=" + images +
                '}';
    }
}
