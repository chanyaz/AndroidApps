package com.shenke.digest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 自定义WebView
 * Created by Cloud on 2016/12/27.
 */

public class AriaWebView extends WebView {
    private OnScrollChangedCallback mOnScrollChangedCallback;

    public AriaWebView(Context mContext) {
        super(mContext);

    }

    public AriaWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AriaWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl,
                                   final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(l - oldl, t - oldt);
        }
    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(
            final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    public static interface OnScrollChangedCallback {
        void onScroll(int dx, int dy);
    }
}
