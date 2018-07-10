package com.yzrj.app.suixinji.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yzrj.app.suixinji.R;
import com.yzrj.app.suixinji.bean.TianQiBean;
import com.yzrj.app.suixinji.utils.AppUtils;
import com.yzrj.app.suixinji.utils.AsyncHttpClientUtil;
import com.yzrj.app.suixinji.utils.DbServices;
import com.yzrj.app.suixinji.utils.GsonUtil;
import com.yzrj.app.suixinji.utils.LocationUtils;
import com.yzrj.app.suixinji.utils.RequestPermissions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import test.greenDAO.bean.Duty;

public class RiJiAddDialogFrament extends DialogFragment {

    TextView add_diary_tv_city;
    private EditText add_diary_et_title;
    private EditText add_diary_et_content;

    //创建接口在Acitvity中调用
    public interface AddDutyInputListener {
        void onAddDutyInputComplete(Duty duty);
    }

    RequestPermissions.PermissionCallBack permissionCallBack2001 = new RequestPermissions.PermissionCallBack() {
        @Override
        public void setOnPermissionListener(Boolean bo) {
            if (bo) {
                add_diary_tv_city.setText("刷新中");
                //城市
                LocationUtils.getCNBylocation(getActivity(), handler);
            } else {
                Toast.makeText(getActivity(), "" + "没有定位权限，请授权后再操作", Toast.LENGTH_SHORT).show();
            }
        }
    };
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    SharedPreferences preferences = getActivity().getSharedPreferences("weathercity", getActivity().MODE_PRIVATE);
                    String city = preferences.getString("city", "");
                    if (!TextUtils.isEmpty(LocationUtils.cityName) &&
                            !city.equals(LocationUtils.cityName)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("city", LocationUtils.cityName);
                        editor.putString("sheng", LocationUtils.adminArea);
                        editor.commit();
                    }
                    city = preferences.getString("city", "");
                    add_diary_tv_city.setText(city + " 天气刷新中");
                    tq();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    String citys = "";

    public void tq() {
        try {
            SharedPreferences preferences = getActivity().getSharedPreferences("weathercity", getActivity().MODE_PRIVATE);
            citys = preferences.getString("city", "");
            Log.e("sssssssssssssssssssss", "tq:city " + citys);
            AsyncHttpClientUtil.getInstance().get(AppUtils.url_tq + citys, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String json = new String(responseBody);
                        Log.e("ssssssssssssssssss", json);
                        TianQiBean baseBean = GsonUtil.buildGson().fromJson(json, TianQiBean.class);
                        SharedPreferences preferences = getActivity().getSharedPreferences("weathercity", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        String weather = baseBean.data.forecast.get(0).type;
                        editor.putString("weather", weather);
                        editor.commit();
                        add_diary_tv_city.setText(citys + " " + weather);
                        return;
                    } catch (Exception e) {
                        Log.e("sssssssssssssssssssss", "tq2: ", e);
                        try {
                            add_diary_tv_city.setText(citys + " 天气刷新失败");
                        } catch (Exception e1) {
                            Log.e("sssssssssssssssssssss", "tq2: ", e1);
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e("sssssssssssssssssssss", "tqonFailure: " + statusCode);
                    try {
                        add_diary_tv_city.setText(citys + " 天气刷新失败");
                    } catch (Exception e) {
                        Log.e("sssssssssssssssssssss", "tq2: ", e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("sssssssssssssssssssss", "tq: ", e);
            try {
                add_diary_tv_city.setText(citys + " 天气刷新失败");
            } catch (Exception e1) {
                Log.e("sssssssssssssssssssss", "tq2: ", e1);
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rijiadd, null);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String now = sdf.format(new Date());
        TextView add_diary_tv_date = (TextView) view.findViewById(R.id.add_diary_tv_date);
        add_diary_tv_date.setText("今天，" + now);

        add_diary_et_title = (EditText) view.findViewById(R.id.add_diary_et_title);
        add_diary_et_content = (EditText) view.findViewById(R.id.add_diary_et_content);

        add_diary_tv_city = (TextView) view.findViewById(R.id.add_diary_tv_city);
        SharedPreferences preferences = getActivity().getSharedPreferences("weathercity", getActivity().MODE_PRIVATE);
        final String city = preferences.getString("city", "定位失败请刷新");
        final String weather = preferences.getString("weather", "阴天");
        add_diary_tv_city.setText(city + " " + weather);

        ImageButton refresh = (ImageButton) view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestPermissions.diweiExternalStorage(getActivity(), permissionCallBack2001, 2001);
            }
        });


        builder.setView(view)
                .setTitle("写日记").setPositiveButton("保存",
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
                        if (add_diary_et_title.getText().toString().trim().isEmpty()) {
                            Toast.makeText(getActivity(), "标题不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (add_diary_et_content.getText().toString().trim().isEmpty()) {
                            Toast.makeText(getActivity(), "内容不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Duty newduty = new Duty(null, add_diary_et_title.getText().toString(), add_diary_et_content.getText().toString(),
                                AppUtils.typerj, false, new Date(), 0L, 0, 0, city, weather);
                        DbServices.getInstance(getActivity()).saveNote(newduty);


                        DaibanDetailFrame.AddDutyInputListener listener =
                                (DaibanDetailFrame.AddDutyInputListener) getActivity();
                        // listener.onAddDutyInputComplete(et_title.getText().toString(), bp_model.getSelectedItem().toString(), et_info.getText().toString());
                        listener.onAddDutyInputComplete(newduty);
                        Log.e("sssssssssssss", "onClick: " + newduty.getId());
                        dia.dismiss();
                    }
                });
            }
        });
        return dia;
    }


}
