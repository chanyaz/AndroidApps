package com.shenke.digest.db;

/**
 * Created by Cloud on 2017/6/15.
 */

public class DigestStatus {
    public int _id;
    public String uuid;
    public int isChecked;

    public DigestStatus() {

    }

    public DigestStatus(String uuid, int isChecked) {
        this.isChecked = isChecked;
        this.uuid = uuid;
    }
}
