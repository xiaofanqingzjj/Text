package com.example.testpermission2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public class RuntimePermissionHelper {
    public interface OnPermissionGrantRetry {
        void onRetry();
    }

    public interface onPermissionAction {
        void onAllow();
        void onCancel();
    }

    public static void showRationalDialog(Context context, String perm, final onPermissionAction action) {
        String msg = String.format("社区正常运行须使用%s权限", perm);
        try {
            new AlertDialog.Builder(context)
                    .setMessage(msg)
                    .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            action.onAllow();
                        }
                    })
                    .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            action.onCancel();
                        }
                    })
                    .show();
        } catch (Exception e) {

            // 数据上报有这里有这个bug，先catch下
            // android.view.WindowManager$BadTokenException
            //Unable to add window -- token android.os.BinderProxy@432b6aa is not valid; is your activity running?
        }
    }



    public static void noRequiredPermission(final Activity activity, final OnPermissionGrantRetry retry) {
        new AlertDialog.Builder(activity)
                .setMessage("社区正常使用需要电话、访问存储权限")
                .setPositiveButton("重新授权", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (retry != null) {
                            retry.onRetry();
                        }
                    }
                })
                .setNegativeButton("退出社区", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .show();
    }

    public static void neccessaryPermissionRefused(Activity activity, String perm) {
        showToast(activity, "在设置-应用-" + activity.getString(R.string.app_name) + "-权限中开启" + perm + "权限，以正常使用社区功能");
        activity.finish();
    }

    public static void normalPermissionDenied() {
        //do nothing
    }

    public static void refuseForever() {

    }

    public static void refuseForeverPromptOpen(final Activity activity, String perm) {
        new AlertDialog.Builder(activity)
                .setMessage("在设置-应用-" + activity.getString(R.string.app_name) + "-权限中开启\"" + perm + "\"权限，以正常使用社区功能")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startInstalledAppDetailsActivity(activity, activity.getPackageName());

                        activity.finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .show();
    }

    public static void neccessaryPermissionDeniedForever(Activity activity, String perm) {
        showToast(activity, "在设置-应用-\" + activity.getString(R.string.app_name) + \"-权限中开启\"" + perm + "\"权限，以正常使用社区功能");
        activity.finish();
    }

    private static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static boolean startInstalledAppDetailsActivity(final Context context, String pkg) {
        try {
            if (context == null) {
                return false;
            }
            final Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + pkg));
            addCommonFlag(intent);
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static void addCommonFlag(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    }
}