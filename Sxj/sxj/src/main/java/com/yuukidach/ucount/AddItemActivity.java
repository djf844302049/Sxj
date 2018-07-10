package com.yuukidach.ucount;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yuukidach.ucount.model.BookItem;
import com.yuukidach.ucount.model.IOItem;
import com.yzrj.app.suixinji.R;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {
    private static final String TAG = "AddItemActivity";

    private FragmentManager manager;
    private FragmentTransaction transaction;

    private Button addCostBtn;
    private Button addEarnBtn;
    private Button clearBtn;
    private Button addSaveBtn;
    // private ImageButton addFinishBtn;

    TextView tv_description;

    private ImageView bannerImage;
    private TextView bannerText;
    RelativeLayout bannerLayout;

    private TextView moneyText;

    private TextView words;

    private SimpleDateFormat formatItem = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
    private SimpleDateFormat formatSum = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    IOItem oldioItem;

    public static void start(Context context, IOItem ioItem) {
        Intent starter = new Intent(context, AddItemActivity.class);
        starter.putExtra("IOItem", ioItem);
        context.startActivity(starter);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        try {
            Intent intent = getIntent();
            oldioItem = (IOItem) intent.getSerializableExtra("IOItem");
        } catch (Exception e) {

        }

        addCostBtn = (Button) findViewById(R.id.add_cost_button);
        addEarnBtn = (Button) findViewById(R.id.add_earn_button);
        // addFinishBtn   = (ImageButton) findViewById(R.id.add_finish);
        addSaveBtn = (Button) findViewById(R.id.add_save);
        RelativeLayout calculator_banner = (RelativeLayout) findViewById(R.id.calculator_banner);
        tv_description = (TextView) findViewById(R.id.tv_description);

        clearBtn = (Button) findViewById(R.id.clear);
        words = (TextView) findViewById(R.id.anime_words);
        // 设置字体颜色
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/chinese_character.ttf");
        clearBtn.setTypeface(typeface);
        words.setTypeface(typeface);
        // 设置按钮监听
        addCostBtn.setOnClickListener(new ButtonListener());
        addEarnBtn.setOnClickListener(new ButtonListener());
        //addFinishBtn.setOnClickListener(new ButtonListener());
        addSaveBtn.setOnClickListener(new ButtonListener());

        calculator_banner.setOnClickListener(new ButtonListener());
        clearBtn.setOnClickListener(new ButtonListener());


        bannerText = (TextView) findViewById(R.id.chosen_title);
        bannerImage = (ImageView) findViewById(R.id.chosen_image);
        bannerLayout = (RelativeLayout) findViewById(R.id.have_chosen);

        moneyText = (TextView) findViewById(R.id.input_money_text);


        manager = getSupportFragmentManager();

        transaction = manager.beginTransaction();
        transaction.replace(R.id.item_fragment, new CostFragment());
        transaction.commit();
        if (oldioItem != null) {
            GlobalVariables.setmInputMoney(oldioItem.getMoney() + "");
            GlobalVariables.setmBookId(oldioItem.getBookId());
            GlobalVariables.setmDescription(oldioItem.getDescription());
            if (oldioItem.getType() == -1) {
                addCostBtn.setTextColor(0xffff8c00); // 设置“收入“按钮为灰色
                addEarnBtn.setTextColor(0xff908070); // 设置“支出”按钮为橙色
                transaction.replace(R.id.item_fragment, new CostFragment());
            } else {
                addEarnBtn.setTextColor(0xffff8c00); // 设置“支出“按钮为灰色
                addCostBtn.setTextColor(0xff908070); // 设置“收入”按钮为橙色
                transaction.replace(R.id.item_fragment, new EarnFragment());
            }
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    bannerImage.setImageResource(oldioItem.getSrcId());
                    bannerText.setText(oldioItem.getName());
                    bannerImage.setTag(oldioItem.getType());                        // 保留图片资源属性，-1表示支出
                    bannerText.setTag(oldioItem.getSrcName());      // 保留图片资源名称作为标签，方便以后调用
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), oldioItem.getSrcId());
                    Palette.Builder pb = new Palette.Builder(bm);
                    pb.maximumColorCount(1);
                    // 获取图片颜色并改变上方banner的背景色
                    pb.generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            Palette.Swatch swatch = palette.getSwatches().get(0);
                            if (swatch != null) {
                                bannerLayout.setBackgroundColor(swatch.getRgb());
                            } else {
                                Log.d(TAG, "changeBanner: ");
                            }
                        }
                    });
                }
            }, 500);

        }

        try {
            moneyText.setText(decimalFormat.format(Double.valueOf(GlobalVariables.getmInputMoney())));
        } catch (Exception e) {
            moneyText.setText("0");
        }
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            transaction = manager.beginTransaction();

            switch (view.getId()) {
                case R.id.add_cost_button:
                    addCostBtn.setTextColor(0xffff8c00); // 设置“收入“按钮为灰色
                    addEarnBtn.setTextColor(0xff908070); // 设置“支出”按钮为橙色
                    transaction.replace(R.id.item_fragment, new CostFragment());
                    Log.d(TAG, "onClick: add_cost_button");
                    break;
                case R.id.add_earn_button:
                    addEarnBtn.setTextColor(0xffff8c00); // 设置“支出“按钮为灰色
                    addCostBtn.setTextColor(0xff908070); // 设置“收入”按钮为橙色
                    transaction.replace(R.id.item_fragment, new EarnFragment());
                    Log.d(TAG, "onClick: add_earn_button");

                    break;
               /* case R.id.add_finish:
                    String moneyString =  moneyText.getText().toString();
                    if (moneyString.equals("0.00") || GlobalVariables.getmInputMoney().equals(""))
                        Toast.makeText(getApplicationContext(),"唔姆，你还没输入金额",Toast.LENGTH_SHORT).show();
                    else {
                        putItemInData(Double.parseDouble(moneyText.getText().toString()));
                        calculatorClear();
                        finish();
                    }
                    break;*/
                case R.id.add_save:
                    String moneyString = moneyText.getText().toString();
                    if (moneyString.equals("0.00") || GlobalVariables.getmInputMoney().equals(""))
                        Toast.makeText(getApplicationContext(), "你还没输入金额", Toast.LENGTH_SHORT).show();
                    else {
                        putItemInData(Double.parseDouble(moneyText.getText().toString()));
                        calculatorClear();
                        finish();
                    }
                    break;
                case R.id.clear:
                    calculatorClear();
                    moneyText.setText("0.00");
                    break;
                case R.id.calculator_banner:
                    Intent intent = new Intent(AddItemActivity.this, AddDescription.class);
                    startActivity(intent);
            }

            transaction.commit();
        }
    }

    public void putItemInData(double money) {
        IOItem ioItem = new IOItem();
        BookItem bookItem = DataSupport.find(BookItem.class, GlobalVariables.getmBookId());
        String tagName = (String) bannerText.getTag();
        int tagType = (int) bannerImage.getTag();

        if (tagType < 0) {
            ioItem.setType(ioItem.TYPE_COST);
        } else {
            ioItem.setType(ioItem.TYPE_EARN);
        }
        ioItem.setName(bannerText.getText().toString());
        ioItem.setSrcName(tagName);
        ioItem.setMoney(money);
        ioItem.setDescription(GlobalVariables.getmDescription());
        ioItem.setBookId(GlobalVariables.getmBookId());


        if (oldioItem != null) {
            ioItem.setTimeStamp(oldioItem.getTimeStamp());         // 存储记账时间
            ioItem.setId(oldioItem.getId());
            ioItem.update(oldioItem.getId());
            // 将收支存储在对应账本下
//           bookItem.getIoItemList().add(ioItem);
            bookItem.setSumAll(bookItem.getSumAll() + money * ioItem.getType()
                    - oldioItem.getMoney() * oldioItem.getType());
            bookItem.save();
        } else {
            ioItem.setTimeStamp(formatItem.format(new Date()));         // 存储记账时间
            ioItem.save();
            // 将收支存储在对应账本下
            bookItem.getIoItemList().add(ioItem);
            bookItem.setSumAll(bookItem.getSumAll() + money * ioItem.getType());
            bookItem.save();
        }
        calculateMonthlyMoney(bookItem, ioItem.getType(), ioItem);

        // 存储完之后及时清空备注
        GlobalVariables.setmDescription("");
    }

    // 计算月收支
    public void calculateMonthlyMoney(BookItem bookItem, int money_type, IOItem ioItem) {
        String sumDate = formatSum.format(new Date());
        try {
            Date date = formatItem.parse(ioItem.getTimeStamp());
            Date date2 = formatSum.parse(bookItem.getDate());
            if (TextUtils.isEmpty(bookItem.getDate()) || date.getTime() >= date2.getTime()) {
                // 求取月收支类型
                if (bookItem.getDate().equals(ioItem.getTimeStamp().substring(0, 8))) {
                    if (oldioItem != null) {
                        if (oldioItem.getType() == 1) {
                            bookItem.setSumMonthlyEarn(bookItem.getSumMonthlyEarn() - oldioItem.getMoney());
                        } else {
                            bookItem.setSumMonthlyCost(bookItem.getSumMonthlyCost() - oldioItem.getMoney());
                        }
                    }
                    if (money_type == 1) {
                        bookItem.setSumMonthlyEarn(bookItem.getSumMonthlyEarn() + ioItem.getMoney());
                    } else {
                        bookItem.setSumMonthlyCost(bookItem.getSumMonthlyCost() + ioItem.getMoney());
                    }
                } else {
                    if (money_type == 1) {
                        bookItem.setSumMonthlyEarn(ioItem.getMoney());
                        bookItem.setSumMonthlyCost(0.0);
                    } else {
                        bookItem.setSumMonthlyCost(ioItem.getMoney());
                        bookItem.setSumMonthlyEarn(0.0);
                    }
                    bookItem.setDate(sumDate);
                }
                bookItem.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // 数字输入按钮
    public void calculatorNumOnclick(View v) {
        Button view = (Button) v;
        String digit = view.getText().toString();
        String money = GlobalVariables.getmInputMoney();
        if (GlobalVariables.getmHasDot() && GlobalVariables.getmInputMoney().length() > 2) {
            String dot = money.substring(money.length() - 3, money.length() - 2);
            Log.d(TAG, "calculatorNumOnclick: " + dot);
            if (dot.equals(".")) {
                Toast.makeText(getApplicationContext(), "唔，已经不能继续输入了", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        GlobalVariables.setmInputMoney(money + digit);
        moneyText.setText(decimalFormat.format(Double.valueOf(GlobalVariables.getmInputMoney())));
    }

    // 清零按钮
    public void calculatorClear() {
        GlobalVariables.setmInputMoney("");
        GlobalVariables.setHasDot(false);
    }

    // 小数点处理工作
    public void calculatorPushDot(View view) {
        if (GlobalVariables.getmHasDot()) {
            Toast.makeText(getApplicationContext(), "已经输入过小数点了 ━ω━●", Toast.LENGTH_SHORT).show();
        } else {
            GlobalVariables.setmInputMoney(GlobalVariables.getmInputMoney() + ".");
            GlobalVariables.setHasDot(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (!TextUtils.isEmpty(GlobalVariables.getmDescription())) {
                tv_description.setText("备注：" + GlobalVariables.getmDescription());
            }
        } catch (Exception e) {
        }
    }
}