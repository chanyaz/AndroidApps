package com.shenke.digest.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Cloud on 2017/5/27.
 */

public class DaemonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            // Widget仅支持4.0+
            Intent service = new Intent();
            service.setClass(context, DigestWidgetService.class);
            context.startService(service);
        }

    }
}
