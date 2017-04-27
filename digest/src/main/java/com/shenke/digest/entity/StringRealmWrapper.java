package com.shenke.digest.entity;


import io.realm.RealmObject;

public class StringRealmWrapper extends RealmObject {
    public String value;


    public String getValue() {
        return value;
    }

    public StringRealmWrapper setValue(String value) {
        this.value = value;
        return this;
    }
}
