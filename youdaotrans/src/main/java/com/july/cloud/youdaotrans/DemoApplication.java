/**
 * @(#)DemoApplication.java, 2015年4月3日. Copyright 2012 Yodao, Inc. All rights
 *                           reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is
 *                           subject to license terms.
 */
package com.july.cloud.youdaotrans;

import android.app.Application;
import android.os.Handler;

/**
 * @author lukun
 */
public class DemoApplication extends Application {

	private static DemoApplication swYouAppction;

	private Handler handler = new Handler();

	// the dir of the default dict.
	public static String LOCAL_DICT_DEFAULT_DIR = "dict_simple";
	public static String LOCAL_DICT_DEFAULT_INDEX_NAME = "yddict.idx";
	public static int MAX_SUGGEST_NUMBER = 20;

	@Override
	public void onCreate() {
		super.onCreate();
		swYouAppction = this;
	}

	public static DemoApplication getInstance() {
		return swYouAppction;
	}

}
