package com.shenke.digest.entity;


import io.realm.RealmObject;

public class StatDetailMeta  extends RealmObject {
    private String text;
    private String color;
    private boolean enabled;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "StatDetailMeta{" +
                "text='" + text + '\'' +
                ", color='" + color + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
