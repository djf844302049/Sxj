package com.yzrj.app.suixinji.fragments;


import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.yuukidach.ucount.model.RoundIcon;
import com.yzrj.app.suixinji.R;
import com.yzrj.app.suixinji.RxBus.Event;
import com.yzrj.app.suixinji.RxBus.RxBus;
import com.yzrj.app.suixinji.adapter.RiJiAdapter;
import com.yzrj.app.suixinji.ui.AboutUsActivity;
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
    private ImageView imageView;//侧滑菜单键
    private RadioGroup radioGroup;

    private TextView textlogin;//点击登录
    private RoundIcon roundIcon;


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
        drawerLayout = rootView.findViewById(R.id.drawer);//侧滑菜单
        roundIcon =rootView.findViewById(R.id.image_1);
        textlogin = rootView.findViewById(R.id.textLogin);
        imageView = rootView.findViewById(R.id.common_iv_test);//侧滑菜单图片按钮
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        cehua();//侧滑点击事件
        dianjilogin();//点击登录的点击事件

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
    public void cehua(){
        radioGroup = rootView.findViewById(R.id.radio_0);
        final RadioButton radioButton1 = rootView.findViewById(R.id.radio_1);
        final RadioButton radioButton2 = rootView.findViewById(R.id.radio_2);
        final RadioButton radioButton3 = rootView.findViewById(R.id.radio_3);
        final RadioButton radioButton4 = rootView.findViewById(R.id.radio_4);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioButton1.getId() == i){
                    radioButton1.setChecked(false);

                    if(textlogin.getText().equals("点击登录")){
                        Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(),"同步数据成功",Toast.LENGTH_SHORT).show();
                    }

                }
                if (radioButton2.getId() == i){
                    radioButton2.setChecked(false);
                    Intent intent = new Intent(getContext(), AboutUsActivity.class);
                    startActivity(intent);
                }
                if (radioButton3.getId() == i){
                    radioButton3.setChecked(false);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("qxs@youzirj.com").setIcon(R.drawable.email).setMessage("请将您的宝贵建议发送至上方邮箱，感谢您的使用。");
                    builder.create().show();
                }
                if (radioButton4.getId() == i){
                    radioButton4.setChecked(false);
                    Toast.makeText(getContext(),"已是最新版本",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }//侧滑点击事件
    public void dianjilogin(){
        textlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textlogin.getText().equals("点击登录")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());//创建对话框构建器
                    View view2 = View.inflate(getContext(),R.layout.login,null);//设置布局
                    final EditText usename = view2.findViewById(R.id.edit_id);//获取布局里的输入框
                    final EditText password = view2.findViewById(R.id.edit_pass);
                    final TextView textV = view2.findViewById(R.id.textzhuce);
                    final Button button = view2.findViewById(R.id.btn_login);
                    builder.setTitle("登录").setIcon(R.mipmap.ico).setView(view2);//设置对话框参数
                    final AlertDialog alertDialog  = builder.create();//创建对话框
                    textV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(getContext(),"注册这辈子是不会注册了",Toast.LENGTH_SHORT).show();
                            
                        }
                    });//注册的点击事件
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String u = usename.getText().toString();
                            String p = password.getText().toString();
                            if(u.equals("123456")&&p.equals("123456")){
                                Toast.makeText(getContext(),"登录成功",Toast.LENGTH_SHORT).show();
                                textlogin.setText("皮皮虾跟我来");
                                int a = textlogin.getText().length();
                                if(a>4&&a<6){
                                    textlogin.setTextSize(25);
                                }else if(a>5&&a<7){
                                    textlogin.setTextSize(22);
                                }
                                alertDialog.dismiss();//关闭对话框
                                roundIcon.setImageResource(R.drawable.yangmi2);//动态设置圆形头像
                            }else {
                                Toast.makeText(getContext(),"账号或密码错误",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    alertDialog.show();
                }else{//如果是已经登录，点击注销
                    AlertDialog.Builder aa = new AlertDialog.Builder(getContext());
                    aa.setTitle("注销").setIcon(R.drawable.exit).setMessage("是否退出登录？")
                            .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    textlogin.setText("点击登录");
                                    textlogin.setTextSize(30);
                                    roundIcon.setImageResource(R.drawable.yngmi);
                                }
                            }).setNegativeButton("不", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    aa.create().show();//创建对话框
                }
            }
        });
    }//点击登录的点击事件
}
