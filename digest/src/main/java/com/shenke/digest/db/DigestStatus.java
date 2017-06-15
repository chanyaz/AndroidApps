package com.shenke.digest.db;

/**
 * Created by Cloud on 2017/6/15.
 */

public class DigestStatus {
    public int _id;
    public String uuid;
    public boolean isChecked;

    public DigestStatus() {

    }

    public DigestStatus(String uuid, boolean isChecked) {
        this.isChecked = isChecked;
        this.uuid = uuid;
    }
}
