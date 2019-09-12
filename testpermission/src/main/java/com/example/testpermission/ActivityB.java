package com.example.testpermission;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


public class ActivityB extends FragmentActivity {



    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_b);

    }

//
//    @NeedsPermission(Manifest.permission.CAMERA)
//    void onPermissonGranted() {
//
//    }
//
//    @NeedsPermission(Manifest.permission.CAMERA)
//    void onPermissonDenied() {
//
//    }


}
