package com.yzrj.app.suixinji.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by admin on 2018/6/26.
 */

public class UserPresentReceiver extends BroadcastReceiver {

    private static final String TAG = "UserPresentReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.e("sssssssssssssssssss", "UserPresentReceiver:" + intent.getAction());
        // do something
        context.startService(new Intent(context, FirstService.class));//启动闹钟服务
        context.startService(new Intent(context, SecondService.class));

    }
}