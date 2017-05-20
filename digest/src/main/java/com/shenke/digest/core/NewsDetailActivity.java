package com.shenke.digest.core;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.shenke.digest.BuildConfig;
import com.shenke.digest.R;
import com.shenke.digest.entity.NewsDigest;
import com.shenke.digest.fragment.ExtraFragment;
import com.shenke.digest.fragment.NewsDetailFragment;
import com.shenke.digest.util.RxBus;
import com.shenke.digest.util.StatusBarCompat;

import java.util.ArrayList;


public class NewsDetailActivity extends AppCompatActivity {

    public static String TAG = "NewsDetailActivity";
    public static final String INDEX = "index";
    public static final String DATA = "data";
    public static final String MORE = "more";
    private ArrayList<NewsDigest> data = new ArrayList<NewsDigest>();
    private ViewPager viewPager;
    private NewsDetailAdapter adapter;
    private boolean more;
    private int index;
    private RxBus rxBus;
    private int currentIndex = -1;
    public NewsDigest mNewsDigest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.showSystemUI(this);
        setContentView(R.layout.activity_news_detail);
        Intent intent = getIntent();
        rxBus = new RxBus();
        index = intent.getIntExtra(INDEX, 0);
        // data = intent.getParcelableArrayListExtra(DATA);
        mNewsDigest = (NewsDigest) intent.getSerializableExtra(DATA);
        more = intent.getBooleanExtra(MORE, false);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new NewsDetailAdapter(getSupportFragmentManager(), mNewsDigest, more);
        viewPager.setAdapter(adapter);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setCurrentItem(index);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //int preIndex = -1;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // LogUtil.e(TAG, "onPageScrolled position:" + position + ";positionOffset:" + positionOffset);
                if (currentIndex != position) {
                    currentIndex = position;
                    rxBus.post(Integer.valueOf(currentIndex));
                    if (currentIndex == adapter.getCount() - 1 && more) {
                        viewPager.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // rxBus.post(data);
                            }
                        }, 1000);
                    }
                }


            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });
        viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                ImageView img = (ImageView) page.findViewById(R.id.banner);
                if (img != null) {
                    if (position > -1.0f && position < 1.0f) {
                        img.setTranslationX(-0.6f * position * page.getWidth());
                    } else if (position <= -1.0f) {
                        img.setTranslationX(0f);
                    }
                }

            }
        });

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

    }

    public RxBus getRxBus() {
        return rxBus;
    }

    public static class NewsDetailAdapter extends FragmentPagerAdapter {
       private NewsDigest mNewsDigest;
        private boolean more;

        public NewsDetailAdapter(FragmentManager fm, NewsDigest mNewsDigest, boolean more) {
            super(fm);
          this.mNewsDigest = mNewsDigest;
            this.more = more;
        }

        @Override
        public Fragment getItem(int position) {
            if (!more && mNewsDigest != null && !mNewsDigest.items.isEmpty()) {
                NewsDigest.NewsItem newsItem = mNewsDigest.items.get(position);
                return NewsDetailFragment.newInstance(newsItem.uuid, android.graphics.Color.parseColor(newsItem.colors.get(0).hexcode), -1,mNewsDigest);
            } else if (more && mNewsDigest != null && !mNewsDigest.items.isEmpty()) {
                if (position < getCount() - 1) {
                    NewsDigest.NewsItem newsItem= mNewsDigest.items.get(position);
                    return NewsDetailFragment.newInstance(newsItem.uuid, android.graphics.Color.parseColor(newsItem.colors.get(0).hexcode), position,mNewsDigest);

                } else {
                    Fragment fragment = new ExtraFragment();
                    Bundle bundle = new Bundle();
                   // bundle.putParcelableArrayList("data", detailItemArrayList);
                    bundle.putSerializable("NewsDigestData",mNewsDigest);
                    bundle.putInt("INDEX",position);
                    fragment.setArguments(bundle);
                    return fragment;
                }
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            if (more) {
                return mNewsDigest == null ? 0 : mNewsDigest.items.size() + 1;
            } else {
                return mNewsDigest == null ? 0 : mNewsDigest.items.size();
            }
        }
    }


    public int getCurrentIndex() {
        return currentIndex;
    }

    public void updateIndex(int index) {
        if (data != null && !data.isEmpty() && index >= 0 && index < data.size()) {
           //data.get(index).checked = true;
        }

    }

    public void setCurrentPosition(int position) {
      if (viewPager != null && position < adapter.getCount() && position >= 0) {

            viewPager.setCurrentItem(position, true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
