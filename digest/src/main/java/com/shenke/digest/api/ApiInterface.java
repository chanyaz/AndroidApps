package com.shenke.digest.api;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Cloud on 2017/5/15.
 */

public interface ApiInterface {
    String  BASE_URL = "https://newsdigest-yql.media.yahoo.com/v2/";

    /**
     *
     * @param create_time
     * @param timezone
     * @param date
     * @param lang
     * @param region_edition
     * @param digest_edition
     * @param more_stories
     * @return
     */
    @GET("digest")
    Observable<DigestApi> GetDigestList(@Query("create_time") int create_time, @Query("timezone") String timezone, @Query("date") String date
            , @Query("String date") String lang, @Query("region_edition") String region_edition, @Query("digest_edition") String digest_edition,
                                        @Query("more_stories") int more_stories);
}
