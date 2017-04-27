package com.shenke.digest.util;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

public class IntentUtil {
    /**
     * 获取已安装apk
     *
     * @param context
     * @param intent
     * @return
     */
    public static List<ResolveInfo> queryIntents(Context context, Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, 0);

    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
