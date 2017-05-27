package com.shenke.digest.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Cloud on 2017/5/27.
 */

public class DigestWidgetService extends RemoteViewsService implements Runnable{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return null;
    }

    @Override
    public void run() {

    }
}
