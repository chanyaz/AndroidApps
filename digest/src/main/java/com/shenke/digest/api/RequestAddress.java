package com.shenke.digest.api;


public class RequestAddress {

    /**
     * yahoo news digest api url
     */
    public static final String BASE_URL = "https://www.yahoo.com/digest";
    public static final String YAHOO_NEWS_DIGEST = "https://newsdigest-yql.media.yahoo.com/v2/";
    /**
     * query news list according to news's url
     */
    public static final String NEWS_LIST_URL = "https://www.yahoo.com/digest/_td_api/api/resource/digest;url=";
    /**
     * query  detail news according to news's uuid
     */
    public static final String NEWS_DETAIL_URL = "https://www.yahoo.com/digest/_td_api/api/resource/digest;uuids=";


}
