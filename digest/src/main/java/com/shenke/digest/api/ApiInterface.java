package com.shenke.digest.api;


import retrofit2.http.GET;
import retrofit2.http.Query;
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
            , @Query("lang") String lang, @Query("region_edition") String region_edition, @Query("digest_edition") int digest_edition,
                                        @Query("more_stories") String more_stories);
}
