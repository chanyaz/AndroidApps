package com.shenke.digest.core;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.shenke.digest.R;
import com.shenke.digest.fragment.ExtraNewsListFragment;
import com.shenke.digest.util.StatusBarCompat;

public class ExtraNewsListActivity extends AppCompatActivity {
    public static final String ALL_CHECKED = "all_checked";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.showSystemUI(this);
        setContentView(R.layout.extra_news_activity);
        boolean allChecked = getIntent().getBooleanExtra(ALL_CHECKED, false);
        Fragment fragment = new ExtraNewsListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ALL_CHECKED, allChecked);
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment, "extraList")
                .commit();

    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.move_in1, R.anim.move_out1);
    }
}
