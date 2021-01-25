package com.mingwei.aptdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mingwe.myanno.BindView;
import com.mingwe.myanno.OnClick;
import com.mingwei.myapi.ButterKnife;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";

    @BindView(R.id.btn1)
    public Button mBtn;
    @BindView(R.id.btn2)
    public Button mBtn2;

    @BindView(R.id.btn3)
    public Button btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        GameHelperPluginCenter.init();

        mBtn.setText("按钮");
        mBtn2.setText("按钮2");

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map map = GameHelperPluginCenter.instance.pluginsMap;
                Log.d(TAG, "map size:" + map);
            }
        });
    }

    @OnClick({R.id.btn1})
    public void click() {
        Toast.makeText(this, "show", Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.btn2})
    public void click2() {
        Toast.makeText(this, "show2", Toast.LENGTH_SHORT).show();
    }

}
