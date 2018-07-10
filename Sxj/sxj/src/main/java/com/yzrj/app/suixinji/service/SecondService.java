package com.yzrj.app.suixinji.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.yzrj.app.suixinji.R;
import com.yzrj.app.suixinji.service.aidl.ProcessService;

public class SecondService extends Service {

    private MyBinder binder;
    private MyConn conn;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        binder = new MyBinder();
        if (conn == null)
            conn = new MyConn();
//        Notification notification = new Notification();
//        notification.flags = Notification.FLAG_ONGOING_EVENT;
//        notification.flags |= Notification.FLAG_NO_CLEAR;
//        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
//        startForeground(1, notification);

        postDelayed();
    }

    // 用于判断进程是否运行
    private String Process_Name = "com.yzrj.app.suixinji:service1";

    @Override
    public void onTrimMemory(int level) {
        Log.e("sssssssssssssfirst", "onTrimMemory");
//        boolean isRun = ServiceUtils.isProessRunning(this, Process_Name);
//        if (isRun == false) {
//            try {
//                Log.e("sssssssssssssSecond", "onServiceDisconnected:FirstService被杀死 SecondService重启FirstService");
//                // 启动FirstService
//                SecondService.this.startService(new Intent(SecondService.this, FirstService.class));
//                //绑定FirstService
//                SecondService.this.bindService(new Intent(SecondService.this, FirstService.class), conn, Context.BIND_IMPORTANT);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("sssssssssssssSecond", "onStartCommand: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        } else {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            startForeground(250, builder.build());
            startService(new Intent(this, ThirdService.class));
        }
        SecondService.this.bindService(new Intent(this, FirstService.class), conn, Context.BIND_IMPORTANT);

        return START_STICKY;//super.onStartCommand(intent, flags, startId);
    }

    public void postDelayed() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Log.e("sssssssssssssSecond", "我还在2 ");
                postDelayed();
            }
        }, 60 * 1000);
    }

    class MyBinder extends ProcessService.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return "I am SecondService";
        }
    }

    class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("sssssssssssssSecond", "onServiceConnected：与FirtService连接成功" + SecondService.this.getApplicationInfo().processName);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("sssssssssssssSecond", "onServiceDisconnected:FirstService被杀死 SecondService重启FirstService");
            // 启动FirstService
            SecondService.this.startService(new Intent(SecondService.this, FirstService.class));
            //绑定FirstService
            SecondService.this.bindService(new Intent(SecondService.this, FirstService.class), conn, Context.BIND_IMPORTANT);
        }
    }

    @Override
    public void onDestroy() {
        Log.e("sssssssssssssSecond", "onDestroy：SecondService关闭");
        // 启动SecondService
        SecondService.this.startService(new Intent(SecondService.this, SecondService.class));
        super.onDestroy();
    }
}
