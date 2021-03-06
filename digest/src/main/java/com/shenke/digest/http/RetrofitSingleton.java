package com.shenke.digest.http;

/**
 * Created by Cloud on 2017/5/15.
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shenke.digest.api.ApiInterface;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitSingleton {

    private static ApiInterface apiService = null;

    private static Retrofit retrofit = null;

    private static OkHttpClient okHttpClient = null;

    private static final String TAG = RetrofitSingleton.class.getSimpleName();

    public Context context;


    /**
     * 初始化
     */

    public static void init(final Context context) {

        Executor executor = Executors.newCachedThreadPool();

        Gson gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(ApiInterface.BASE_URL)
                .callbackExecutor(executor)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();


        apiService = retrofit.create(ApiInterface.class);
    }


    public static ApiInterface getApiService(Context context) {
        if (apiService != null) return apiService;
        init(context);
        return getApiService(context);
    }


    public static Retrofit getRetrofit(Context context) {
        if (retrofit != null) return retrofit;
        init(context);
        return getRetrofit(context);
    }


    public static OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient != null) return okHttpClient;
        init(context);
        return getOkHttpClient(context);
    }


    public static void disposeFailureInfo(Throwable t, Context context) {
        if (t.toString().contains("GaiException") || t.toString().contains("SocketTimeoutException") ||
                t.toString().contains("UnknownHostException")) {
            Toast.makeText(context, "Error Network", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
        }
        Log.w(TAG, t.toString());
    }
}