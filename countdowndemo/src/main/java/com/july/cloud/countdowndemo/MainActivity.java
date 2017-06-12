package com.july.cloud.countdowndemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv_countdown = (TextView) findViewById(R.id.tv_countdown);
        tv_countdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoreDigestDialog dialog = new MoreDigestDialog();
                dialog.show(getSupportFragmentManager(), "loginDialog");
            }
        });
    }
}
