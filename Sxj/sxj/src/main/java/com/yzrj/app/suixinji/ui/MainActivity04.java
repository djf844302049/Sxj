package com.yzrj.app.suixinji.ui;


import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yzrj.app.suixinji.R;
import com.yzrj.app.suixinji.RxBus.Event;
import com.yzrj.app.suixinji.RxBus.RxBus;
import com.yzrj.app.suixinji.bean.TianQiBean;
import com.yzrj.app.suixinji.fragments.DaiBanFragment;
import com.yzrj.app.suixinji.fragments.DaibanDetailFrame;
import com.yzrj.app.suixinji.fragments.JiNianRiFragment;
import com.yzrj.app.suixinji.fragments.RiJiFragment;
import com.yzrj.app.suixinji.fragments.ZhanDanFragment;
import com.yzrj.app.suixinji.service.FirstService;
import com.yzrj.app.suixinji.service.SecondService;
import com.yzrj.app.suixinji.utils.AppUtils;
import com.yzrj.app.suixinji.utils.AsyncHttpClientUtil;
import com.yzrj.app.suixinji.utils.GsonUtil;
import com.yzrj.app.suixinji.utils.LocationUtils;
import com.yzrj.app.suixinji.utils.RequestPermissions;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import test.greenDAO.bean.Duty;

public class MainActivity04 extends RxAppCompatActivity implements DaibanDetailFrame.AddDutyInputListener {
    TabHost mTabHost;
    ViewPager mViewPager;
    TabsAdapter mTabsAdapter;

    private static RxBus _rxBus;
    public static String PACKAGE_NAME;
    public static Resources resources;

    /**
     * 获取RxBus对象
     */
    public static RxBus getRxBusSingleton() {
        if (_rxBus == null) {
            _rxBus = new RxBus();
        }
        return _rxBus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main04);
        // 获得包名和资源，方便后面的程序使用
        PACKAGE_NAME = getApplicationContext().getPackageName();
        resources = getResources();
        _rxBus = getRxBusSingleton();
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (ViewPager) findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

//        mTabsAdapter.addTab(mTabHost.newTabSpec("one")
//                        .setIndicator(getIndicatorView(R.mipmap.ic_launcher, "待办")),
//                DaiBanFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("two")
                        .setIndicator(getIndicatorView(R.mipmap.ic_launcher, "日记")),
                RiJiFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("three")
                        .setIndicator(getIndicatorView(R.mipmap.ic_launcher, "账单")),
                ZhanDanFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("four")
                        .setIndicator(getIndicatorView(R.mipmap.ic_launcher, "纪念日")),
                JiNianRiFragment.class, null);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }

//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);

        initAlarmService();
    }

    private void initAlarmService() {
        try {
            startService(new Intent(this, FirstService.class));//启动闹钟服务
            startService(new Intent(this, SecondService.class));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //JobScheduler

            }
            //绑定闹钟服务
            this.bindService(new Intent(this, FirstService.class), new MyConn(), Context.BIND_IMPORTANT);
        } catch (Exception e) {
            Log.e("ssssssssssMainActivity", "initAlarmService:", e);
        }
    }


    class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("ssssssssssMainActivity", "onServiceConnected：MainActivity与FirstService连接成功");

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("ssssssssssMainActivity", "onServiceDisconnected:FirstService被杀死 MainActivity重启FirstService");
            // 启动FirstService
            MainActivity04.this.startService(new Intent(MainActivity04.this, FirstService.class));
            //绑定FirstService
            MainActivity04.this.bindService(new Intent(MainActivity04.this, FirstService.class), new MyConn(), Context.BIND_IMPORTANT);
        }
    }

    @Override
    protected void onStop() {
        Log.e("ssssssssssMainActivity", "onStop应用");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("ssssssssssMainActivity", "onDestroy应用关闭");
        try {
            startService(new Intent(this, FirstService.class));//重新启动闹钟服务
            Log.e("ssssssssssMainActivity", "onDestroy闹钟重置完成");
        } catch (Exception e) {
            Log.e("ssssssssssMainActivity", "onDestroy闹钟重置失败", e);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                RequestPermissions.diweiExternalStorage(MainActivity04.this, permissionCallBack2001, 2001);
            }
        }, 100);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    SharedPreferences preferences = getSharedPreferences("weathercity", MODE_PRIVATE);
                    String city = preferences.getString("city", "");
                    if (!TextUtils.isEmpty(LocationUtils.cityName) &&
                            !city.equals(LocationUtils.cityName)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("city", LocationUtils.cityName);
                        editor.putString("sheng", LocationUtils.adminArea);
                        editor.commit();
                    }
                    tq();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void tq() {
        try {
            SharedPreferences preferences = getSharedPreferences("weathercity", MODE_PRIVATE);
            String city = preferences.getString("city", "");
            Log.e("sssssssssssssssssssss", "tq:city " + city);
            AsyncHttpClientUtil.getInstance().get(AppUtils.url_tq + city, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String json = new String(responseBody);
                        Log.e("ssssssssssssssssss", json);
                        TianQiBean baseBean = GsonUtil.buildGson().fromJson(json, TianQiBean.class);
                        SharedPreferences preferences = getSharedPreferences("weathercity", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("weather", baseBean.data.forecast.get(0).type);
                        editor.commit();
                        return;
                    } catch (Exception e) {
//                    tv_tq.setText("天气:" + "未知");
                        Log.e("sssssssssssssssssssss", "tq2: ", e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                tv_tq.setText("天气:" + "未知" + statusCode);
                    Log.e("sssssssssssssssssssss", "tqonFailure: " + statusCode);
                }
            });
        } catch (Exception e) {
            Log.e("sssssssssssssssssssss", "tq: ", e);
        }
    }

    RequestPermissions.PermissionCallBack permissionCallBack2001 = new RequestPermissions.PermissionCallBack() {
        @Override
        public void setOnPermissionListener(Boolean bo) {
            if (bo) {
                //城市
                LocationUtils.getCNBylocation(MainActivity04.this, handler);
            } else {
                Toast.makeText(MainActivity04.this, "" + "没有定位权限，请授权后再操作", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2001: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCallBack2001.setOnPermissionListener(true);
                } else {
                    permissionCallBack2001.setOnPermissionListener(false);
//                    RequestPermissions.diweiExternalStorage(MainActivity04.this, permissionCallBack1, 2001);
                }
            }
            break;
        }
    }

    @SuppressLint("InflateParams")
    public View getIndicatorView(int id, String name) {
        View layout = getLayoutInflater().inflate(R.layout.mytab_linear, null);
        ImageView img = (ImageView) layout.findViewById(R.id.mytab_img);
        img.setImageResource(id);
        TextView tv = (TextView) layout.findViewById(R.id.mytab_tv);
        tv.setText(name);
        return layout;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }


    public static class TabsAdapter extends FragmentPagerAdapter
            implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final TabHost mTabHost;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mContext));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);
            mTabHost.addTab(tabSpec);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onTabChanged(String tabId) {
            int position = mTabHost.getCurrentTab();
            mViewPager.setCurrentItem(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            // Unfortunately when TabHost changes the current tab, it kindly
            // also takes care of putting focus on it when not in touch mode.
            // The jerk.
            // This hack tries to prevent this from pulling focus out of our
            // ViewPager.
            TabWidget widget = mTabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
//                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAddDutyInputComplete(Duty newduty) {
        if (_rxBus.hasObservers()) {    //是否有观察者，有，则发送一个事件
            _rxBus.send(new Event.AddEvent(newduty, newduty.getType()));
        }
    }


}
