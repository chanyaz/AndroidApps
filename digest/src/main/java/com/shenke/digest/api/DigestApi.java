package com.shenke.digest.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shenke.digest.entity.NewsDigest;

/**
 * Created by Cloud on 2017/5/15.
 */

public class DigestApi {
    @SerializedName("result")
    @Expose
    public NewsDigest result = new NewsDigest();
}
