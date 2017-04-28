package com.shenke.digest.entity;

import io.realm.RealmObject;

/**
 * Created by Cloud on 2017/4/28.
 */

public class Bonu extends RealmObject {
    private String id;
    private String text;
    private String type;
    private String source;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "Bonu{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", type='" + type + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
