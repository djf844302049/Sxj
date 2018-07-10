package com.yzrj.app.suixinji.adapter;

import android.content.Context;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yzrj.app.suixinji.R;
import com.yzrj.app.suixinji.utils.SmoothCheckBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import test.greenDAO.bean.Duty;

/**
 * Created by castl on 2016/5/20.
 */
public class QuickAdapter extends BaseQuickAdapter<Duty> {


    public QuickAdapter(Context context, List<Duty> dataSize) {
        super(context, R.layout.item_duty, dataSize);

    }

    @Override
    protected void convert(BaseViewHolder helper, Duty item) {
        SmoothCheckBox scb = helper.getView(R.id.scb);
        if (item.getStatus()) {//默认显示已完成
            scb.setChecked(true);
        } else {
            scb.setChecked(false);
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.setTimeInMillis(item.getClocktime());
        String t1 = format.format(calendar.getTime());
        helper.setText(R.id.item_title, item.getTitle())
                .setText(R.id.item_info, "内容：" + item.getInfo())
                .setText(R.id.item_date, "" + t1)
                .setOnClickListener(R.id.scb, new OnItemChildClickListener());

        ((TextView) helper.getView(R.id.item_title1)).setTextColor(0xff5156fc);
        ((TextView) helper.getView(R.id.item_title)).setTextColor(0x8ce61f6e);
        if (item.getClocktime() < now) {
            ((TextView) helper.getView(R.id.item_title1)).setTextColor(0xff666666);
            ((TextView) helper.getView(R.id.item_title)).setTextColor(0xff666666);
        }
    }
}