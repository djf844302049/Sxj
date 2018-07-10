package com.yzrj.app.suixinji;

import android.content.Context;

import org.litepal.LitePalApplication;

import test.greenDAO.dao.DaoMaster;
import test.greenDAO.dao.DaoSession;

/**
 * Created by castl on 2016/5/19.
 */
public class MyApplication extends LitePalApplication {


    private static MyApplication mInstance;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;


    public static String API_KEY = "19f7c5051b12a7c73b69251f59ba534f";

    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance == null)
            mInstance = this;
    }
    // 截取字符串中 图片的地址
    public static String GetBingImageUrl(String str) {
        String[] strArray = str.split("地址：");
        return strArray[1];
    }

    /**
     * 取得DaoMaster
     *
     * @param context 上下文
     * @return DaoMaster
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, "myDb", null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 取得DaoSession
     *
     * @param context 上下文
     * @return DaoSession
     */
    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
