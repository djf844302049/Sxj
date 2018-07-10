package com.yzrj.app.suixinji.ui;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.yzrj.app.suixinji.R;
import com.yzrj.app.suixinji.RxBus.Event;
import com.yzrj.app.suixinji.RxBus.RxBus;
import com.yzrj.app.suixinji.adapter.ViewPagerAdapter;
import com.yzrj.app.suixinji.dialogs.AddDialogFragment;
import com.yzrj.app.suixinji.fragments.DaiBanFragment;
import com.yzrj.app.suixinji.fragments.JiNianRiFragment;
import com.yzrj.app.suixinji.fragments.RiJiFragment;
import com.yzrj.app.suixinji.fragments.ZhanDanFragment;
import com.yzrj.app.suixinji.utils.DbServices;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.Date;

import test.greenDAO.bean.Duty;

public class MainActivity02 extends RxAppCompatActivity implements View.OnClickListener, AddDialogFragment.AddDutyInputListener {
    private static RxBus _rxBus;

    /**
     * 获取RxBus对象
     */
    public static RxBus getRxBusSingleton() {
        if (_rxBus == null) {
            _rxBus = new RxBus();
        }
        return _rxBus;
    }

    private TabLayout toolbar_tab;
    private ViewPager main_vp_container;

    private SharedPreferences preferences;  //修改城市时保存到本地
    private SharedPreferences.Editor sp_edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main02);
        _rxBus = getRxBusSingleton();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
           // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }

        initView();
    }


    private void initView() {
        preferences = getSharedPreferences("weathercity", MODE_PRIVATE);
        sp_edit = preferences.edit();

        main_vp_container = (ViewPager) findViewById(R.id.main_vp_container);
        toolbar_tab = (TabLayout) findViewById(R.id.toolbar_tab);



        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        vpAdapter.addFragment(new DaiBanFragment().newInstance("待办"), "待办");
        vpAdapter.addFragment(new RiJiFragment().newInstance(), "日记");
        vpAdapter.addFragment(new ZhanDanFragment().newInstance(), "账单");
        vpAdapter.addFragment(new JiNianRiFragment().newInstance(), "纪念日");
        main_vp_container.setAdapter(vpAdapter);
        main_vp_container.setOffscreenPageLimit(4);  //设置4页缓存

        toolbar_tab.setupWithViewPager(main_vp_container);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }


    @Override
    public void onAddDutyInputComplete(String title, String type, String info) {

        if (title.trim().isEmpty()) {
            Toast.makeText(MainActivity02.this, "标题不能为空！", Toast.LENGTH_SHORT).show();
        } else {

            Duty newduty = new Duty(null, title, info, type, false, new Date(),0L,0,0,"","");
            DbServices.getInstance(this).saveNote(newduty);
            if (_rxBus.hasObservers()) {    //是否有观察者，有，则发送一个事件
                _rxBus.send(new Event.AddEvent(newduty, type));
            }
        }
    }


}
