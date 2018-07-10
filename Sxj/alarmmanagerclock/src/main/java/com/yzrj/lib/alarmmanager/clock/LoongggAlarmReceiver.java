package com.yzrj.lib.alarmmanager.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by loongggdroid on 2016/3/21.
 */
public class LoongggAlarmReceiver extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        try {
            Log.e("sssssssssssssssss", "onReceive:定时任务开始 ");
            int id = intent.getIntExtra("id", -1000);

            long clocktime = intent.getLongExtra("clocktime", -1000L);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar clocktimecalendar = Calendar.getInstance();
            clocktimecalendar.setTimeInMillis(clocktime);
            Calendar calendar = Calendar.getInstance();
            String t1 = format.format(clocktimecalendar.getTime());
            String t2 = format.format(calendar.getTime());

            Log.e("sssssssssssssssss", "onReceive:id=" + id);
            if (id != -1000) {
                Log.e("sssssssssssssssss", "onReceive:闹钟响了 ");
                String msg = intent.getStringExtra("msg");
                long intervalMillis = intent.getLongExtra("intervalMillis", 0);
                if (intervalMillis != 0) {
                    AlarmManagerUtil.setAlarmTime(context, System.currentTimeMillis() + intervalMillis,
                            intent);
                }
                if (t1.equals(t2)) {
                    int flag = intent.getIntExtra("soundOrVibrator", 0);
                    Intent clockIntent = new Intent("com.yzyj.alarm.clockclarmactivity");
//            Intent clockIntent = new Intent(context, ClockAlarmActivity.class);
                    clockIntent.putExtra("msg", msg);
                    clockIntent.putExtra("flag", flag);
                    clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(clockIntent);
                }
            }
        } catch (Exception e) {
            Log.e("sssssssssssssssss", "onReceive:闹钟 ", e);
        }
    }


}
