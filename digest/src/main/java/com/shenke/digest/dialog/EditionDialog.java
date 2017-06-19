package com.shenke.digest.dialog;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.shenke.digest.R;
import com.shenke.digest.util.Helper;
import com.shenke.digest.util.RxBus;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.shenke.digest.R.id.inIcon;
import static com.shenke.digest.core.NewsListActivity.PREFERENCES_SETTINS;

public class EditionDialog extends DialogFragment implements View.OnClickListener {

    public static final String PREFS_NAME = "NEWS_EDITION";
    public static final String OPENED = "first_time";
    public static final String EDITION = "edition";
    public static final String PAGE = "page";
    public static final String PAGE_NOTICE = "notice";
    public static final String PAGE_ABOUT = "about";
    public static final String SECTION_SELECTED = "sectionSelected";
    public static final String DATE_SELECTED = "dateSelected";
    public static final int EDITION_CA = 0;
    public static final int EDITION_GB = 1;
    public static final int EDITION_US = 2;
    public static final int EDITION_INT = 3;
    private int mEdition;
    private View termOfService;
    private View privacyPolicy;
    private View credits;
    private View uk;
    private View ca;
    private View usa;
    private View internation;
    private View caIcon;
    private View intIcon;
    private View usaIcon;
    private View ukIcon;
    private Subscription subscription;
    private String from = PAGE_NOTICE;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            from = bundle.getString(PAGE, PAGE_NOTICE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.edition_dialog, null);
        final View notice = rootView.findViewById(R.id.notice);
        final View about = rootView.findViewById(R.id.about);
        if (PAGE_NOTICE.equalsIgnoreCase(from)) {
            notice.setVisibility(View.VISIBLE);
            about.setVisibility(View.GONE);
            usaIcon = rootView.findViewById(R.id.usaIcon);
            intIcon = rootView.findViewById(inIcon);
            ukIcon = rootView.findViewById(R.id.ukIcon);
            caIcon = rootView.findViewById(R.id.caIcon);
            usa = rootView.findViewById(R.id.usa);
            usa.setOnClickListener(this);
            internation = rootView.findViewById(R.id.in);
            internation.setOnClickListener(this);
            uk = rootView.findViewById(R.id.uk);
            uk.setOnClickListener(this);
            ca = rootView.findViewById(R.id.ca);
            ca.setOnClickListener(this);
            subscription = rx.Observable
                    .create(new Observable.OnSubscribe<Integer>() {

                        @Override
                        public void call(Subscriber<? super Integer> subscriber) {
                            try {
                                SharedPreferences p_settings = getContext().getSharedPreferences(PREFERENCES_SETTINS, 0);
                                String language = p_settings.getString("LANGUAGE", Helper.LanguageEdtion(3));
                                int edition = Language2Edtion(language);
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
                            if (subscription != null) {
                                subscription.unsubscribe();
                                subscription = null;
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Integer integer) {
                            mEdition = integer;
                            resetIcon(mEdition);
                        }
                    });
        } else {
            notice.setVisibility(View.GONE);
            about.setVisibility(View.VISIBLE);
            termOfService = rootView.findViewById(R.id.termsOfService);
            privacyPolicy = rootView.findViewById(R.id.privacyPolicy);
            credits = rootView.findViewById(R.id.credits);
            termOfService.setOnClickListener(this);
            privacyPolicy.setOnClickListener(this);
            credits.setOnClickListener(this);
        }
        builder.setView(rootView);
        return builder.create();
    }
    public static int Language2Edtion(String language) {
        int mlang = 3;
        if (language.equals("en-CA") ) {
            mlang = 0;
        } else if (language.equals("en-GB")) {
            mlang = 1;
        } else if (language.equals("en-US")) {
            mlang = 2;
        }
        return mlang;
    }
    @Override
    public void onClick(View v) {
        int edition = EDITION_INT;
        switch (v.getId()) {
            case R.id.usa:
                resetIcon(edition = EDITION_US);
                break;
            case R.id.in:
                resetIcon(edition = EDITION_INT);
                break;
            case R.id.uk:
                resetIcon(edition = EDITION_GB);
                break;
            case R.id.ca:
                resetIcon(edition = EDITION_CA);
                break;
            case R.id.termsOfService:
                Uri uri = Uri.parse("https://www.yahoo.com/digest");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.privacyPolicy:
                Uri uri1 = Uri.parse("https://policies.yahoo.com/ca/en/yahoo/privacy/topics/mobile/index.htm");
                Intent i = new Intent(Intent.ACTION_VIEW, uri1);
                startActivity(i);
                break;
            case R.id.credits:
                Toast.makeText(getContext(), "building", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        if (edition != mEdition && PAGE_NOTICE.equalsIgnoreCase(from)) {
            mEdition = edition;
            rx.Observable
                    .create(new rx.Observable.OnSubscribe<Boolean>() {
                        @Override
                        public void call(Subscriber<? super Boolean> subscriber) {
                            try {
                                SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putInt(EDITION, mEdition);
                                if (editor.commit()) {
                                    subscriber.onNext(true);
                                } else {
                                    subscriber.onNext(false);
                                }
                                subscriber.onCompleted();
                            } catch (Exception e) {
                                subscriber.onError(e);
                            }
                        }
                    })
                    .onBackpressureBuffer()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {
                            RxBus.getInstance().post(Integer.valueOf(mEdition));
                            dismiss();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            dismiss();
                        }

                        @Override
                        public void onNext(Boolean success) {
                            if (!success) {
                                Toast.makeText(getContext(), "saving date occurs error", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        } else {
            dismiss();
        }

    }

    private void reset() {
        usaIcon.setVisibility(View.GONE);
        ukIcon.setVisibility(View.GONE);
        intIcon.setVisibility(View.GONE);
        caIcon.setVisibility(View.GONE);
    }

    private void resetIcon(int edition) {
        reset();
        if (edition == EDITION_GB) {

            ukIcon.setVisibility(View.VISIBLE);

        } else if (edition == EDITION_INT) {

            intIcon.setVisibility(View.VISIBLE);

        } else if (edition == EDITION_US) {

            usaIcon.setVisibility(View.VISIBLE);

        } else if (edition == EDITION_CA) {

            caIcon.setVisibility(View.VISIBLE);

        }
    }

}
