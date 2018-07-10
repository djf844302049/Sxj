package com.yzrj.app.suixinji.fragments;


import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.yzrj.app.suixinji.R;
import com.yzrj.app.suixinji.RxBus.Event;
import com.yzrj.app.suixinji.RxBus.RxBus;
import com.yzrj.app.suixinji.adapter.JinianriAdapter;
import com.yzrj.app.suixinji.dialogs.DutyInfoDialogFragment;
import com.yzrj.app.suixinji.ui.MainActivity04;
import com.yzrj.app.suixinji.utils.AppUtils;
import com.yzrj.app.suixinji.utils.DbServices;
import com.yzrj.app.suixinji.utils.RequestPermissions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.functions.Action1;
import test.greenDAO.bean.Duty;

/**
 * A simple {@link Fragment} subclass.
 */
public class JiNianRiFragment extends RxFragment {


    private View rootView;

    public JiNianRiFragment() {
        // Required empty public constructor
    }

    public JiNianRiFragment newInstance() {
        Bundle args = new Bundle();
        JiNianRiFragment dailyFragment = new JiNianRiFragment();
        dailyFragment.setArguments(args);
        return dailyFragment;
    }

    RecyclerView rv_show_diary;
    private JinianriAdapter qadapter;
    private List<Duty> datas;
    private RxBus _rxBus;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_ri_ji, null);
        }
        TextView common_tv_title = (TextView) rootView.findViewById(R.id.common_tv_title);
        common_tv_title.setText("纪念日");

        //查询数据库
        datas = DbServices.getInstance(getContext()).queryNote(AppUtils.typejnr);

//实例化Adapter
        qadapter = new JinianriAdapter(getContext(), datas);
        //设置动画
        qadapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        rv_show_diary = (RecyclerView) rootView.findViewById(R.id.rv_show_diary);
        rv_show_diary.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_show_diary.setItemAnimator(new DefaultItemAnimator());
        rv_show_diary.setAdapter(qadapter);

        qadapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Duty duty = (Duty) adapter.getItem(position);
                switch (view.getId()) {
                    case R.id.scb:

                        break;
                }
            }
        });
        qadapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                if (qadapter.getItem(i).getId() != null) {
                    DutyInfoDialogFragment infodialog = new DutyInfoDialogFragment().newInstance(qadapter.getItem(i));
                    infodialog.show(getActivity().getFragmentManager(), "jnraboutDialog");
                }
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
                RequestPermissions.READ_CALENDARandWRITE_CALENDAR(JiNianRiFragment.this, permissionCallBack1, 4001);
            }
        });
        _rxBus = ((MainActivity04) getActivity()).getRxBusSingleton();
        return rootView;
    }

    RequestPermissions.PermissionCallBack permissionCallBack1 = new RequestPermissions.PermissionCallBack() {
        @Override
        public void setOnPermissionListener(Boolean bo) {
            if (bo) {
                JinianriDialogFrament adddialog = new JinianriDialogFrament();
                adddialog.show(getActivity().getFragmentManager(), "jnraddDialog");
            } else {
                Toast.makeText(getContext(), "没有读写日历权限，请授权后再操作", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 4001: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCallBack1.setOnPermissionListener(true);
                } else {
                    permissionCallBack1.setOnPermissionListener(false);
                }
            }
            break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (qadapter.getData().size() == 0) {
                    xg();
                }
            }
        }, 200);
    }

    public void xg() {
        Duty duty = new Duty(null, "", "您还没有记录纪念日",
                AppUtils.typejnr, false, new Date(), new Date().getTime(), 0, 0, "", "");
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
                            if (((Event.AddEvent) event).getMduty().getType() == AppUtils.typejnr) {
                                //查询数据库
                                datas = DbServices.getInstance(getContext()).queryNote(AppUtils.typejnr);
                                qadapter.setNewData(datas);
                                if (qadapter.getData().size() > 0) {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    Duty duty = (Duty) qadapter.getData().get(0);
                                    Date d1 = (duty).getDate();
                                    String t1 = format.format(d1);
                                    String t2 = format.format(new Date());
                                    if (t1.equals(t2) && "您还没有记录纪念日".equals(duty.getInfo())) {
                                        qadapter.remove(0);
                                    } else {

                                    }
                                }
//                                qadapter.add(0, ((Event.AddEvent) event).getMduty());
                            }
                        }
                    }
                });
    }
}
