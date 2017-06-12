package com.july.cloud.countdowndemo;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 根据时间选择MoreDigest
 */
public class MoreDigestDialog extends DialogFragment {

    private DonutProgress donutProgress;
    private ValueAnimator valueAnimator;
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
   public static final String SECTION_SELECTED ="SECTION_SELECTED";
    public static final String DATE_SELECTED = "DATE_SELECTED";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.countdown_layout, container);
        final HorizontalScrollView hsv = (HorizontalScrollView) rootView.findViewById(R.id.hsv);
        //滑动
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hsv.scrollTo(2000, 0);
            }
        }, 2000);

        linearLayout = (LinearLayout) rootView.findViewById(R.id.newsChooser);
        Typeface typefaceLight = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        donutProgress = (DonutProgress) rootView.findViewById(R.id.progress);
        donutProgress.setMax(100);
        layoutContainer = rootView.findViewById(R.id.container);
        img = (ImageView) rootView.findViewById(R.id.img);
        tvH = (TextView) rootView.findViewById(R.id.h);
        tvH.setTypeface(typefaceLight);
        tvM = (TextView) rootView.findViewById(R.id.m);
        tvM.setTypeface(typefaceLight);
        tvS = (TextView) rootView.findViewById(R.id.s);
        tvS.setTypeface(typefaceLight);
        tvHour = (TextView) rootView.findViewById(R.id.hour);
        tvHour.setTypeface(typefaceLight);
        tvMinute = (TextView) rootView.findViewById(R.id.minute);
        tvMinute.setTypeface(typefaceLight);
        tvSecond = (TextView) rootView.findViewById(R.id.second);
        tvSecond.setTypeface(typefaceLight);
        next = (TextView) rootView.findViewById(R.id.next);
        next.setTypeface(typefaceLight);
        infoType = (TextView) rootView.findViewById(R.id.infoType);
        infoType.setTypeface(typefaceLight);
        mHandler = initHandler();
        return rootView;
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mHandler != null) {
            mHandler.removeMessages(0x110);
        }
    }

    /**
     * 获取选择项
     * @param newsSection
     * @return
     */
    private Subscription getSelected(final int newsSection) {
        return rx.Observable
                .create(new Observable.OnSubscribe<Map<String, String>>() {
                    @Override
                    public void call(Subscriber<? super Map<String, String>> subscriber) {
                        try {
                            section =  0;
                            mLang = "en-GB";
                            mDate = Helper.getGlobalTime(mLang).trim().substring(0, 10);
                            Map<String, String> map = new HashMap<String, String>();
                            map.put(SECTION_SELECTED, String.valueOf(section));
                            map.put(DATE_SELECTED, mDate);
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

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Map<String, String> map) {

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
                if (section == SECTION_MORNING) {
                    mHour = deltaValue.hour;
                    infoType.setText("Util Evening Digest");
                    img.setImageResource(R.mipmap.countdown_day_moon);
                } else {
                    mHour = deltaValue.hour;
                    infoType.setText("Util Morning Digest");
                    img.setImageResource(R.mipmap.countdown_day_sun);
                }
                mMinute =deltaValue.minute;
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
        subscription = getSelected(section);

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
            String ymd = sdf1.format(sdf2.parse(Helper.getGlobalTime("en-AA")));
            Date morning = sdf2.parse(ymd + " 08:00:00");
            Date evening = sdf2.parse(ymd + " 18:00:00");
            Date night = sdf2.parse(ymd + " 00:00:00");
            Date nighttoday = sdf2.parse(ymd + " 23:59:59");
            Date present = sdf2.parse(Helper.getGlobalTime("en-AA"));
            long delta = 0L;
            int day = 0, hour = 0, min = 0, second = 0, progress = 0;

            if (present.after(night) && present.before(morning)) {//00:00至08:00之间
                section = SECTION_EVENING;
                delta = morning.getTime() - present.getTime();
                progress = 100 - (int) (100 * delta / (1000 * 14 * 60 * 60));

            } else if (present.after(evening) && present.before(nighttoday) ) {//18:00:00至23:59:59之间
                section = SECTION_EVENING;

                delta = present.getTime() - evening.getTime()+morning.getTime()-night.getTime();
                progress = (int) (100 * delta / (1000 * 14 * 60 * 60));

            } else {//08:00至18:00之间
                section = SECTION_MORNING;

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
        for (int i = 5; i >= 0; i--) {
            Date date = DateUtil.getPreNDay(new Date(), i);
            final String str = Helper.format(date);
            View item = LayoutInflater.from(getContext()).inflate(R.layout.news_chooser_item, linearLayout, false);
            TextView newsDate = (TextView) item.findViewById(R.id.newsDate);/**日期*/
            ImageView divider = (ImageView) item.findViewById(R.id.divider);
            newsDate.setText(str.substring(4, 9));
            TextView newsDay = (TextView) item.findViewById(R.id.newsDay);/**星期*/
            newsDay.setText(str.substring(0, 3));
            ImageView daytime = (ImageView) item.findViewById(R.id.daytime);
            daytime.setTag(str);
            daytime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();

                }
            });
            ImageView night = (ImageView) item.findViewById(R.id.night);
            night.setTag(str);
            night.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            if (section == SECTION_MORNING) {
                if (i == 5) {
                    daytime.setVisibility(View.GONE);
                    divider.setVisibility(View.INVISIBLE);
                    newsDate.setVisibility(View.INVISIBLE);
                    newsDay.setVisibility(View.INVISIBLE);
                } else if (i == 0) {
                    night.setVisibility(View.GONE);
                    daytime.setBackgroundResource(R.drawable.count_down_check_drawable);
                    daytime.setImageResource(R.mipmap.countdown_day_sun_w);
                }
            } else if (section == SECTION_EVENING) {
                if (i == 5) {
                    item.setVisibility(View.GONE);
                } else if (i == 0) {


                }
            }
            int sectionSelected = Integer.parseInt(map.get(SECTION_SELECTED));
            String dateSelected = map.get(DATE_SELECTED);
            if (sectionSelected == SECTION_MORNING && str.equals(dateSelected)) {
                daytime.setBackgroundResource(R.drawable.count_down_check_drawable);
                daytime.setImageResource(R.mipmap.countdown_day_sun_w);
            } else if (sectionSelected == SECTION_EVENING && str.equals(dateSelected)) {
                night.setBackgroundResource(R.drawable.count_down_check_drawable);
                night.setImageResource(R.mipmap.countdown_day_moon_w);
            }

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
