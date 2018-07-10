package com.yzrj.app.suixinji.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;


/**
 * Created by admin on 2016/8/18.
 */
public class RequestPermissions {
    public static int REQUEST_EXTERNAL_STORAGE = 1;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void SET_ALARM(final Context mContext, PermissionCallBack mCallBack, int requestCode) {
        int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.SET_ALARM);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                        (Activity) mContext,
                        new String[]{Manifest.permission.SET_ALARM},
                        requestCode);
                mCallBack.setOnPermissionListener(false);
            } else {
                mCallBack.setOnPermissionListener(false);
            }
        } else {
            mCallBack.setOnPermissionListener(true);
        }
    }

    public static void READ_CALENDARandWRITE_CALENDAR(final Fragment fragment, PermissionCallBack mCallBack, int requestCode) {
        int permission = ActivityCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.READ_CALENDAR);
        int permission2 = ActivityCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.WRITE_CALENDAR);
        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.requestPermissions(
                        new String[]{Manifest.permission.READ_CALENDAR,
                                Manifest.permission.WRITE_CALENDAR},
                        requestCode);
                mCallBack.setOnPermissionListener(false);
            } else {
                mCallBack.setOnPermissionListener(false);
            }
        } else {
            mCallBack.setOnPermissionListener(true);
        }
    }

    public static void READ_CALENDARandWRITE_CALENDAR(final Context mContext, PermissionCallBack mCallBack, int requestCode) {
        int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR);
        int permission2 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR);
        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                        (Activity) mContext,
                        new String[]{Manifest.permission.READ_CALENDAR,
                                Manifest.permission.WRITE_CALENDAR},
                        requestCode);
                mCallBack.setOnPermissionListener(false);
            } else {
                mCallBack.setOnPermissionListener(false);
            }
        } else {
            mCallBack.setOnPermissionListener(true);
        }
    }

    /**
     * 读写权限申请 true 有权限  false 无权限
     *
     * @param mContext
     */
    public static void writeExternalStorage(final Context mContext, PermissionCallBack mCallBack, int requestCode) {
        int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                        (Activity) mContext,
                        PERMISSIONS_STORAGE,
                        requestCode);
                mCallBack.setOnPermissionListener(false);
            } else {
                mCallBack.setOnPermissionListener(false);
            }
        } else {
            mCallBack.setOnPermissionListener(true);
        }
    }

    /**
     * 读写权限、相机申请 true 有权限  false 无权限
     *
     * @param mContext
     */
    public static void writeExternalStorageandCAMERA(final Context mContext, PermissionCallBack mCallBack, int requestCode) {
        int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                        (Activity) mContext,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        requestCode);
                mCallBack.setOnPermissionListener(false);
            } else {
                mCallBack.setOnPermissionListener(false);
            }
        } else {
            mCallBack.setOnPermissionListener(true);
        }
    }

    public static void CAMERA(final Context mContext, PermissionCallBack mCallBack, int requestCode) {
        int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                        (Activity) mContext,
                        new String[]{Manifest.permission.CAMERA},
                        requestCode);
                mCallBack.setOnPermissionListener(false);
            } else {
                mCallBack.setOnPermissionListener(false);
            }
        } else {
            mCallBack.setOnPermissionListener(true);
        }
    }


    /**
     * 定位权限申请 true 有权限  false 无权限
     *
     * @param mContext
     */
    public static void diweiExternalStorage(final Context mContext, PermissionCallBack mCallBack, int requestCode) {
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission3 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED
                || permission3 != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, requestCode);
                mCallBack.setOnPermissionListener(false);
            } else {
                mCallBack.setOnPermissionListener(false);
            }
        } else {
            mCallBack.setOnPermissionListener(true);
        }
    }

    /**
     * 权限申请回调，需要在你调用的Activity中onRequestPermissionsResult调用该方法   true 申请权限成功  false申请权限失败
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionCallBack mCallBack) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCallBack.setOnPermissionListener(true);
            } else {
                mCallBack.setOnPermissionListener(false);
            }
            return;
        }
    }

    /**
     * 6.0以下申请打开权限管理
     */
//    public static void openPre(final Context mContext) {
//        RemindDialogUtil.showRemindDialog(mContext, "當前無權限，將無法進行應用升級~,是否打开设置？", new RemindDialogUtil.DialogCallBack() {
//            @Override
//            public void clickYes() {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
//                mContext.startActivity(intent);
//                RemindDialogUtil.hideRemindDialog();
//            }
//
//            @Override
//            public void clickCancel() {
//                RemindDialogUtil.hideRemindDialog();
//            }
//        });
//    }

    public interface PermissionCallBack {
        void setOnPermissionListener(Boolean bo);
    }


}
