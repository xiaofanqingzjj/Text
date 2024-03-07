package com.example.testaop;

import android.os.Bundle;
import android.util.Log;

import com.bedrock.module_base.MenuActivity;
import com.example.testaop.aop.TimeConsume;
import com.tencent.common.log.TLog;

public class MainActivity extends MenuActivity {

    static  final  String TAG = "MainActivity";

    @Override
    @TimeConsume
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addMenu("Test", () -> {
            int result = testSomeFoo("xiao", 30, false);
            Log.d(TAG, "result:" + result);
            return null;
        });

        addMenu("TestFoo2.foo", ()-> {
            new Foo2().testFoo("FAN", 33, true);
            return null;
        });

        addMenu("Tlog", ()-> {
            TLog.i(TAG, "tlog");
            return null;
        });
    }


    @TimeConsume
    int testSomeFoo(String name, int age, boolean isChild) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return 30;
    }
}