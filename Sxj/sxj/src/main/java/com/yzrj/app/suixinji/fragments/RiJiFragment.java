package com.yzrj.app.suixinji.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.functions.Action1;
import test.greenDAO.bean.Duty;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RiJiFragment extends RxFragment {
    public static final int TAKE_PHOTO = 1;//1声明一个常量，拍照请求码
    public static final int CHOOSE_PHOTO = 2;//2声明一个常量，相册请求码
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

    private Uri imageUri;
    private ImageView imageView_zhucetouxiang;


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
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){//结果码如果等于RESULT_OK(源码中的常量)
                    try{//将拍摄的照片显示出来。
                        // 调用BitmapFactory的decodeStream方法解码
                        Bitmap bitmap = BitmapFactory.decodeStream( getContext().getContentResolver()
                                .openInputStream( imageUri ) );
                        //将Uri解码成为Bitmap，并设置为显示的图片
                        imageView_zhucetouxiang.setImageBitmap( bitmap );
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }

//        case CHOOSE_PHOTO:
//        if (resultCode == RESULT_OK){//2如果结果码是RESULT_OK则通过
//            //2判断手机系统版本号
//            if(Build.VERSION.SDK_INT >=19){
//                //4.4及以上的系统使用这个方法处理图片，在后面写这个方法
//                handleImageOnKitKat(data);
//            }else{
//                //4.4以下版本使用这个方法处理图片，在后面写这个方法
//                handleImageBeforeKitKat(data);
//            }
//            break;
//        }
//        default:
//        break;
        }
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
                            AlertDialog.Builder register = new AlertDialog.Builder(getContext());
                            View b = View.inflate(getContext(),R.layout.register,null);
                            imageView_zhucetouxiang = b.findViewById(R.id.touxiang);
                            final Button shangc = b.findViewById(R.id.shangchuan);
                            final EditText register_id = b.findViewById(R.id.edit_register_id);
                            final EditText register_pass = b.findViewById(R.id.edit__register_pass);
                            final EditText nicheng = b.findViewById(R.id.nicheng);
                            final Button zhucea = b.findViewById(R.id.btn_register);
                            register.setTitle("注册").setIcon(R.drawable.register_a).setView(b);
                            final AlertDialog alertDialog1 = register.create();//创建对话框
                            shangc.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    shangchuan();//上传方法
                                }
                            });//上传头像点击事件
                            zhucea.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(getContext(),"哈哈哈",Toast.LENGTH_SHORT).show();
                                }
                            });
                            alertDialog1.show();
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
    public void shangchuan(){
        Dialog dialog = new Dialog(getContext(),R.style.my_dialog);
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.xuanzetouxiang,null);
        TextView takePhoto = inflate.findViewById(R.id.takePhoto);
        TextView choosePhoto = inflate.findViewById(R.id.choosePhoto);
        dialog.setContentView(inflate);//设置对话框布局
        Window dialogWindow = dialog.getWindow();//获取当前窗体
        dialogWindow.setGravity(Gravity.BOTTOM);//设置对话框从底部弹出
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();//获取窗体的属性
        lp.y = 5 ;//设置对话框与底部的距离
        lp.alpha = 9f;//透明度
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);//将属性设置给窗体
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage = new File( getContext().getExternalCacheDir(),"output_image.jpg" );
                try{
                    if(outputImage.exists()){//如果这个File对象存在
                        outputImage.delete();//那就删除它
                    }
                    outputImage.createNewFile();//创建新的File对象
                }catch (IOException e){
                    e.printStackTrace();
                }
                //为啥要进行一下判断，因为从安卓7.0开始直接使用本地真实路径的Uri被认为是不安全的，
                //会抛出一个FileUriExposedException异常，而FileProvider则是一种特殊的内容提供器
                //它使用了和内容提供器类似的机制来对数据进行保护，可以选择将封装过的Uri共享给外部。
                if(Build.VERSION.SDK_INT >= 24){//判断系统版本是否高于Android7.0，如果是则
                    //调用FileProvider的getUriForFile方法将File对象转换成一个封装过的Uri对象
                    //参数一Context对象，参数二可以是任意唯一的字符串，参数三则是我们刚刚创建的File对象
                    imageUri = FileProvider.getUriForFile( getContext(),
                            "com.example.a84430.fileprovider",outputImage );
                }else{//如果是低于7.0，则
                    //调用Uri的fromFile()方法将File对象转换成Uri对象，这个Uri对象标识着
                    // output_image.jpg这张图片的本地真实路径。
                    imageUri = Uri.fromFile( outputImage );
                }
                //启动相机程序
                Intent intent = new Intent( "android.media.action.IMAGE_CAPTURE" );
                //指定图片输出地址，这里填入刚得到的Uri对象
                intent.putExtra( MediaStore.EXTRA_OUTPUT,imageUri );
                //为啥调用startActivityForResult()，原因在笔记里
                startActivityForResult( intent,1 );
            }
        });
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        dialog.show();//显示对话框
    }//上传头像方法
}

