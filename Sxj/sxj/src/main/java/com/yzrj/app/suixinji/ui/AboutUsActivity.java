package com.yzrj.app.suixinji.ui;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yzrj.app.suixinji.R;

public class AboutUsActivity extends AppCompatActivity {

    private Button buttonYin;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_layout);
        textView = findViewById(R.id.text11);
        textView.setClickable(true);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutUsActivity.this);
                builder.setTitle("随心记").setIcon(R.mipmap.ico).setMessage("随心记是一款非常牛逼的应用程序哟");
                builder.create().show();
            }
        });
        buttonYin = findViewById(R.id.btn_yinsi);
        buttonYin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutUsActivity.this,YinSiActivity.class);
                startActivity(intent);
            }
        });
    }
}
