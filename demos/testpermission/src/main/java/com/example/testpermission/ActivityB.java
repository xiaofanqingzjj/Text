package com.example.testpermission;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;


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
