package com.yzrj.app.suixinji.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yzrj.app.suixinji.R;
import com.yzrj.app.suixinji.utils.AppUtils;
import com.yzrj.app.suixinji.utils.DbServices;
import com.yzrj.app.suixinji.utils.EventReminderUtils;
import com.yzrj.app.suixinji.widget.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;
import test.greenDAO.bean.Duty;

public class DaibanDetailFrame extends DialogFragment {


    private EditText et_title;
    private EditText et_info;

    private MaterialSpinner bp_model, bp_time;

    private long mAlarmsTime = 0;

    CustomDatePicker customDatePicker;
    Duty duty;

    public static DaibanDetailFrame newInstance(Duty duty) {
        Bundle args = new Bundle();
        args.putSerializable("duty", duty);
        DaibanDetailFrame fragment = new DaibanDetailFrame();
        fragment.setArguments(args);
        return fragment;
    }

    //创建接口在Acitvity中调用
    public interface AddDutyInputListener {
        void onAddDutyInputComplete(Duty duty);
    }

    String[] ITEMS = {"震动", "铃声", "震动+铃声"};
    String[] ITEMS0 = {"准时", "提前5分钟", "提前10分钟", "提前15分钟"};
    int[] times = {0, 5, 10, 15};
    int model = -1;
    int time = -1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            duty = (Duty) getArguments().getSerializable("duty");
        } catch (Exception e) {

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapter0 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, ITEMS0);
        adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_daiban_detail, null);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DAY_OF_MONTH, 6);
        String end = sdf.format(endCalendar.getTime());
        customDatePicker = new CustomDatePicker(getActivity(), view, now, end);

        TextView tv_1 = (TextView) view.findViewById(R.id.tv_1);
        et_title = (EditText) view.findViewById(R.id.et_title);
        et_info = (EditText) view.findViewById(R.id.et_info);
        bp_model = (MaterialSpinner) view.findViewById(R.id.spinner_model);
        bp_model.setAdapter(adapter);
        bp_model.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model = position;
                Log.e("sssssssssss", "onItemSelected:model " + model);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                model = -1;
            }
        });
        bp_time = (MaterialSpinner) view.findViewById(R.id.spinner_time);
        bp_time.setAdapter(adapter0);
        bp_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                time = position;
                Log.e("sssssssssss", "onItemSelected: time" + time);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                time = -1;
            }
        });


        if (duty != null) {
            tv_1.setText("修改");
            Calendar calendar = Calendar.getInstance();
            if (duty.getClocktime() > calendar.getTimeInMillis()) {
                customDatePicker.show(sdf.format(duty.getClocktime()));
            } else {
                customDatePicker.show(now);
            }
            et_title.setText(duty.getTitle());
            et_info.setText(duty.getInfo());
            bp_model.setSelection(duty.getClockfanshi(), true);
            for (int i = 0; i < times.length; i++) {
                if (times[i] == duty.getClocktqtime()) {
                    bp_time.setSelection(i, true);
                }
            }
        } else {
            tv_1.setText("新增");
            customDatePicker.show(now);
            bp_model.setSelection(0, true);
            bp_time.setSelection(0, true);
        }


        builder.setView(view)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {


                            }

                        }).setNegativeButton("取消", null);
        final AlertDialog dia = builder.create();
        dia.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnPositive = dia.getButton(DialogInterface.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (et_title.getText().toString().trim().isEmpty()) {
                            Toast.makeText(getActivity(), "标题不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (model == -1) {
                            Toast.makeText(getActivity(), "请选择闹钟模式", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (time == -1) {
                            Toast.makeText(getActivity(), "请选择提醒设置", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (duty != null) {
                            Duty newduty = new Duty(duty.getId(), et_title.getText().toString(), et_info.getText().toString(),
                                    AppUtils.typedb, false, new Date(), customDatePicker.getTimeInMillis(), model, times[time], "", "");
                            EventReminderUtils.deleteCalendarEvent(getActivity(), duty.getTitle(), duty.getInfo(), duty.getClocktime());
                            EventReminderUtils.calendarEvent(getActivity(), newduty.getType(), newduty.getTitle(), newduty.getInfo(), newduty.getClocktime(), newduty.getClockfanshi(), newduty.getClocktqtime());

                            DbServices.getInstance(getActivity()).saveNote(newduty);
                            DaibanDetailFrame.AddDutyInputListener listener =
                                    (DaibanDetailFrame.AddDutyInputListener) getActivity();
                            // listener.onAddDutyInputComplete(et_title.getText().toString(), bp_model.getSelectedItem().toString(), et_info.getText().toString());
                            listener.onAddDutyInputComplete(newduty);
                        } else {
                            Duty newduty = new Duty(null, et_title.getText().toString(), et_info.getText().toString(),
                                    AppUtils.typedb, false, new Date(), customDatePicker.getTimeInMillis(), model, times[time], "", "");

                            EventReminderUtils.calendarEvent(getActivity(), newduty.getType(), newduty.getTitle(), newduty.getInfo(), newduty.getClocktime(), newduty.getClockfanshi(), newduty.getClocktqtime());

                            DbServices.getInstance(getActivity()).saveNote(newduty);
                            DaibanDetailFrame.AddDutyInputListener listener =
                                    (DaibanDetailFrame.AddDutyInputListener) getActivity();
                            // listener.onAddDutyInputComplete(et_title.getText().toString(), bp_model.getSelectedItem().toString(), et_info.getText().toString());
                            listener.onAddDutyInputComplete(newduty);
                        }

                        dia.dismiss();
                    }
                });
            }
        });
        return dia;

    }


}
