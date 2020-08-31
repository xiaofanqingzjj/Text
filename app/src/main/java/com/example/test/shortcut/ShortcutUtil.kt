package com.example.test.shortcut

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Parcelable
import android.util.Log
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.test.R


object ShortcutUtil {

    const val TAG = "ShortcutUtil"


    //创建桌面快捷方式
    fun createShortCut(context: Context) {
//        //创建Intent对象
//        val shortcutIntent = Intent()
//
//        //设置点击快捷方式，进入指定的Activity
//        //注意：因为是从Lanucher中启动，所以这里用到了ComponentName
//        //其中new ComponentName这里的第二个参数，是Activity的全路径名，也就是包名类名要写全。
//        shortcutIntent.setComponent(ComponentName(context.getPackageName(), "com.example.test.shortcut.ShortcutActivity"))
//
//        //给Intent添加 对应的flag
//        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NEW_TASK)
//
//        val resultIntent = Intent()
//
//        // Intent.ShortcutIconResource.fromContext 这个就是设置快捷方式的图标
//        resultIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher))
//        //启动的Intent
//        resultIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
//
//        //这里可以设置快捷方式的名称
//        resultIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "这是一个测试的快捷方式")
//
//        //设置Action
//        resultIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT")
//
//        //发送广播、通知系统创建桌面快捷方式
//        context.sendBroadcast(resultIntent)


//        val shortcutintent = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
//        //不允许重复创建
//        shortcutintent.putExtra("duplicate", false)
//        //需要现实的名称
//        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "这是一个测试的快捷方式")
//        //快捷图片
//        val icon: Parcelable = Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher)
//        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon)
//        //点击快捷图片，运行的程序主入口
//        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, Intent(context, ShortcutActivity::class.java))
//        //发送广播。OK
//        context.sendBroadcast(shortcutintent)

        val iconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_rank_1)


        Log.d(TAG, "icon:$iconBitmap")

        addShortcut(context,
                name = "Test ShortCut2",
                icon = iconBitmap,
                actionIntent = Intent(context, ShortcutActivity::class.java).apply {
                    action = Intent.ACTION_CREATE_SHORTCUT
                })
    }


    const val RC_CREATE_SHORTCUT = 0x33

    fun addShortcut(activity: Context, name: String, icon: Bitmap?, actionIntent: Intent) {

        Log.d(TAG, "addShortcut name:$name, icon:$icon, action:$actionIntent")
//        val shortcutInfo: ShortcutInfo = ShortcutInfo.Builder(activity, name)
//                .setShortLabel(name)
//                .setIcon(Icon.createWithBitmap(icon))
//                .setIntent(actionIntent)
//                .setLongLabel(name)
//                .build()

        val shortcut = ShortcutInfoCompat.Builder(activity, name)
                .setShortLabel(name)
                .setIcon(IconCompat.createWithBitmap(icon))
                .setIntent(actionIntent)
                .setLongLabel(name)
                .build()

        ShortcutManagerCompat.requestPinShortcut(activity, shortcut, null)


//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            //  创建快捷方式的intent广播
//            val shortcut = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
//            // 添加快捷名称
//            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
//            //  快捷图标是允许重复(不一定有效)
//            shortcut.putExtra("duplicate", false)
//            // 快捷图标
//            // 使用资源id方式
////            Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(activity, R.mipmap.icon);
////            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
//
//            // 使用Bitmap对象模式
//            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon)
//            // 添加携带的下次启动要用的Intent信息
//            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent)
//            // 发送广播
//            activity.sendBroadcast(shortcut)
//
//        } else {
//            val shortcutManager: ShortcutManager? = activity.getSystemService(Context.SHORTCUT_SERVICE) as? ShortcutManager
//
//            if (null == shortcutManager) {
//                // 创建快捷方式失败
//                Log.e("MainActivity", "Create shortcut failed")
//                return
//            }
//
//
//            val shortcutInfo: ShortcutInfo = ShortcutInfo.Builder(activity, name)
//                    .setShortLabel(name)
//                    .setIcon(Icon.createWithBitmap(icon))
//                    .setIntent(actionIntent)
//                    .setLongLabel(name)
//                    .build()
//
//            shortcutManager.requestPinShortcut(shortcutInfo, null)
//        }
    }

}