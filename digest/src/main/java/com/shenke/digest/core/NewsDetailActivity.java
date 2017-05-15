package com.shenke.digest.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.shenke.digest.R;
import com.shenke.digest.util.RxBus;
import com.shenke.digest.util.StatusBarCompat;


public class NewsDetailActivity extends AppCompatActivity {

    public static String TAG = "NewsDetailActivity";
    public static final String INDEX = "index";
    public static final String DATA = "data";
    public static final String MORE = "more";
  //  private ArrayList<DetailItem> data;
    private ViewPager viewPager;
  //  private NewsDetailAdapter adapter;
    private boolean more;
    private int index;
    private RxBus rxBus;
    private int currentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.showSystemUI(this);
        setContentView(R.layout.activity_news_detail);
        Intent intent = getIntent();
        rxBus = new RxBus();
        index = intent.getIntExtra(INDEX, 0);
        //data = intent.getParcelableArrayListExtra(DATA);
        more = intent.getBooleanExtra(MORE, false);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
   /*     adapter = new NewsDetailAdapter(getSupportFragmentManager(), data, more);
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
        }*/

    }

    public RxBus getRxBus() {
        return rxBus;
    }

   /* public static class NewsDetailAdapter extends FragmentPagerAdapter {
       // private ArrayList<DetailItem> detailItemArrayList;
        private boolean more;

        public NewsDetailAdapter(FragmentManager fm, ArrayList<DetailItem> detailItemArrayList, boolean more) {
            super(fm);
          //  this.detailItemArrayList = detailItemArrayList;
            this.more = more;
        }

        @Override
        public Fragment getItem(int position) {
            if (!more && detailItemArrayList != null && !detailItemArrayList.isEmpty()) {
                DetailItem item = detailItemArrayList.get(position);
                return NewsDetailFragment.newInstance(item.uuid, item.color, -1);
            } else if (more && detailItemArrayList != null && !detailItemArrayList.isEmpty()) {
                if (position < getCount() - 1) {
                    DetailItem item = detailItemArrayList.get(position);
                    return NewsDetailFragment.newInstance(item.uuid, item.color, position);
                } else {
                    Fragment fragment = new ExtraFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("data", detailItemArrayList);
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
                return detailItemArrayList == null ? 0 : detailItemArrayList.size() + 1;
            } else {
                return detailItemArrayList == null ? 0 : detailItemArrayList.size();
            }
        }
    }


    public int getCurrentIndex() {
        return currentIndex;
    }

    public void updateIndex(int index) {
        if (data != null && !data.isEmpty() && index >= 0 && index < data.size()) {
            data.get(index).checked = true;
        }

    }*/

    public void setCurrentPosition(int position) {
       /* if (viewPager != null && position < adapter.getCount() && position >= 0) {

            viewPager.setCurrentItem(position, true);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
