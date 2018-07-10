package com.yzrj.app.suixinji.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yzrj.app.suixinji.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import test.greenDAO.bean.Duty;

/**
 * Created by castl on 2016/5/20.
 */
public class RiJiAdapter extends BaseQuickAdapter<Duty> {


    public RiJiAdapter(Context context, List<Duty> dataSize) {
        super(context, R.layout.item_riji, dataSize);

    }

    @Override
    protected void convert(BaseViewHolder helper, Duty item) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(item.getDate().getTime());
        String t1 = format.format(calendar.getTime());
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        helper.setText(R.id.tv_date, "" + t1)
                .setText(R.id.tv_date2, "" + weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1])
                .setText(R.id.tv_title, "" + item.getTitle())
                .setText(R.id.tv_content, "" + item.getInfo())
                .setText(R.id.tv_city, "" + item.getCity())
                .setText(R.id.tv_w, "" + item.getWeather());
    }
}