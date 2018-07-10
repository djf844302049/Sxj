package com.yzrj.app.suixinji.adapter;

import android.content.Context;
import android.widget.TextView;

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
public class JinianriAdapter extends BaseQuickAdapter<Duty> {


    public JinianriAdapter(Context context, List<Duty> dataSize) {
        super(context, R.layout.item_jinianri, dataSize);

    }

    @Override
    protected void convert(BaseViewHolder helper, Duty item) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.setTimeInMillis(item.getClocktime());
        String t1 = format.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -item.getClocktqtime());
        String t2 = format.format(calendar.getTime());

        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        helper.setText(R.id.tv_date, "" + t1)
                .setText(R.id.tv_date2, "" + weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1])
                .setText(R.id.tv_title, "" + item.getTitle())
                .setText(R.id.tv_content, "" + item.getInfo())
                .setText(R.id.item_date, "" + t2);


        ((TextView) helper.getView(R.id.tv_title)).setTextColor(0xff3020e2);
        if (item.getClocktime() < now) {
            ((TextView) helper.getView(R.id.tv_title)).setTextColor(0xff666666);
        }
    }
}