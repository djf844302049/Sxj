package com.yzrj.app.suixinji.fragments;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
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
import com.yzrj.app.suixinji.adapter.QuickAdapter;
import com.yzrj.app.suixinji.dialogs.DutyInfoDialogFragment;
import com.yzrj.app.suixinji.service.FirstService;
import com.yzrj.app.suixinji.ui.MainActivity04;
import com.yzrj.app.suixinji.utils.AppUtils;
import com.yzrj.app.suixinji.utils.DbServices;
import com.yzrj.app.suixinji.utils.RequestPermissions;
import com.yzrj.app.suixinji.utils.SmoothCheckBox;

import java.util.List;

import rx.functions.Action1;
import test.greenDAO.bean.Duty;

/**
 * Created by castl on 2016/5/18.
 * 用于显示每天的课程
 */
public class DaiBanFragment extends RxFragment {


    private RxBus _rxBus;
    private RecyclerView Rv_duty;
    private QuickAdapter qadapter;
    private View rootView;
    private List<Duty> datas;
    private DbServices dbservices;


    public static final String TYPE = "TYPE";

    public static MainActivity04 activity;
    private TextView tv_title, tv_empty;


    public DaiBanFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        DaiBanFragment dailyFragment = new DaiBanFragment();
        dailyFragment.setArguments(args);
        return dailyFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbservices = DbServices.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_dai_ban, null);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity04) getActivity();
        _rxBus = (activity).getRxBusSingleton();
        initView();


    }

    RequestPermissions.PermissionCallBack permissionCallBack1 = new RequestPermissions.PermissionCallBack() {
        @Override
        public void setOnPermissionListener(Boolean bo) {
            if (bo) {
                DaibanDetailFrame adddialog = new DaibanDetailFrame();
                adddialog.show(activity.getFragmentManager(), "addDialog");

            } else {
                Toast.makeText(getContext(), "没有读写日历权限，请授权后再操作", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1001: {
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

    private void initView() {
        tv_title = (TextView) rootView.findViewById(R.id.tool_bar_title);
        tv_empty = (TextView) rootView.findViewById(R.id.tv_empty);

        FloatingActionButton fab_add = (FloatingActionButton) rootView.findViewById(R.id.fabBtn);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestPermissions.READ_CALENDARandWRITE_CALENDAR(DaiBanFragment.this, permissionCallBack1, 1001);
            }
        });


        Rv_duty = (RecyclerView) rootView.findViewById(R.id.rv_duty);
        Rv_duty.setLayoutManager(new LinearLayoutManager(getContext()));
        //查询数据库
        datas = DbServices.getInstance(getContext()).queryNote(AppUtils.typedb);
        //实例化Adapter
        qadapter = new QuickAdapter(getContext(), datas);
        //设置动画
        qadapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        Rv_duty.setItemAnimator(new DefaultItemAnimator());

        Rv_duty.setAdapter(qadapter);

        tv_title.setText("待办事项");
        if (datas.size() > 0) {
            tv_empty.setVisibility(View.GONE);
        }
        qadapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Duty duty = (Duty) adapter.getItem(position);
                switch (view.getId()) {
                    case R.id.scb:
                        SmoothCheckBox scb = (SmoothCheckBox) view;
                        scb.setChecked(!scb.isChecked(), true);
                        duty.setStatus(scb.isChecked());
                        dbservices.saveNote(duty);
                        qadapter.notifyDataSetChanged();

                        FirstService.service.setClock(duty);
                        break;
                }
            }
        });
        qadapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                if (qadapter.getItem(i).getId() != null) {
                    DutyInfoDialogFragment infodialog = new DutyInfoDialogFragment().newInstance(qadapter.getItem(i));
                    infodialog.show(getActivity().getFragmentManager(), "aboutDialog");
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
                            FirstService.deleteClock(getActivity(), qadapter.getItem(i));

                            dbservices.deleteNote(qadapter.getItem(i).getId());  //删除数据库中的数据
                            qadapter.remove(i);
                            if (qadapter.getData().size() == 0) {
                                tv_empty.setVisibility(View.VISIBLE);
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();  // 显示弹窗
                } else {
                    Toast.makeText(getActivity(), "数据不正确无法删除", Toast.LENGTH_SHORT).show();

                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        qadapter.notifyDataSetChanged();
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
                            if (((Event.AddEvent) event).getMduty().getType() == AppUtils.typedb) {
                                tv_title.setText("待办事项");
                                tv_empty.setVisibility(View.GONE);
                                //查询数据库
                                datas = DbServices.getInstance(getContext()).queryNote(AppUtils.typedb);
                                qadapter.setNewData(datas);

                                FirstService.service.setClock(((Event.AddEvent) event).getMduty());

//                                qadapter.add(0, ((Event.AddEvent) event).getMduty());
                            }
                        }
                    }
                });
    }
}
