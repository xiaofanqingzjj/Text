package some;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by jesus7_w on 2016/11/22.
 */


/**
 demo:
 PermissionComp.OnPermissionRequestCallBack callback = new PermissionComp.OnPermissionRequestCallBack() {
    @Override
    public void callBack(String permission, boolean hasPermission, boolean shouldShowRequestPermissionRationale) {
    }
}
 
 PermissionComp.requestPermissions(SplashActivity.this, callback,
 Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION);
 */


public class PermissionComp {


    public static final int PERSISSION_REQUEST_CAMERA = 0x01;
    public static final int PERSISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x02;
    public static final int PERSISSION_REQUEST_READ_EXTERNAL_STORAGE = 0x03;

    //android.permission.CAMER
    public static boolean lauchCamera(Context context, Runnable runnable) {
        return exec(context, runnable, Manifest.permission.CAMERA ,PERSISSION_REQUEST_CAMERA);
    }

    public static boolean readExternalStorage(Context context, Runnable runnable) {
        return exec(context, runnable, Manifest.permission.READ_EXTERNAL_STORAGE ,PERSISSION_REQUEST_READ_EXTERNAL_STORAGE);
    }

    public static boolean writeExternalStorage(Context context, Runnable runnable) {
        return exec(context, runnable, Manifest.permission.WRITE_EXTERNAL_STORAGE ,PERSISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
    }


    /**
     *
     * @param context
     * @param hasPermissionRun 权限授权执行的任务
     * @param permission  执行该任务需要的权限
     * @return 是否有执行任务或者请求权限
     */
    public static boolean exec(Context context, final Runnable hasPermissionRun, String permission, int requestCode) {
        if (hasPermission(context, permission)) {
            if (hasPermissionRun != null) hasPermissionRun.run();
            return true;
        }

        requestPermissions(context, new OnPermissionRequestCallBack() {
            @Override
            public void callBack(String permission, boolean hasPermission, boolean shouldShowRequestPermissionRationale) {
                if (hasPermission && hasPermissionRun != null) {
                    hasPermissionRun.run();
                    return;
                }

            }
        }, permission);

        return false;
    }


    /**
     * 请求权限列表
     * @param context
     * @param callBack  权限申请回调
     * @param permissons  申请的权限列表，支持多个
     */
    public static void requestPermissions(Context context, OnPermissionRequestCallBack callBack, String... permissons) {

        ArrayList<String>  needRequestLists = new ArrayList<>();
        for (String permission : permissons) {
            if (hasPermission(context, permission)) {
                if (callBack != null) callBack.callBack(permission, true, false);
                continue;
            }

            manager.put(permission, callBack);
            needRequestLists.add(permission);
        }

        if (needRequestLists.isEmpty()) return;
        PermissionProxyActivity.requestPermissions(context, needRequestLists);
    }

    //是否会显示请求权限的理由
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        return activity.shouldShowRequestPermissionRationale(permission);
    }

    //检查是否有权限
    public static boolean hasPermission(Context context, String permission) {
        if (context == null || TextUtils.isEmpty(permission))
            return true;

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        int permission_int = context.checkSelfPermission(permission);

        if (permission_int == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;
    }

    //打开当前app的应用详情
    public static void gotoAppDetail(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getApplicationInfo().packageName, null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
            context.startActivity(intent);
        }

    }

    private static OnPermissionRequestCallBackManager manager = new OnPermissionRequestCallBackManager();
    private static class OnPermissionRequestCallBackManager {


        HashMap<String, WeakReference<OnPermissionRequestCallBack>> callbacks = new HashMap<>();

        public void put(String permission, OnPermissionRequestCallBack callBack) {
            callbacks.put(permission, new WeakReference(callBack));
        }

        public OnPermissionRequestCallBack get(String permission) {
            if (!callbacks.containsKey(permission)) return null;
            WeakReference<OnPermissionRequestCallBack> wref = callbacks.remove(permission);
            if (wref != null) return wref.get();
            return null;
        }
    }

    public interface OnPermissionRequestCallBack {
        void callBack(String permission, boolean hasPermission, boolean shouldShowRequestPermissionRationale);
    }


    public static void notifyPermissionResult(String permission, boolean hasPermission, boolean shouldShowRequestPermissionRationale) {
        OnPermissionRequestCallBack callBack = manager.get(permission);
        if (callBack != null) callBack.callBack(permission, hasPermission, shouldShowRequestPermissionRationale);
    }
}
