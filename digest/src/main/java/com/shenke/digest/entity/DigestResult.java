package com.shenke.digest.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Cloud on 2017/4/12.
 */

public class DigestResult {
    @SerializedName("result")
    @Expose
    public NewsDigestBean mresult;
}
