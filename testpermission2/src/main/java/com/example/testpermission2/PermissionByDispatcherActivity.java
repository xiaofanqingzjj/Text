package com.example.testpermission2;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


/**
 * 使用PermissionDispatcher申请权限例子
 *
 * @author  fortunexiao
 */
@RuntimePermissions
public class PermissionByDispatcherActivity extends Activity {

    static final String TAG = "PermissionByDispatcher";


    /**
     * 给shouldShowRequestPermissionRationale做标识，表示当前处于的情况
     *
     * 请参看：shouldShowRequestPermissionRationale方法
     */
    protected boolean isInOnRequestPermissionsResulting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permision_dispatcher);
        PermissionByDispatcherActivityPermissionsDispatcher.onPermissionGrantedWithPermissionCheck(this);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        isInOnRequestPermissionsResulting = true;

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        PermissionByDispatcherActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,  grantResults);

        isInOnRequestPermissionsResulting = false;
    }


    /**
     * 这个方法有2个作用：
     *
     * 1.在请求系统权限之前是否显示权限说明弹框。如果返回true则在弹系统的权限框之前弹一个自己的权限说明框
     * 2.在权限请求之后，如果权限被拒绝，如果该方法返回false表示用户勾选了不再提示勾选框
     *
     * 所以我们现在遇到了一个恶心的问题：
     *
     * 我们希望所有的权限在申请之前都弹出一个权限说明框，所以我们重写这个方法，强制返回true，这样就使得所有的权限都会弹权限说明。
     *
     * 但是这个方法还有一个功能是表示用户是否勾选了"不再提示"勾选，所以如果强制返回true之后那么PermissionDispatcher框架就没办法监听到是否勾选了"不再提示"框。
     *
     * 解决办法：
     *
     * 因为权限说明是在请求权限的时候使用的.
     * 而"不在提示"是在onRequestPermissionsResult里判断的,所以我们在onRequestPermissionResult增加一个标志，用来告诉shouldShowRequestPermissionRationale方法，当前这次调用的目的。
     *
     * @param permission
     * @return
     */
    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        boolean superResult = super.shouldShowRequestPermissionRationale(permission);
        Log.d("PermissionDispatcher", "shouldShowRequestPermissionRationale super:" + superResult);

        if (!isInOnRequestPermissionsResulting) { // 为true的时候当前调用该方法是用来判断用户是否勾选了"不再提示"，false的时候表示询问当前是否弹权限说明
            superResult = true;
        }

        Log.d("PermissionDispatcher", "shouldShowRequestPermissionRationale return :" + superResult);
        return  superResult;
    }



    /**
     * 显示申请某个权限的权限说明
     * @param request
     */
    @OnShowRationale({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onShowPermissionsRatianaleaaa(final PermissionRequest request) {
        Log.i(TAG, "onShowRatianalaa");

        RuntimePermissionHelper.INSTANCE.showRationalDialog(this, "读电话状态和写文件", new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, null, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                request.proceed();
                return null;
            }
        });

//        RuntimePermissionHelper.INSTANCE.showRationalDialog(this, "读电话状态和写文件", null, null) {
//
//        };
    }


    /**
     * 某个行为需要获取了权限之后才能继续
     */
    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPermissionGranted() {
        Log.i(TAG, "onRequestPermissionsResult");
        toast("成功获取了权限");

    }

    /**
     * 用户选择了拒绝
     */
    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPermissionRefuse() {
        Log.i(TAG, "onPermissionDeny");
        toast("用户拒绝了授权");
    }

    /**
     * 用户选择了拒绝，且勾选了不再提示
     */
    @OnNeverAskAgain({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onNeverAskPermission() {
        Log.i(TAG, "onPermissionAndNeverAskAgain");
        toast("用户拒绝了授权且勾选了不再提示");
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
