package com.yzrj.app.suixinji.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yzrj.app.suixinji.R;
import com.yzrj.app.suixinji.fragments.DaibanDetailFrame;
import com.yzrj.app.suixinji.fragments.JinianriDialogFrament;
import com.yzrj.app.suixinji.utils.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import test.greenDAO.bean.Duty;

/**
 * Created by castl on 2016/5/21.
 */
public class DutyInfoDialogFragment extends DialogFragment {

    private TextView duty_info_title;
    private TextView duty_info_type;
    private TextView duty_info_content;
    private TextView duty_info_date;
    private TextView duty_info_state;

    public static final String DUTY = "DUTY";
    private Duty myduty;

    public DutyInfoDialogFragment newInstance(Duty duty) {
        Bundle args = new Bundle();
        args.putSerializable(DUTY, duty);
        DutyInfoDialogFragment infoFragment = new DutyInfoDialogFragment();
        infoFragment.setArguments(args);
        return infoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myduty = (Duty) getArguments().getSerializable(DUTY);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_dutyinfo, null);
        duty_info_title = (TextView) view.findViewById(R.id.duty_info_title);
        duty_info_type = (TextView) view.findViewById(R.id.duty_info_type);
        duty_info_content = (TextView) view.findViewById(R.id.duty_info_content);
        duty_info_date = (TextView) view.findViewById(R.id.duty_info_date);
        duty_info_state = (TextView) view.findViewById(R.id.duty_info_state);
        TextView duty_sj = (TextView) view.findViewById(R.id.duty_sj);
        TextView duty_info_txfs = (TextView) view.findViewById(R.id.duty_info_txfs);

        duty_info_title.setText(myduty.getTitle());
        duty_info_type.setText(myduty.getType());


        SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(myduty.getClocktime());
        String t2 = format2.format(calendar.getTime());
        duty_sj.setText(t2);
        String s = "";
        if (AppUtils.typedb.equals(myduty.getType())) {
            s = "提前" + myduty.getClocktqtime() + "分钟";
        } else {
            s = "提前" + myduty.getClocktqtime() + "天";
        }
        duty_info_txfs.setText(AppUtils.ITEMS[myduty.getClockfanshi()] + " " + s);

        duty_info_content.setText(myduty.getInfo());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = myduty.getDate();
        String t1 = format.format(d1);
        duty_info_date.setText(t1);

        View duty_info_linzt = view.findViewById(R.id.duty_info_linzt);
        if (AppUtils.typedb.equals(myduty.getType())) {
            duty_info_linzt.setVisibility(View.VISIBLE);
            String state = myduty.getStatus() ? "已完成" : "未完成";
            duty_info_state.setText(state);
        } else {
            duty_info_linzt.setVisibility(View.GONE);
        }

        builder.setView(view)
                .setPositiveButton("修改",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (AppUtils.typedb.equals(myduty.getType())) {
                                    DaibanDetailFrame adddialog = DaibanDetailFrame.newInstance(myduty);
                                    adddialog.show(getActivity().getFragmentManager(), "xgdaibanDialog");
                                } else {
                                    JinianriDialogFrament adddialog = JinianriDialogFrament.newInstance(myduty);
                                    adddialog.show(getActivity().getFragmentManager(), "xgjnrDialog");
                                }
                            }
                        }).setNegativeButton("关闭", null);
        return builder.create();
    }


}
