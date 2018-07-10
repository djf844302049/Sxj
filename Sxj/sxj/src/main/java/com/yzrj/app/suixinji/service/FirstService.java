package com.yzrj.app.suixinji.service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.coolerfall.daemon.Daemon;
import com.yzrj.app.suixinji.R;
import com.yzrj.app.suixinji.utils.AppUtils;
import com.yzrj.app.suixinji.utils.DbServices;
import com.yzrj.app.suixinji.utils.EventReminderUtils;
import com.yzrj.lib.alarmmanager.clock.AlarmManagerUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import test.greenDAO.bean.Duty;

public class FirstService extends Service {
    public static FirstService service;
    private MyConn conn;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return myBinder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        service = this;
        if (conn == null)
            conn = new MyConn();
        Log.e("sssssssssssssfirst", "onCreate: ");
        Daemon.run(FirstService.this, FirstService.class, Daemon.INTERVAL_ONE_MINUTE);

        grayGuard();

//        Notification notification = new Notification();
//        notification.flags = Notification.FLAG_ONGOING_EVENT;
//        notification.flags |= Notification.FLAG_NO_CLEAR;
//        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
//        startForeground(1, notification);

        postDelayed();
    }
//    在这里利用5.0以上的JobScheduler创建一个定时的任务，定时检测闹钟服务是否存在，没在存在则重新启动闹钟服务。（这里我设置每一分钟检测一次闹钟服务）
//    private void jobScheduler() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,
//                    new ComponentName(getPackageName(), JobSchedulerService.class.getName()));
//
//            builder.setPeriodic(60 * 1000); //每隔60秒运行一次
//            builder.setRequiresCharging(true);
//            builder.setPersisted(true);  //设置设备重启后，是否重新执行任务
//            builder.setRequiresDeviceIdle(true);
//
//            if (mJobScheduler.schedule(builder.build()) <= 0) {
//                //If something goes wrong
//            }
//        }
//    }

    private final static int GRAY_SERVICE_ID = 1001;

    //    灰色保活手段
    private void grayGuard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        } else if (Build.VERSION.SDK_INT >= 18) {
            Intent innerIntent = new Intent(this, DaemonInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else if (Build.VERSION.SDK_INT < 18) {
            //API < 18 ，此方法能有效隐藏Notification上的图标
            startForeground(GRAY_SERVICE_ID, new Notification());
        }

        //发送唤醒广播来促使挂掉的UI进程重新启动起来
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent();
        alarmIntent.setAction("com.yzyj.app.GrayGuardReceiver");
        PendingIntent operation = PendingIntent.getBroadcast(this,
                10001, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    operation);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setWindow(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), 0, operation);
        } else {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), 0, operation);
        }
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class DaemonInnerService extends Service {
        @Override
        public void onCreate() {
            Log.i("sssssssssssss", "InnerService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i("sssssssssssss", "InnerService -> onStartCommand");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            } else {
                startForeground(GRAY_SERVICE_ID, new Notification());
            }
            //stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
            Log.i("sssssssssssss", "InnerService -> onDestroy");
            super.onDestroy();
        }
    }

    public void resetAlarm() {
        Log.e("sssssssssssssfirst", "resetAlarm:查询数据库闹钟 ");
        List<Duty> datas = DbServices.getInstance(this).queryNote(AppUtils.typedb);
        for (Duty duty : datas) {
            setClock(duty);
        }
    }

    public static int getDutyid(Duty duty) {
        int id = 0;
        if (AppUtils.typedb.equals(duty.getType())) {
            id = Integer.parseInt("1000" + duty.getId() + "");
        } else if (AppUtils.typejnr.equals(duty.getType())) {
            id = Integer.parseInt("2000" + duty.getId() + "");
        } else {
            id = Integer.parseInt("3000" + duty.getId() + "");
        }
        return id;
    }

    public void setClock(Duty duty) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Calendar nowcalendar = Calendar.getInstance();
        Calendar clockcalendar = Calendar.getInstance();
        clockcalendar.setTimeInMillis(duty.getClocktime());
        if (AppUtils.typejnr.equals(duty.getType())) {
            clockcalendar.add(Calendar.DAY_OF_MONTH, -duty.getClocktqtime());
        } else {
            clockcalendar.add(Calendar.MINUTE, -duty.getClocktqtime());
        }
        if (!duty.getStatus() && clockcalendar.getTimeInMillis() > nowcalendar.getTimeInMillis() - 1 * 60 * 1000 && clockcalendar.getTimeInMillis() < nowcalendar.getTimeInMillis() + 7 * 24 * 60 * 60 * 1000 - 1 * 60 * 1000) {
            EventReminderUtils.calendarEvent(this, duty.getType(), duty.getTitle(), duty.getInfo(), duty.getClocktime(), duty.getClockfanshi(), duty.getClocktqtime());
            try {
                Log.e("sssssssssssssfirst", "开始设置闹钟");
                int[] weeks = new int[]{7, 1, 2, 3, 4, 5, 6};

                AlarmManagerUtil.setAlarm(this, clockcalendar.getTimeInMillis(),
                        2, getDutyid(duty),
                        weeks[clockcalendar.get(Calendar.DAY_OF_WEEK) - 1],
                        duty.getTitle() + "\n" + format.format(duty.getClocktime()) + "\n\n" + duty.getInfo(),
                        duty.getClockfanshi());
                Log.e("sssssssssssssfirst", "设置闹钟完成" + format.format(clockcalendar.getTime()));
            } catch (Exception e) {
                Log.e("sssssssssssssfirst", "设置失败: ", e);
            }
        } else if (clockcalendar.getTimeInMillis() < nowcalendar.getTimeInMillis() - 10 * 60 * 1000) {
            deleteClock(this, duty);
        }
    }

    public static void deleteClock(Context context, Duty duty) {
        Log.e("sssssssssssssfirst", "开始取消无用闹钟");
        try {
            EventReminderUtils.deleteCalendarEvent(context, duty.getTitle(), duty.getInfo(), duty.getClocktime());
        } catch (Exception e) {
            Log.e("sssssssssssssfirst", "取消日历事件失败: ", e);
        }
        try {
            AlarmManagerUtil.cancelAlarm(context, AlarmManagerUtil.ALARM_ACTION, getDutyid(duty));
            Log.e("sssssssssssssfirst", "取消闹钟完成");
        } catch (Exception e) {
            Log.e("sssssssssssssfirst", "取消闹钟失败: ", e);
        }
    }

    // 用于判断进程是否运行
    private String Process_Name = "com.yzrj.app.suixinji:service2";

    @Override
    public void onTrimMemory(int level) {
        Log.e("sssssssssssssfirst", "onTrimMemory");
//        boolean isRun = ServiceUtils.isProessRunning(this, Process_Name);
//        if (isRun == false) {
//            try {
//                // 启动FirstService
//                FirstService.this.startService(new Intent(FirstService.this, SecondService.class));
//                //绑定FirstService
//                FirstService.this.bindService(new Intent(FirstService.this, SecondService.class), conn, Context.BIND_IMPORTANT);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("sssssssssssssfirst", "onStartCommand: ");
        service = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        } else {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ico);
            startForeground(250, builder.build());
            startService(new Intent(this, ThirdService.class));
        }
        FirstService.this.bindService(new Intent(this, SecondService.class), conn, Context.BIND_IMPORTANT);

        Log.e("sssssssssssssfirst", "onStartCommand:设置闹钟 ");
        resetAlarm();


        return START_STICKY;
    }

    public void postDelayed() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Log.e("sssssssssssssfirst", "我还在 ");
                postDelayed();
            }
        }, 60 * 1000);
    }

    private final IBinder myBinder = new MyBinder();

    public class MyBinder extends Binder {
        public FirstService getService() {
            return FirstService.this;
        }
    }


    class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("sssssssssssssfirst", "onServiceConnected：与SecondService连接成功");
            ActivityManager activityManager = (ActivityManager) FirstService.this
                    .getSystemService(Context.ACTIVITY_SERVICE);


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("sssssssssssssfirst", "onServiceDisconnected:SecondService被杀死 FirstService重启SecondService");
            // 启动FirstService
            FirstService.this.startService(new Intent(FirstService.this, SecondService.class));
            //绑定FirstService
            FirstService.this.bindService(new Intent(FirstService.this, SecondService.class), conn, Context.BIND_IMPORTANT);
        }
    }

    @Override
    public void onDestroy() {
        Log.e("sssssssssssssfirst", "onDestroy：FirstService关闭");
        // 启动FirstService
        FirstService.this.startService(new Intent(FirstService.this, FirstService.class));

        super.onDestroy();
    }
}
