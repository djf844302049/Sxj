package com.yzrj.app.suixinji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yzrj.app.suixinji.R;

public class WelcomeActivity extends RxAppCompatActivity {

    private ImageView iv_splash;
    private static final long NONET_TIME = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();
        startAnima();
        initData();

    }



    // private SharedPreferences.Editor sp_edit;
    private void initData() {
//        //判断一下网络状态
//        if (AppUtils.isOnline(this)) {
//            //获取保存的城市用于查询
//            preferences = getSharedPreferences("weathercity", MODE_PRIVATE);
//            String city = preferences.getString("city", "");
//            Observable.combineLatest(NetWork.getBingApi()
//                    .getBingPicPath("0", "ZH-CN"), NetWork.getWeatherApi()
//                    .getWeatherInfo(city, API_KEY), new Func2<ResponseBody, Weather, Boolean>() {
//                @Override
//                public Boolean call(ResponseBody responseBody, Weather weather) {
//                    try {
//                        AppUtils.back_url = GetBingImageUrl(responseBody.string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    // 判断并传值
//                    if (weather.getError_code() == 0)  //查询成功 可以保存
//                    {
//                        AppUtils.today_weather = weather;
//                    }
//                    return true;
//                }
//            }).compose(this.<Boolean>bindToLifecycle())
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<Boolean>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onNext(Boolean aBoolean) {
//
//                        }
//                    });
//        } else {
//
//        }
        //直接进入主界面
        new Handler().postDelayed(new Runnable() {
            public void run() {
                goHome();
            }
        }, NONET_TIME);
    }


    private void initView() {
        iv_splash = (ImageView) findViewById(R.id.iv_splash);
    }


    private void startAnima() {
        ScaleAnimation animation = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(2000);
        animation.setFillAfter(true);
        iv_splash.startAnimation(animation);
    }

    /**
     * 启动
     */
    private void goHome() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity04.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iv_splash.clearAnimation();
        iv_splash = null;
    }
}
