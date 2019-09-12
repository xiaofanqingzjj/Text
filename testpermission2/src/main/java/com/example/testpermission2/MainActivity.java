package com.example.testpermission2;

import android.Manifest;
import android.app.Activity;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends Activity {

    static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Field f = MainActivityPermissionsDispatcher.class.getDeclaredField("REQUEST_ONPERMISSIONGRANTED");
                    f.setAccessible(true);

                    int value = f.getInt(null);


                    Log.d(TAG, "F:" + f + value);

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


            }
        });

//        LayerDrawable


//        MainActivityPermissionsDispatcher.onPermissionGrantedWithPermissionCheck(this);

//        MainActivityPermissionsDispatcher.onPermissionGrantedWithCheck(this);




        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private static int getMainActivityPermissionsDispatcherRequestCode() {
        try {
            final Field f = MainActivityPermissionsDispatcher.class.getDeclaredField("REQUEST_ONPERMISSIONGRANTED");
            f.setAccessible(true);
            int value = f.getInt(null);
            Log.d(TAG, "F:" + f + value);
            return value;
        } catch (Exception e) {

        }
        return -1;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,  grantResults);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        Log.d(TAG, "shouldShowRequestPermissionRationale");
//        return super.shouldShowRequestPermissionRationale(permission);
        return true;
    }

    @OnShowRationale({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onShowPermissionsRatianaleaaa(final PermissionRequest request) {
        Log.i(TAG, "onShowRatianalaa");
        RuntimePermissionHelper.showRationalDialog(this, "读电话状态和写文件", new RuntimePermissionHelper.onPermissionAction() {
            @Override
            public void onAllow() {
                request.proceed();
            }

            @Override
            public void onCancel() {
                request.cancel();
            }
        });

    }


    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPermissionGranted() {
        Log.i(TAG, "onRequestPermissionsResult");
    }







    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPermissionRefuse() {
        Log.i(TAG, "onPermissionRefuse");
    }

    @OnNeverAskAgain({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onNeverAskPermission() {
        Log.i(TAG, "onNeverAskPermission");
    }
}
