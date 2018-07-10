package com.yzrj.app.suixinji.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxFragment;
import com.yuukidach.ucount.AddItemActivity;
import com.yuukidach.ucount.GlobalVariables;
import com.yuukidach.ucount.model.BookItem;
import com.yuukidach.ucount.model.BookItemAdapter;
import com.yuukidach.ucount.model.IOItem;
import com.yuukidach.ucount.model.IOItemAdapter;
import com.yzrj.app.suixinji.R;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import at.markushi.ui.CircleButton;

import static android.content.Context.MODE_PRIVATE;
import static com.yzrj.app.suixinji.ui.MainActivity04.PACKAGE_NAME;
import static com.yzrj.app.suixinji.ui.MainActivity04.resources;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhanDanFragment extends RxFragment {

    private List<IOItem> ioItemList = new ArrayList<>();
    private List<BookItem> bookItemList = new ArrayList<>();

    private RecyclerView ioItemRecyclerView;
    private IOItemAdapter ioAdapter;
    private TextView showBtn;
    Button btn_name;
    private CircleButton addBtn;
    private ImageView headerImg;
    private TextView monthlyCost, monthlyEarn;

    // parameter for drawer
    private DrawerLayout drawerLayout;
    private LinearLayout bookLinearLayout;
    private RecyclerView bookItemRecyclerView;
    private BookItemAdapter bookAdapter;
    private ImageButton addBookButton;
    private ImageView drawerBanner;

    public static final int SELECT_PIC4MAIN = 1;
    public static final int SELECT_PIC4DRAWER = 2;
    public DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private static final String TAG = "MainActivity03";
    private SimpleDateFormat formatSum = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
    String sumDate = formatSum.format(new Date());

    // 为ioitem recyclerView设置滑动动作
    private ItemTouchHelper.Callback ioCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            // 获得滑动位置
            final int position = viewHolder.getAdapterPosition();

            if (direction == ItemTouchHelper.RIGHT) {
                // 弹窗确认
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("你确定要删除么？");

                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ioAdapter.removeItem(position);
                        // 刷新界面
                        onResume();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout sonView = (LinearLayout) viewHolder.itemView;
                        TextView grandsonTextView = (TextView) sonView.findViewById(R.id.iotem_date);
                        // 判断是否应该显示时间
                        if (sonView.findViewById(R.id.date_bar).getVisibility() == View.VISIBLE)
                            GlobalVariables.setmDate("");
                        else GlobalVariables.setmDate(ioAdapter.getItemDate(position));
                        ioAdapter.notifyItemChanged(position);
                    }
                }).show();  // 显示弹窗
            }
        }
    };

    private ItemTouchHelper ioTouchHelper = new ItemTouchHelper(ioCallback);


    // 为bookitem recyclerview添加动作
    private ItemTouchHelper.Callback bookCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            // 如果不想上下拖动，可以将 dragFlags = 0
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

            // 如果你想左右滑动，可以将 swipeFlags = 0
            int swipeFlags = ItemTouchHelper.RIGHT;

            //最终的动作标识（flags）必须要用makeMovementFlags()方法生成
            int flags = makeMovementFlags(dragFlags, swipeFlags);
            return flags;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }


        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            // 获得滑动位置
            final int position = viewHolder.getAdapterPosition();

            if (direction == ItemTouchHelper.RIGHT) {
                // 弹窗确认
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("你确定要删除么？");

                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bookAdapter.removeItem(position);
                        // 刷新界面
                        onResume();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bookAdapter.notifyDataSetChanged();
                    }
                }).show();  // 显示弹窗
            }
        }
    };

    private ItemTouchHelper bookTouchHelper = new ItemTouchHelper(bookCallback);


    private View rootView;

    public ZhanDanFragment() {
        // Required empty public constructor
    }

    public ZhanDanFragment newInstance() {
        Bundle args = new Bundle();
        ZhanDanFragment dailyFragment = new ZhanDanFragment();
        dailyFragment.setArguments(args);
        return dailyFragment;
    }


    //=============================================================================================================//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_zhan_dan, null);
        }


        // litepal
        Connector.getDatabase();

        // 获得包名和资源，方便后面的程序使用
        PACKAGE_NAME = getActivity().getApplicationContext().getPackageName();
        resources = getResources();
        btn_name = (Button) rootView.findViewById(R.id.btn_name);
        showBtn = (TextView) rootView.findViewById(R.id.show_money_button);
        addBtn = (CircleButton) rootView.findViewById(R.id.add_button);
        ioItemRecyclerView = (RecyclerView) rootView.findViewById(R.id.in_and_out_items);
        headerImg = (ImageView) rootView.findViewById(R.id.header_img);
        monthlyCost = (TextView) rootView.findViewById(R.id.monthly_cost_money);
        monthlyEarn = (TextView) rootView.findViewById(R.id.monthly_earn_money);
        // drawer
        drawerLayout = (DrawerLayout) rootView.findViewById(R.id.drawer_of_books);
        bookItemRecyclerView = (RecyclerView) rootView.findViewById(R.id.book_list);
        addBookButton = (ImageButton) rootView.findViewById(R.id.add_book_button);
        bookLinearLayout = (LinearLayout) rootView.findViewById(R.id.left_drawer);
        drawerBanner = (ImageView) rootView.findViewById(R.id.drawer_banner);

        // 设置按钮监听
        btn_name.setOnClickListener(new ButtonListener());
        addBtn.setOnClickListener(new ButtonListener());
        addBookButton.setOnClickListener(new ButtonListener());

        // 设置首页header图片长按以更换图片
        headerImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectPictureFromGallery(1);
                return false;
            }
        });

        drawerBanner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectPictureFromGallery(2);
                return false;
            }
        });

        setImageForHeaderAndBanner();
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        initBookItemList(getContext());
        initIoItemList(getContext());

        BookItem tmps = DataSupport.find(BookItem.class, GlobalVariables.getmBookId());
        String sumString = decimalFormat.format(tmps.getSumAll());
        showBtn.setText("结余：" + sumString);

        BookItem tmp = DataSupport.find(BookItem.class, bookItemList.get(GlobalVariables.getmBookPos()).getId());
        monthlyCost.setText(decimalFormat.format(tmp.getSumMonthlyCost()));
        monthlyEarn.setText(decimalFormat.format(tmp.getSumMonthlyEarn()));

        btn_name.setText(tmp.getName());
    }


    // 各个按钮的活动
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                // 按住加号按钮以后，切换到AddItemActivity
                case R.id.add_button:
                    Intent intent = new Intent(getContext(), AddItemActivity.class);
                    startActivity(intent);
                    break;
                case R.id.add_book_button:
                    final BookItem bookItem = new BookItem();
                    final EditText book_title = new EditText(getContext());
                    // 弹窗输入
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("请输入新的账本名字");

                    builder.setView(book_title);

                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!book_title.getText().toString().isEmpty()) {
                                bookItem.setName(book_title.getText().toString());
                                bookItem.setSumAll(0.0);
                                bookItem.setSumMonthlyCost(0.0);
                                bookItem.setSumMonthlyEarn(0.0);
                                bookItem.setDate(sumDate);
                                bookItem.save();

                                onResume();
                            } else
                                Toast.makeText(getContext(), "没有输入新账本名称哦", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();  // 显示弹窗
                    break;

                case R.id.btn_name:
                    drawerLayout.openDrawer(bookLinearLayout);
                    break;
                default:
                    break;
            }
        }
    }


    // 初始化收支项目显示
    public void initIoItemList(final Context context) {

        ioItemList = DataSupport.where("bookId = ?", String.valueOf(GlobalVariables.getmBookId())).find(IOItem.class);
        setIoItemRecyclerView(context);
    }


    public void initBookItemList(final Context context) {
        bookItemList = DataSupport.findAll(BookItem.class);

        if (bookItemList.isEmpty()) {
            BookItem bookItem = new BookItem();

            bookItem.saveBook(bookItem, 1, "默认账本");
            bookItem.setSumAll(0.0);
            bookItem.setSumMonthlyCost(0.0);
            bookItem.setSumMonthlyEarn(0.0);
            bookItem.setDate(sumDate);
            bookItem.save();

            bookItemList = DataSupport.findAll(BookItem.class);
        }

        setBookItemRecyclerView(context);
    }

    public void selectPictureFromGallery(int id) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // 设置选择类型为图片类型
        intent.setType("image/*");
        // 打开图片选择
        if (id == 1)
            startActivityForResult(intent, SELECT_PIC4MAIN);
        else
            startActivityForResult(intent, SELECT_PIC4DRAWER);

    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            int takeFlags = data.getFlags();
            switch (requestCode) {
                case SELECT_PIC4MAIN:
                    if (data == null) return;
                    // 用户从图库选择图片后会返回所选图片的Uri
                    Uri uri1 = data.getData();
                    this.headerImg.setImageURI(uri1);
                    saveImageUri(SELECT_PIC4MAIN, uri1);

                    // 获取永久访问图片URI的权限

                    takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getActivity().getContentResolver().takePersistableUriPermission(uri1, takeFlags);
                    break;

                case SELECT_PIC4DRAWER:
                    if (data == null) return;
                    // 用户从图库选择图片后会返回所选图片的Uri
                    Uri uri2 = data.getData();
                    this.drawerBanner.setImageURI(uri2);
                    saveImageUri(SELECT_PIC4DRAWER, uri2);

                    // 获取永久访问图片URI的权限
                    //int takeFlags2 = data.getFlags();
                    takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getActivity().getContentResolver().takePersistableUriPermission(uri2, takeFlags);
                    break;
            }
        } catch (Exception e) {
        }

    }

    // 利用SharedPreferences保存图片uri
    public void saveImageUri(int id, Uri uri) {
        SharedPreferences pref = getActivity().getSharedPreferences("image" + id, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString("uri", uri.toString());
        prefEditor.apply();
    }

    public void setImageForHeaderAndBanner() {
        SharedPreferences pref1 = getActivity().getSharedPreferences("image" + SELECT_PIC4MAIN, MODE_PRIVATE);
        String imageUri1 = pref1.getString("uri", "");

        if (!imageUri1.equals("")) {
            Uri contentUri = Uri.parse(imageUri1);
            this.headerImg.setImageURI(contentUri);
        }

        SharedPreferences pref2 = getActivity().getSharedPreferences("image" + SELECT_PIC4DRAWER, MODE_PRIVATE);
        String imageUri2 = pref2.getString("uri", "");

        if (!imageUri2.equals("")) {
            Uri contentUri = Uri.parse(imageUri2);
            this.drawerBanner.setImageURI(contentUri);
        }
    }

    public void setIoItemRecyclerView(Context context) {
        // 用于存储recyclerView的日期
        GlobalVariables.setmDate("");

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);    // 列表从底部开始展示，反转后从上方开始展示
        layoutManager.setReverseLayout(true);   // 列表反转

        ioItemRecyclerView.setLayoutManager(layoutManager);
        ioAdapter = new IOItemAdapter(ioItemList);
        ioItemRecyclerView.setAdapter(ioAdapter);
//        ioTouchHelper.attachToRecyclerView(ioItemRecyclerView);


        //添加监听
        ioAdapter.setOnClickListener(new IOItemAdapter.MyOnClickListener() {
            @Override
            public void OnItemClickListener(final View view, final int position) {
                final IOItem ioItem = ioAdapter.mIOItemList.get(position);
                // 弹窗确认
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage("请选择对【" + ioItem.getName() + (ioItem.getType() == -1 ? "支出" : "收入")
                        + ":" + " " + ioItem.getMoney()
                        + "元】的操作");

                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ioAdapter.removeItem(position);
                        // 刷新界面
                        onResume();
                    }
                }).setNeutralButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddItemActivity.start(getActivity(), ioItem);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout sonView = (LinearLayout) view;
                        TextView grandsonTextView = (TextView) sonView.findViewById(R.id.iotem_date);
                        // 判断是否应该显示时间
                        if (sonView.findViewById(R.id.date_bar).getVisibility() == View.VISIBLE)
                            GlobalVariables.setmDate("");
                        else GlobalVariables.setmDate(ioAdapter.getItemDate(position));
                        ioAdapter.notifyItemChanged(position);
                    }
                }).show();  // 显示弹窗
            }
        });
        ioAdapter.setOnLongClickListener(new IOItemAdapter.MyOnLongClickListener() {
            @Override
            public void OnItemLongClickListener(final View view, final int position) {

            }
        });


    }

    public void setBookItemRecyclerView(Context context) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        bookItemRecyclerView.setLayoutManager(layoutManager);
        bookItemRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        bookAdapter = new BookItemAdapter(bookItemList);

        bookAdapter.setOnItemClickListener(new BookItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 选中之后的操作
                GlobalVariables.setmBookPos(position);
                GlobalVariables.setmBookId(bookItemList.get(position).getId());
                onResume();
                drawerLayout.closeDrawer(bookLinearLayout);
            }
        });

        bookItemRecyclerView.setAdapter(bookAdapter);
        bookTouchHelper.attachToRecyclerView(bookItemRecyclerView);

        //GlobalVariables.setmBookId(bookItemRecyclerView.getId());
    }


}
