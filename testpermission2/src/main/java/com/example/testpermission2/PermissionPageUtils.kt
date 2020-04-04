package com.example.testpermission2

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast

/**
 * 权限请求页适配，不同手机系统跳转到不同的权限请求页
 *
 * @author  fortunexiao
 */

object PermissionPageUtils {

    private val TAG = "PermissionPageManager"

    fun jumpPermissionPage(context: Context) {

        val name = Build.MANUFACTURER
        Log.e(TAG, "jumpPermissionPage --- name : $name")
        when (name) {
            "HUAWEI" -> goHuaWei(context)
            "vivo" -> goVivo(context)
            "OPPO" -> goOppoMainager(context)
            "Coolpad" -> goCoolpadMainager(context)
            "Meizu" -> goMeizuMainager(context)
            "Xiaomi" -> goXiaoMi(context)
            "samsung" -> goSangXinMainager(context)
            "Sony" -> goSonyMainager(context)
            "LG" -> goLGMainager(context)
            else -> goIntentSetting(context)
        }
    }

    private fun goLGMainager(context: Context) {
        val packageName = context.packageName

        try {

            val intent = Intent(packageName)
            val comp = ComponentName("com.android.settings", "com.android.settings.Settings\$AccessLockSummaryActivity")
            intent.component = comp
            context.startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(context, "跳转失败", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            goIntentSetting(context)
        }

    }

    private fun goSonyMainager(context: Context) {
        val packageName = context.packageName
        try {
            val intent = Intent(packageName)
            val comp = ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity")
            intent.component = comp
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "跳转失败", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            goIntentSetting(context)
        }

    }

    private fun goHuaWei(context: Context) {
        try {
            val intent = Intent(context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val comp = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
            intent.component = comp
            context.startActivity(intent)
        } catch (e: Exception) {
            goIntentSetting(context)
        }

    }


    private fun goXiaoMi(context: Context) {
        try { // MIUI 8
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
            intent.putExtra("extra_pkgname", context.packageName)
            context.startActivity(intent)
        } catch (e: Exception) {
            try { // MIUI 5/6/7
                val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
                intent.putExtra("extra_pkgname", context.packageName)
                context.startActivity(intent)
            } catch (e1: Exception) { // 否则跳转到应用详情
                goIntentSetting(context)
            }

        }

    }

    private fun goMeizuMainager(context: Context) {
        try {

            val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.putExtra("packageName", context.packageName)
            context.startActivity(intent)

        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            goIntentSetting(context)
        }

    }

    private fun goSangXinMainager(context: Context) {
        //三星4.3可以直接跳转
        goIntentSetting(context)
    }


    private fun goOppoMainager(context: Context) {
        doStartApplicationWithPackageName(context, "com.coloros.safecenter")
    }

    /**
     * doStartApplicationWithPackageName("com.yulong.android.security:remote")
     * 和Intent open = getPackageManager().getLaunchIntentForPackage("com.yulong.android.security:remote");
     * startActivity(open);
     * 本质上没有什么区别，通过Intent open...打开比调用doStartApplicationWithPackageName方法更快，也是android本身提供的方法
     */
    private fun goCoolpadMainager(context: Context) {
        doStartApplicationWithPackageName(context, "com.yulong.android.security:remote")
        /*  Intent openQQ = getPackageManager().getLaunchIntentForPackage("com.yulong.android.security:remote");
        startActivity(openQQ);*/
    }

    private fun goVivo(context: Context) {
        doStartApplicationWithPackageName(context, "com.bairenkeji.icaller")
    }


    private fun doStartApplicationWithPackageName(mContext: Context, packagename: String) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        var packageinfo: PackageInfo? = null
        try {
            packageinfo = mContext.packageManager.getPackageInfo(packagename, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        if (packageinfo == null) {
            return
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        val resolveIntent = Intent(Intent.ACTION_MAIN, null)
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        resolveIntent.setPackage(packageinfo.packageName)
        // 通过getPackageManager()的queryIntentActivities方法遍历
        val resolveinfoList = mContext.packageManager
                .queryIntentActivities(resolveIntent, 0)
        Log.e("PermissionPageManager", "resolveinfoList" + resolveinfoList.size)

        for (i in resolveinfoList.indices) {
            Log.e("PermissionPageManager", resolveinfoList[i].activityInfo.packageName + resolveinfoList[i].activityInfo.name)
        }

        val iterator = resolveinfoList.iterator()
        if (iterator.hasNext()) {
            val resolveinfo = iterator.next()
            if (resolveinfo != null) {
                // packageName参数2 = 参数 packname
                val packageName = resolveinfo.activityInfo.packageName
                // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packageName参数2.mainActivityname]
                val className = resolveinfo.activityInfo.name

                // LAUNCHER Intent
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                // 设置ComponentName参数1:packageName参数2:MainActivity路径
                val cn = ComponentName(packageName, className)
                intent.component = cn
                try {
                    mContext.startActivity(intent)
                } catch (e: Exception) {
                    goIntentSetting(mContext)
                    e.printStackTrace()
                }

            }
        }
    }


    private fun goIntentSetting(mContext: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", mContext.packageName, null)
        intent.data = uri
        try {
            mContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}