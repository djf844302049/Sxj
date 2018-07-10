package com.yzrj.app.suixinji.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yzrj.app.suixinji.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import test.greenDAO.bean.Duty;

/**
 * Created by admin on 2018/6/22.
 */

public class RiJiDataActivity extends RxAppCompatActivity {
    public static void start(Context context, Duty duty) {
        Intent starter = new Intent(context, RiJiDataActivity.class);
        starter.putExtra("duty", duty);
        context.startActivity(starter);
    }

    Duty duty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rijidata);

        Intent intent = getIntent();
        duty = (Duty) intent.getSerializableExtra("duty");

        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(duty.getDate().getTime());
        String t1 = format.format(calendar.getTime());
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

        TextView tv_date = (TextView) findViewById(R.id.tv_date);
        TextView tv_city = (TextView) findViewById(R.id.tv_city);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        TextView tv_info = (TextView) findViewById(R.id.tv_info);
        tv_date.setText(t1 + " " + weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
        tv_city.setText(duty.getCity() + " " + duty.getWeather());
        tv_title.setText("" + duty.getTitle());
        tv_info.setText("" + duty.getInfo());

        ImageView common_iv_back = (ImageView) findViewById(R.id.common_iv_back);
        common_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
