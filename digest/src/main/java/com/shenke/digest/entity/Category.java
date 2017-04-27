package com.shenke.digest.entity;


import io.realm.RealmObject;

public class Category extends RealmObject {
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Category{" +
                "label='" + label + '\'' +
                '}';
    }
}
