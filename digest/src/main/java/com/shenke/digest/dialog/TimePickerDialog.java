package com.shenke.digest.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.shenke.digest.R;
import com.shenke.digest.view.RulerView;

/**
 * Created by Cloud9 on 2016/11/30.
 */

public class TimePickerDialog extends DialogFragment implements CompoundButton.OnCheckedChangeListener {
    public static final String AM = "am";
    public static final String PM = "pm";
    private TextView tv_morning, tv_pushtime_morning, tv_evening, tv_pushtime_evening;
    private CheckBox checkBox_Morning, checkBox_Evening;
    private RulerView mRulerView_Moring, RulerView_Evening;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.timepicker_dialog, null);
        /**
         * Morning
         */
        tv_morning = (TextView) view.findViewById(R.id.tv_morning);
        tv_pushtime_morning = (TextView) view.findViewById(R.id.tv_pushtime_morning);
        checkBox_Morning = (CheckBox) view.findViewById(R.id.checkBox_Morning);
        checkBox_Morning.setOnCheckedChangeListener(this);
        mRulerView_Moring = (RulerView) view.findViewById(R.id.RulerView_Morning);
        mRulerView_Moring.mIndicatorTxtPaint.setColor(Color.BLACK);
        mRulerView_Moring.setStartValue(6);
        mRulerView_Moring.setEndValue(10);
        mRulerView_Moring.setOriginValue(8);
        mRulerView_Moring.setOriginValueSmall(0);
        mRulerView_Moring.setPartitionValue(2);
        mRulerView_Moring.setSmallPartitionCount(2);
        mRulerView_Moring.setmValue(8);
        mRulerView_Moring.setValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int intVal, int fltval) {
                //tv_pushtime_morning.setText(intVal + " " + fltval);
            }
        });
        /**
         * Evening
         */
        tv_evening = (TextView) view.findViewById(R.id.tv_evening);
        tv_pushtime_evening = (TextView) view.findViewById(R.id.tv_pushtime_evening);
        checkBox_Evening = (CheckBox) view.findViewById(R.id.checkBox_Evening);
        checkBox_Evening.setOnCheckedChangeListener(this);
        RulerView_Evening = (RulerView) view.findViewById(R.id.RulerView_Evening);
        builder.setView(view);
        return builder.create();
    }

    /**
     * CheckBox Listener
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(getResources().getColor(R.color.default_color1));
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(getResources().getColor(R.color.default_color1));
        paint2.setStyle(Paint.Style.FILL);
        paint2.setAntiAlias(true);//消除锯齿
        Paint paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3.setColor(Color.GRAY);
        Paint paint4 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint4.setColor(Color.GRAY);
        paint4.setStyle(Paint.Style.FILL_AND_STROKE);
        paint4.setAntiAlias(true);//消除锯齿

        switch (buttonView.getId()) {
            case R.id.checkBox_Morning:
                if (isChecked) {
                    mRulerView_Moring.setmBgPaint(paint1);
                    mRulerView_Moring.setmIndicatorViewPaint(paint2);
                } else {
                    mRulerView_Moring.setmBgPaint(paint3);
                    mRulerView_Moring.setmIndicatorViewPaint(paint4);
                }
                break;
            case R.id.checkBox_Evening:
                if (isChecked) {
                    RulerView_Evening.setmBgPaint(paint1);
                    RulerView_Evening.setmIndicatorViewPaint(paint2);
                } else {
                    RulerView_Evening.setmBgPaint(paint3);
                    RulerView_Evening.setmIndicatorViewPaint(paint4);
                }
                break;
        }
    }
}
