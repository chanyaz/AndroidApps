package com.shenke.digest.core;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shenke.digest.R;
import com.shenke.digest.adapter.NewsAdapter;
import com.shenke.digest.fragment.NewsListFragment;
import com.shenke.digest.util.DateUtil;
import com.shenke.digest.util.Helper;
import com.shenke.digest.util.LogUtil;
import com.shenke.digest.util.StatusBarCompat;
import com.shenke.digest.view.BlurredView;
import com.shenke.digest.view.DonutProgress;
import com.shenke.digest.view.LoadViewLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.shenke.digest.core.NewsListActivity.PREFERENCES_SETTINS;

/**
 * Created by Cloud on 2017/6/26.
 */

public class MoreDigestActivity extends BaseActivity {
    private DonutProgress donutProgress;
    private ValueAnimator valueAnimator;
    private static final String TAG = "MoreDigestDialog";
    private TextView next;
    private TextView tvH, tvM, tvS;
    private ImageView img;
    private TextView tvHour, tvMinute, tvSecond, infoType;
    private View layoutContainer;
    public static final int SECTION_MORNING = 0;
    public static final int SECTION_EVENING = 1;
    private int section;
    private String mDate;
    private String mLang;
    private int mHour;
    private int mMinute;
    private int mSecond;
    private Subscription subscription;
    private LinearLayout linearLayout;
    public static final String SECTION_SELECTED = "SECTION_SELECTED";
    public static final String DATE_SELECTED = "DATE_SELECTED";
    public static final String LANGUAGE_SELECTED = "LANGUAGE_SELECTED";
    private BlurredView iv_background;
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.showSystemUI(this);
        setContentView(R.layout.more_digest_dialog);
        SharedPreferences pre_settings = getSharedPreferences(PREFERENCES_SETTINS, 0);
        section = pre_settings.getInt("DIGEST_EDITION", 0);
        mLang = pre_settings.getString("LANGUAGE", "en-AA");
        mDate = pre_settings.getString("DATE", Helper.getGlobalTime(mLang).trim().substring(0, 10));
        initView();

    }

    private void initView() {
        final HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.hsv);
        //滑动
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hsv.scrollTo(2000, 0);
            }
        }, 2000);
        iv_background = (BlurredView) findViewById(R.id.iv_background);
        String TAG = getIntent().getStringExtra("fragment");
        if (TAG.equals("NewsListFragment")) {
            bitmap = NewsListFragment.bitmap;
        } else if (TAG.equals("LoadViewLayout")) {
            bitmap = LoadViewLayout.bitmap;
        } else if (TAG.equals("NewsAdapter")) {
            bitmap = NewsAdapter.bitmap;
        }
        if (bitmap != null) {
            iv_background.setBlurredImg(bitmap);
            iv_background.setBlurredLevel(100);
        }
        linearLayout = (LinearLayout) findViewById(R.id.newsChooser);
        Typeface typefaceLight = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");
        donutProgress = (DonutProgress) findViewById(R.id.progress);
        donutProgress.setMax(100);
        layoutContainer = findViewById(R.id.container);
        img = (ImageView) findViewById(R.id.img);
        tvH = (TextView) findViewById(R.id.h);
        tvH.setTypeface(typefaceLight);
        tvM = (TextView) findViewById(R.id.m);
        tvM.setTypeface(typefaceLight);
        tvS = (TextView) findViewById(R.id.s);
        tvS.setTypeface(typefaceLight);
        tvHour = (TextView) findViewById(R.id.hour);
        tvHour.setTypeface(typefaceLight);
        tvMinute = (TextView) findViewById(R.id.minute);
        tvMinute.setTypeface(typefaceLight);
        tvSecond = (TextView) findViewById(R.id.second);
        tvSecond.setTypeface(typefaceLight);
        next = (TextView) findViewById(R.id.next);
        next.setTypeface(typefaceLight);
        infoType = (TextView) findViewById(R.id.infoType);
        infoType.setTypeface(typefaceLight);
        mHandler = initHandler();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        progressAnimation();
    }

    private Subscription getSelected() {
        return rx.Observable
                .create(new Observable.OnSubscribe<Map<String, String>>() {
                    @Override
                    public void call(Subscriber<? super Map<String, String>> subscriber) {
                        try {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put(SECTION_SELECTED, String.valueOf(section));
                            map.put(DATE_SELECTED, mDate);
                            map.put(LANGUAGE_SELECTED, mLang);
                            subscriber.onNext(map);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, String>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Map<String, String> map) {
                        LogUtil.e(TAG, "onNext " + map);
                        initChooseItem(map);
                    }
                });
    }

    private void progressAnimation() {
        next.setVisibility(View.VISIBLE);
        next.setAlpha(1f);
        layoutContainer.setVisibility(View.GONE);
        layoutContainer.setAlpha(0f);
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        DeltaValue startValue = new DeltaValue();
        startValue.hour = 0;
        startValue.minute = 0;
        startValue.second = 0;
        startValue.progress = 0;
        DeltaValue endValue = calculateProgress();

        valueAnimator = ValueAnimator.ofObject(new MyTypeEvaluator(), startValue, endValue);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                DeltaValue deltaValue = (DeltaValue) animation.getAnimatedValue();
                donutProgress.setProgress(deltaValue.progress);
                int digest_edition = 0;
                try {
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String ymd = sdf1.format(sdf2.parse(Helper.getGlobalTime(mLang)));
                    Date morning = sdf2.parse(ymd + " 08:00:00");
                    Date evening = sdf2.parse(ymd + " 18:00:00");
                    Date present = sdf2.parse(Helper.getGlobalTime(mLang));
                    if (present.before(morning) || present.after(evening)) {
                        digest_edition = SECTION_EVENING;
                    } else {
                        digest_edition = SECTION_MORNING;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (digest_edition == SECTION_MORNING) {
                    mHour = deltaValue.hour;
                    infoType.setText("Util Evening Digest");
                    img.setImageResource(R.mipmap.countdown_day_moon);
                } else {
                    mHour = deltaValue.hour;
                    infoType.setText("Util Morning Digest");
                    img.setImageResource(R.mipmap.countdown_day_sun);
                }
                mMinute = deltaValue.minute;
                mSecond = deltaValue.second;
                tvHour.setText("" + format(mHour));
                tvMinute.setText("" + format(mMinute));
                tvSecond.setText("" + format(mSecond));
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                layoutContainer.setVisibility(View.VISIBLE);
                next.animate().alpha(0f).setDuration(200).start();
                layoutContainer.animate().alpha(1f).setDuration(200).setStartDelay(200).start();
                mHandler.removeMessages(0x110);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mHandler.sendEmptyMessageDelayed(0x110, 1000);
            }
        });

        valueAnimator.setDuration(1000);
        valueAnimator.start();
        if (subscription != null) {
            subscription.unsubscribe();
        }
        subscription = getSelected();

        donutProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // layoutContainer.setVisibility(View.GONE);
                // next.setVisibility(View.VISIBLE);
            }
        });


    }


    private String format(int value) {
        if (value < 10) {
            return String.valueOf("0" + value);
        }
        return String.valueOf(value);
    }

    /**
     * 倒计时动画
     *
     * @return
     */
    public DeltaValue calculateProgress() {

        DeltaValue deltaValue = new DeltaValue();
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String ymd = sdf1.format(sdf2.parse(Helper.getGlobalTime(mLang)));
            Date morning = sdf2.parse(ymd + " 08:00:00");
            Date evening = sdf2.parse(ymd + " 18:00:00");
            Date night = sdf2.parse(ymd + " 00:00:00");
            Date nighttoday = sdf2.parse(ymd + " 23:59:59");
            Date present = sdf2.parse(Helper.getGlobalTime(mLang));
            long delta = 0L;
            int day = 0, hour = 0, min = 0, second = 0, progress = 0;
            if (present.after(night) && present.before(morning)) {//00:00至08:00之间
                delta = morning.getTime() - present.getTime();
                progress = 100 - (int) (100 * delta / (1000 * 14 * 60 * 60));
            } else if (present.after(evening) && present.before(nighttoday)) {//18:00:00至23:59:59之间
                delta = nighttoday.getTime() - present.getTime() + morning.getTime() - night.getTime();
                progress = (int) (100 * delta / (1000 * 14 * 60 * 60));
            } else {//08:00至18:00之间
                delta = evening.getTime() - present.getTime();
                progress = (int) (100 * delta / (1000 * 10 * 60 * 60));
            }

            day = (int) (delta / (24 * 60 * 60 * 1000));
            hour = (int) (delta / (60 * 60 * 1000) - day * 24);
            min = (int) ((delta / (60 * 1000)) - day * 24 * 60 - hour * 60);
            second = (int) (delta / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

            deltaValue.hour = hour;
            deltaValue.minute = min;
            deltaValue.second = second;
            deltaValue.progress = progress;

        } catch (ParseException e) {

            e.printStackTrace();
        }
        return deltaValue;
    }

    /**
     * 选择日期和版本
     *
     * @param map
     */
    private void initChooseItem(Map<String, String> map) {
        linearLayout.removeAllViews();
        String lang = map.get(LANGUAGE_SELECTED);
        for (int i = 5; i >= 0; i--) {
            Date date = null;
            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String ymd = sdf1.format(sdf2.parse(Helper.getGlobalTime(lang)));
                Date morning = sdf2.parse(ymd + " 08:00:00");
                Date night = sdf2.parse(ymd + " 00:00:00");
                Date present = sdf2.parse(Helper.getGlobalTime(lang));

                if (present.before(morning) && present.after(night)) {
                    Date now_date = DateUtil.getPreDay(new Date());
                    date = DateUtil.getPreNDay(now_date, i);//Thu Jun 08 10:18:42 格林尼治标准时间+0800 2017
                } else {
                    String mTimeZone = Helper.getTimeZone(lang);
                    Calendar calendar = Calendar.getInstance();
                    Calendar ukTime = new GregorianCalendar(TimeZone.getTimeZone(mTimeZone));
                    ukTime.setTimeInMillis(calendar.getTimeInMillis());
                    int now_year = ukTime.get(Calendar.YEAR);
                    int now_month = ukTime.get(Calendar.MONTH);
                    int now_date = ukTime.get(Calendar.DATE);
                    int now_hour = ukTime.get(Calendar.HOUR_OF_DAY);
                    int now_mini = ukTime.get(Calendar.MINUTE);
                    int now_second = ukTime.get(Calendar.SECOND);
                    date = DateUtil.getPreNDay(new Date(now_year - 1900, now_month, now_date, now_hour, now_mini, now_second), i);//Thu Jun 08 10:18:42 格林尼治标准时间+0800 2017
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            final String str = Helper.format(date);//Thu 06/08/2017 10:18:42 周四 Jun Thursday
            View item = LayoutInflater.from(this).inflate(R.layout.news_chooser_item, linearLayout, false);
            TextView newsDate = (TextView) item.findViewById(R.id.newsDate);/**日期*/
            ImageView divider = (ImageView) item.findViewById(R.id.divider);
            newsDate.setText(str.substring(4, 9));
            TextView newsDay = (TextView) item.findViewById(R.id.newsDay);/**星期*/
            newsDay.setText(str.substring(0, 3));
            ImageView daytime = (ImageView) item.findViewById(R.id.daytime);
            daytime.setTag(str);
            final Intent intent = new Intent(this, NewsListActivity.class);
            daytime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    SharedPreferences pre_settings = getSharedPreferences(PREFERENCES_SETTINS, 0);
                    SharedPreferences.Editor editor = pre_settings.edit();
                    String nowdate = str.trim().substring(10, 14) + "-" + str.trim().substring(4, 6) + "-" + str.trim().substring(7, 9);
                    editor.putString("DATE", nowdate);
                    editor.putInt("DIGEST_EDITION", SECTION_MORNING);
                    editor.apply();
                    setResult(3, intent);
                    finish();
                }
            });
            ImageView night = (ImageView) item.findViewById(R.id.night);
            night.setTag(str);
            night.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences pre_settings = getSharedPreferences(PREFERENCES_SETTINS, 0);
                    SharedPreferences.Editor editor = pre_settings.edit();
                    String nowdate = str.trim().substring(10, 14) + "-" + str.trim().substring(4, 6) + "-" + str.trim().substring(7, 9);
                    editor.putString("DATE", nowdate);
                    editor.putInt("DIGEST_EDITION", SECTION_EVENING);
                    editor.apply();
                    setResult(3, intent);
                    finish();
                }
            });

            int digest_edition = Helper.getDigestEdition(lang);
            if (digest_edition == SECTION_MORNING) {
                if (i == 5) {
                    daytime.setVisibility(View.GONE);
                    divider.setVisibility(View.INVISIBLE);
                    newsDate.setVisibility(View.INVISIBLE);
                    newsDay.setVisibility(View.INVISIBLE);
                } else if (i == 0) {
                    night.setVisibility(View.GONE);
                    //daytime.setBackgroundResource(R.drawable.count_down_check_drawable);
                    // daytime.setImageResource(R.mipmap.countdown_day_sun_w);
                }
            } else if (digest_edition == SECTION_EVENING) {
                if (i == 5) {
                    item.setVisibility(View.GONE);
                } else if (i == 0) {


                }
            }

            int sectionSelected = Integer.parseInt(map.get(SECTION_SELECTED));
            String dateSelected = map.get(DATE_SELECTED);
            String datestr = str.trim().substring(10, 14) + "-" + str.trim().substring(4, 6) + "-" + str.trim().substring(7, 9);
            if (sectionSelected == SECTION_MORNING && dateSelected.equals(datestr)) {
                daytime.setBackgroundResource(R.drawable.count_down_check_drawable);
                daytime.setImageResource(R.mipmap.countdown_day_sun_w);
            } else if (sectionSelected == SECTION_EVENING && dateSelected.equals(datestr)) {
                night.setBackgroundResource(R.drawable.count_down_check_drawable);
                night.setImageResource(R.mipmap.countdown_day_moon_w);
            }
            //allChecked DONE!!!
           /* if(allIsChecked){

            }*/
            linearLayout.addView(item);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }


    private Handler mHandler;


    private Handler initHandler() {
        return new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0x110:

                        if (mSecond > 0 && mSecond < 60) {
                            mSecond--;
                        } else if (mSecond == 0) {
                            mSecond = 59;
                            if (mMinute > 0 && mMinute < 60) {
                                mMinute--;
                            } else {
                                mMinute = 59;
                                mSecond--;
                                if (mHour == 0) {
                                    progressAnimation();
                                    return;
                                } else {
                                    mHour--;
                                }

                            }
                        }
                        if (tvHour != null && tvMinute != null && tvSecond != null) {
                            tvHour.setText("" + format(mHour));
                            tvMinute.setText("" + format(mMinute));
                            tvSecond.setText("" + format(mSecond));
                        }
                        sendEmptyMessageDelayed(0x110, 1000);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public static class MyTypeEvaluator implements TypeEvaluator<DeltaValue> {


        @Override
        public DeltaValue evaluate(float fraction, DeltaValue startValue, DeltaValue endValue) {

            DeltaValue result = new DeltaValue();
            result.hour = (int) (startValue.hour + fraction * (endValue.hour - startValue.hour));
            result.minute = (int) (startValue.minute + fraction * (endValue.minute - startValue.minute));
            result.second = (int) (startValue.second + fraction * (endValue.second - startValue.second));
            result.progress = (int) (startValue.progress + fraction * (endValue.progress - startValue.progress));

            return result;
        }
    }

    public static class DeltaValue {
        public int hour;
        public int minute;
        public int second;
        public int progress;
    }
}
