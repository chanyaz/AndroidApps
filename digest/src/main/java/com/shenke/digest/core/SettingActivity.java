package com.shenke.digest.core;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenke.digest.R;
import com.shenke.digest.dialog.EditionDialog;
import com.shenke.digest.dialog.ProductGuideDialog;
import com.shenke.digest.dialog.ShareLogoDialog;
import com.shenke.digest.dialog.TimePickerDialog;
import com.shenke.digest.fragment.NewsDetailFragment;
import com.shenke.digest.fragment.NewsListFragment;
import com.shenke.digest.util.Helper;
import com.shenke.digest.util.RxBus;
import com.shenke.digest.util.StatusBarCompat;
import com.shenke.digest.view.BlurredView;
import com.shenke.digest.view.LoadViewLayout;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.shenke.digest.core.NewsListActivity.PREFERENCES_SETTINS;
import static com.shenke.digest.core.NewsListActivity.UPDATE_SETTINS;

/**
 * Created by Cloud on 2017/6/26.
 */

public class SettingActivity extends BaseActivity {
    private View notifications;
    private View edition;
    private View share;
    private View rate;
    private View works;
    private View privacy;
    private TextView push;
    private TextView tvArea;
    private ImageView pics;
    private String Urlrate = "https://github.com/nanfangjiamu/Cloud9";
    private Subscription subscription;
    private Subscription subscription1;
    private int mEdition = 0;
    private BlurredView iv_background;
    private Bitmap bitmap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.showSystemUI(this);
        setContentView(R.layout.fragment_settings_dialog);
        initView();
    }
    private void initView(){
        iv_background = (BlurredView) findViewById(R.id.iv_background);
        String TAG = getIntent().getStringExtra("fragment");
        if (TAG.equals("NewsListFragment")) {
            bitmap = NewsListFragment.bitmap;
        } else if (TAG.equals("NewsDetailFragment")) {
            bitmap = NewsDetailFragment.bitmap;
        } else if (TAG.equals("LoadViewLayout")) {
            bitmap = LoadViewLayout.bitmap;
        }
        if (bitmap != null) {
            iv_background.setBlurredImg(bitmap);
            iv_background.setBlurredLevel(100);
        }
        /**
         * 通知时间
         */
        push = (TextView) findViewById(R.id.push);
        Typeface typeFacePress = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");
        push.setTypeface(typeFacePress);
        push.setText("8:00 am / 8:00 pm");
        privacy =findViewById(R.id.privacy);
        pics = (ImageView) findViewById(R.id.pics);
        final Animation operatingAnim = AnimationUtils.loadAnimation(MyApplication.getInstance(), R.anim.setting_imagerotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        pics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pics.startAnimation(operatingAnim);
            }
        });
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditionDialog aboutDialog = new EditionDialog();
                Bundle bundle = new Bundle();
                bundle.putString(EditionDialog.PAGE, EditionDialog.PAGE_ABOUT);
                aboutDialog.setArguments(bundle);
                aboutDialog.show(getSupportFragmentManager(), "noticeDialog");
    }
});
        notifications = findViewById(R.id.notifications);
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog();
                Bundle bundle = new Bundle();
                bundle.putString(TimePickerDialog.AM, TimePickerDialog.PM);
                timePickerDialog.setArguments(bundle);
                timePickerDialog.show(getSupportFragmentManager(), "noticeTimeDialog");
            }
        });
        edition = findViewById(R.id.edition);
        tvArea = (TextView) findViewById(R.id.area);
        tvArea.setTypeface(typeFacePress);

        edition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditionDialog editionDialog = new EditionDialog();
                Bundle bundle = new Bundle();
                bundle.putString(EditionDialog.PAGE, EditionDialog.PAGE_NOTICE);
                editionDialog.setArguments(bundle);
                editionDialog.show(getSupportFragmentManager(), "noticeDialog");
            }
        });

        share = findViewById(R.id.shareApp);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLogoDialog editionDialog = new ShareLogoDialog();
                editionDialog.show(getSupportFragmentManager(), "shareLogoDialog");
            }
        });

        rate = findViewById(R.id.rateApp);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Urlrate));
                startActivity(i);
            }
        });
        works = findViewById(R.id.works);
        works.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductGuideDialog productGuideDialog = new ProductGuideDialog();
                productGuideDialog.show(getSupportFragmentManager(), "productDialog");
            }
        });
        subscription = getEvent();

        subscription1 = Observable
                .create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        try {
                            SharedPreferences settings = getSharedPreferences(EditionDialog.PREFS_NAME, 0);
                            int edition = settings.getInt(EditionDialog.EDITION, EditionDialog.EDITION_INT);
                            subscriber.onNext(edition);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        if (subscription1 != null) {
                            subscription1.unsubscribe();
                            subscription1 = null;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        mEdition = integer;
                        initArea(mEdition);
                    }
                });
    }
    private Subscription getEvent() {
        return RxBus
                .getInstance()
                .toObserverable(Integer.class)
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        int edition = integer;
                        initArea(edition);
                        if (edition != mEdition) {
                            mEdition = edition;
                            Intent intent = new Intent(SettingActivity.this, NewsListActivity.class);
                            SharedPreferences pre_settings = getSharedPreferences(PREFERENCES_SETTINS, 0);
                            SharedPreferences.Editor editor = pre_settings.edit();
                            String language = Helper.LanguageEdtion(integer);
                            editor.putString("LANGUAGE", language);
                            String nowdate = Helper.getDigestDate(language);
                            int digest_edition = Helper.getDigestEdition(language);
                            editor.putString("DATE", nowdate);
                            editor.putInt("DIGEST_EDITION", digest_edition);
                            editor.commit();
                            //保存当前最新用于和之后的新内容对比以更新
                            SharedPreferences latest_update = getSharedPreferences(UPDATE_SETTINS, 0);
                            SharedPreferences.Editor update_editor = latest_update.edit();
                            update_editor.putString("LATEST_DATE", nowdate);
                            update_editor.putInt("LATEST_DIGEST_EDITION", digest_edition);
                            update_editor.commit();
                            setResult(2, intent);
                            finish();


                        }
                    }
                });
    }


    private void initArea(int edition) {

        if (edition == EditionDialog.EDITION_INT) {

            tvArea.setText("International");

        } else if (edition == EditionDialog.EDITION_GB) {

            tvArea.setText("United Kingdom");

        } else if (edition == EditionDialog.EDITION_US) {

            tvArea.setText("United States");

        } else if (edition == EditionDialog.EDITION_CA) {

            tvArea.setText("Canada");

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }
}
