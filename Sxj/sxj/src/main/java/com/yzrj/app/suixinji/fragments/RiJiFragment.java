package com.yzrj.app.suixinji.fragments;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.yzrj.app.suixinji.R;
import com.yzrj.app.suixinji.RxBus.Event;
import com.yzrj.app.suixinji.RxBus.RxBus;
import com.yzrj.app.suixinji.adapter.RiJiAdapter;
import com.yzrj.app.suixinji.ui.MainActivity04;
import com.yzrj.app.suixinji.ui.RiJiDataActivity;
import com.yzrj.app.suixinji.utils.AppUtils;
import com.yzrj.app.suixinji.utils.DbServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.functions.Action1;
import test.greenDAO.bean.Duty;

/**
 * A simple {@link Fragment} subclass.
 */
public class RiJiFragment extends RxFragment {


    private View rootView;

    public RiJiFragment() {
        // Required empty public constructor
    }

    public RiJiFragment newInstance() {
        Bundle args = new Bundle();
        RiJiFragment dailyFragment = new RiJiFragment();
        dailyFragment.setArguments(args);
        return dailyFragment;
    }

    RecyclerView rv_show_diary;
    private RiJiAdapter qadapter;
    private List<Duty> datas;
    private RxBus _rxBus;
    private DrawerLayout drawerLayout;
    private ImageView imageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_ri_ji, null);
        }
        //查询数据库
        datas = DbServices.getInstance(getContext()).queryNote(AppUtils.typerj);

//实例化Adapter
        qadapter = new RiJiAdapter(getContext(), datas);
        //设置动画
        qadapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        drawerLayout = rootView.findViewById(R.id.drawer);
        imageView = rootView.findViewById(R.id.common_iv_test);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        rv_show_diary = (RecyclerView) rootView.findViewById(R.id.rv_show_diary);
        rv_show_diary.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_show_diary.setItemAnimator(new DefaultItemAnimator());
        rv_show_diary.setAdapter(qadapter);





        qadapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                Duty duty = (Duty) adapter.getItem(position);
//                switch (view.getId()) {
//                    case R.id.scb:
//
//                        break;
//                }
            }
        });
        qadapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                RiJiDataActivity.start(getActivity(), qadapter.getItem(i));
            }
        });
        qadapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, final int i) {
                if (qadapter.getItem(i).getId() != null) {
                    // 弹窗确认
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("你确定要删除么？");
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DbServices.getInstance(getContext()).deleteNote(qadapter.getItem(i).getId());  //删除数据库中的数据
                            qadapter.remove(i);
                            if (qadapter.getData().size() == 0) {
                                xg();
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();  // 显示弹窗
                }
                return true;
            }
        });

        FloatingActionButton fab_add = (FloatingActionButton) rootView.findViewById(R.id.main_fab_enter_edit);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RiJiAddDialogFrament adddialog = new RiJiAddDialogFrament();
                adddialog.show(getActivity().getFragmentManager(), "rijiaddDialog");
            }
        });
        _rxBus = ((MainActivity04) getActivity()).getRxBusSingleton();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (qadapter.getData().size() > 0) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Duty duty = (Duty) qadapter.getData().get(0);
                    Date d1 = (duty).getDate();
                    String t1 = format.format(d1);
                    String t2 = format.format(new Date());
                    if (t1.equals(t2) && "你什么都没写下...".equals(duty.getInfo())) {
                        qadapter.remove(0);
                    } else {
                        return;
                    }
                }
                xg();
            }
        }, 200);

    }

    public void xg() {
        SharedPreferences preferences = getActivity().getSharedPreferences("weathercity", getActivity().MODE_PRIVATE);
        String city = preferences.getString("city", "");
        String weather = preferences.getString("weather", "阴天");
        Duty duty = new Duty(null, "今天", "你什么都没写下...",
                 AppUtils.typerj, false, new Date(), 0L, 0, 0, city, weather);
        qadapter.add(0, duty);
    }

    @Override
    public void onStart() {
        super.onStart();
        _rxBus.toObserverable()
                .compose(this.bindToLifecycle())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {
                        if (event instanceof Event.AddEvent) {
                            //如果 传来的 新增事件 和当前 查询结果类型一致 则直接往里面填充
                            if (((Event.AddEvent) event).getMduty().getType() ==  AppUtils.typerj) {
                                if (qadapter.getData().size() > 0) {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    Duty duty = (Duty) qadapter.getData().get(0);
                                    Date d1 = (duty).getDate();
                                    String t1 = format.format(d1);
                                    String t2 = format.format(new Date());
                                    if (t1.equals(t2) && "你什么都没写下...".equals(duty.getInfo())) {
                                        qadapter.remove(0);
                                    } else {

                                    }
                                }
                                qadapter.add(0, ((Event.AddEvent) event).getMduty());
                            }
                        }
                    }
                });
    }
}
