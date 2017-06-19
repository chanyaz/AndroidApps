package com.shenke.digest.translate;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.shenke.digest.R;
import com.youdao.sdk.app.WordHelper;

/**
 * 翻译详情页
 */
public class TranslateDetailActivity extends Activity {

    public static void open(Activity activity, TranslateData news) {
        Intent in = new Intent(activity, TranslateDetailActivity.class);
        in.putExtra("news", news);
        activity.startActivity(in);
    }

    TranslateData translateData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(Activity.RESULT_OK);
        try {
            getWindow().requestFeature(Window.FEATURE_PROGRESS);
            getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
                    Window.PROGRESS_VISIBILITY_ON);
        } catch (Exception e) {
        }
        setContentView(R.layout.translate_detail);

        translateData = (TranslateData) this.getIntent().getSerializableExtra(
                "news");

        TextView input = (TextView) findViewById(R.id.input);
        TextView translation = (TextView) findViewById(R.id.translation);
        TextView spell = (TextView) findViewById(R.id.spell);
        TextView ukSpell = (TextView) findViewById(R.id.ukSpell);
        TextView usSpell = (TextView) findViewById(R.id.usSpell);
        TextView means = (TextView) findViewById(R.id.means);
        TextView webmeans = (TextView) findViewById(R.id.webmeans);
        TextView moreBtn = (TextView) findViewById(R.id.moreBtn);

        if (!TextUtils.isEmpty(translateData.getTranslate().getQuery())) {
            input.setText("Input：" + translateData.getQuery());
        }

        if (!TextUtils.isEmpty(translateData.translates())) {
            translation.setText("Result：" + translateData.translates());
        }

        if (!TextUtils.isEmpty(translateData.getTranslate().getPhonetic())) {
            spell.setText("发音：" + translateData.getTranslate().getPhonetic());
        }

        if (!TextUtils.isEmpty(translateData.getTranslate().getUkPhonetic())) {
            ukSpell.setText("UK："
                    + translateData.getTranslate().getUkPhonetic());
        }

        if (!TextUtils.isEmpty(translateData.getTranslate().getUsPhonetic())) {
            usSpell.setText("US："
                    + translateData.getTranslate().getUsPhonetic());
        }

        if (!TextUtils.isEmpty(translateData.means())) {
            means.setText("Translate Result：\n" + translateData.means());
        }

        if (!TextUtils.isEmpty(translateData.webMeans())) {
            webmeans.setText("Internet Interpretation：\n" + translateData.webMeans());
        }
        moreBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
              WordHelper.openMore(TranslateDetailActivity.this, translateData
                        .getTranslate().getQuery());
             /*   if (!WordHelper.openDeepLink(TranslateDetailActivity.this,
                        translateData.getQuery())) {
                    String deeplinkUrl = WordHelper.getWordDetailUrl(translateData.getQuery());
                    toBrowser(TranslateDetailActivity.this,
                            deeplinkUrl);
                }*/
            }


        });

    }

    public void loginBack(View view) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void finish() {
        super.finish();
    }

}
