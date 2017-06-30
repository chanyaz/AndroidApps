package com.shenke.digest.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.shenke.digest.R;
import com.shenke.digest.adapter.NewsAdapter;
import com.shenke.digest.core.ExtraNewsListActivity;
import com.shenke.digest.core.MoreDigestActivity;
import com.shenke.digest.core.NewsDetailActivity;
import com.shenke.digest.core.NewsListActivity;
import com.shenke.digest.core.SettingActivity;
import com.shenke.digest.db.DigestStatus;
import com.shenke.digest.entity.NewsDigest;
import com.shenke.digest.util.Helper;
import com.shenke.digest.util.StatusBarCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.shenke.digest.core.NewsListActivity.PREFERENCES_SETTINS;
import static com.shenke.digest.core.NewsListActivity.mgr;

public class NewsListFragment extends BaseFragment {
    private final String TAG = "NewsListFragment";
    private NewsAdapter adapter;
    private RecyclerView recyclerView;
    private ImageButton menu;
    private boolean initFooterView = false;
    public  ArrayList<DigestStatus>foot_list = new ArrayList<DigestStatus>();
    private int mSection;
    private String mDate;
    private String lang;
    public static Bitmap bitmap;
    public NewsDigest mNewsDigest;
    private Subscription subscription;
    private int lastPosition = 0;
    private int lastOffset = 0;
    public List<DigestStatus> digests = NewsListActivity.mgr.query();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsDigest = (NewsDigest) getArguments().getSerializable("NewsDigestData");
        SharedPreferences p_settings = getContext().getSharedPreferences(PREFERENCES_SETTINS, 0);
        String strdate = Helper.format(new Date());
        String nowdate = strdate.trim().substring(10, 14) + "-" + strdate.trim().substring(4, 6) + "-" + strdate.trim().substring(7, 9);
        mDate = p_settings.getString("DATE", nowdate);
        mSection = p_settings.getInt("DIGEST_EDITION", 2);
        lang = p_settings.getString("LANGUAGE", Helper.LanguageEdtion(3));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_list;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (initFooterView) {
            updateFooterView();
        }
    }

    @Override
    protected void initView(final View rootView) {
        StatusBarCompat.showSystemUI(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        menu = (ImageButton) rootView.findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = v.getRootView();
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                bitmap = view.getDrawingCache();
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.news_list_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.more_digest:
                                moreDigest();
                                return true;
                            case R.id.send_feedback:
                                sendEmail();
                                return true;
                            case R.id.settings:
                                setting();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        adapter = new NewsAdapter(getContext(), mNewsDigest);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NewsAdapter.ViewHolder holder, int position) {
                Intent intent = new Intent(rootView.getContext(), NewsDetailActivity.class);
                intent.putExtra(NewsDetailActivity.INDEX, position);
                intent.putExtra(NewsDetailActivity.MORE, false);
                intent.putExtra(NewsDetailActivity.SOURCE, NewsAdapter.newssource);
                intent.putExtra(NewsDetailActivity.DATA, mNewsDigest);
                DigestStatus digestStatus = new DigestStatus();
                digestStatus.uuid = mNewsDigest.items.get(position).uuid;
                digestStatus.isChecked = 1;
                mgr.updateStatus(digestStatus);
                mNewsDigest.items.get(position).checked = true;
                startActivityForResult(intent, 0x111);

            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(recyclerView.getLayoutManager() != null) {
                    //获取可视的第一个view
                    final int firstCompletelyVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    View topView = recyclerView.getLayoutManager().getChildAt(firstCompletelyVisibleItemPosition);
                    if(topView != null) {
                        //获取与该view的顶部的偏移量
                        lastOffset = topView.getTop();
                        //得到该View的数组位置
                        lastPosition = recyclerView.getLayoutManager().getPosition(topView);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final int firstCompletelyVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                if (firstCompletelyVisibleItemPosition > 1) {
                    if (menu.getVisibility() == View.VISIBLE) {
                        menu.setVisibility(View.GONE);
                    }
                } else {
                    if (menu.getVisibility() == View.GONE) {
                        menu.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        subscription = load();
    }
    private Subscription load() {
        return rx.Observable
                .create(new Observable.OnSubscribe<NewsDigest>() {
                    @Override
                    public void call(Subscriber<? super NewsDigest> subscriber) {

                        for(NewsDigest.NewsItem newsItem : mNewsDigest.items){
                            for(int i = 0; i<digests.size();i++){
                                if(digests.get(i).uuid.equals(newsItem.uuid)){
                                    DigestStatus digestStatus = new DigestStatus();
                                    digestStatus.uuid = newsItem.uuid;
                                    digestStatus.isChecked = digests.get(i).isChecked;
                                    foot_list.add(digestStatus);
                                }
                            }
                        }
                        subscriber.onNext(mNewsDigest);
                        subscriber.onCompleted();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NewsDigest>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(NewsDigest newsItems) {
                        if (adapter != null) {
                           // adapter.clear();
                            foot_list.clear();
                            for (NewsDigest.NewsItem item : newsItems.items) {
                               for(int i = 0; i<digests.size();i++){
                                   if(digests.get(i).uuid.equals(item.uuid)){
                                       DigestStatus digestStatus = new DigestStatus();
                                       digestStatus.uuid = item.uuid;
                                       digestStatus.isChecked = digests.get(i).isChecked;
                                       foot_list.add(digestStatus);
                                   }
                               }
                            }
                        }
                        addFooterView(foot_list);
                    }
                });
    }

    private void moreDigest() {

        Intent intent = new Intent(getContext(), MoreDigestActivity.class);
        intent.putExtra("fragment", TAG);
        startActivityForResult(intent, 3);
    }

    private void setting() {
        Intent intent = new Intent(getContext(), SettingActivity.class);
        intent.putExtra("fragment", TAG);
        startActivityForResult(intent, 1);
    }
    private void sendEmail() {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        String[] addresses = {"yifanfeng@outlook.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        String subject = "Please replace this text with your issues";
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Your Phone does not install email application.", Toast.LENGTH_SHORT).show();
        }

    }

    private void addFooterView(ArrayList<DigestStatus> items) {
        View footer = LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.news_list_footer_view, recyclerView, false);
        adapter.setFooterView(footer);
        View toggleButton = adapter.getFooterView().findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraNews();
            }
        });

        initFooterView = true;
    }

    private void updateFooterView() {
        if (adapter != null) {
        } else {

        }

    }

    /**
     * Read more n
     */
    public void extraNews() {
        Intent intent = new Intent(getContext(), ExtraNewsListActivity.class);
        intent.putExtra(ExtraNewsListActivity.ALL_CHECKED, adapter.isAllChecked());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.move_in, R.anim.move_out);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x110 && resultCode == 0x110) {
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    extraNews();
                }
            }, 300);
        }
        if (resultCode == 2){
            adapter.notifyDataSetChanged();
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(lastPosition, lastOffset);
        }
    }

}
