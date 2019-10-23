package com.example.testpermission2

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.View
import java.util.*

/**
 * 动态权限管理辅助类
 *
 * Created by fortune on 2017/8/7.
 */
object RuntimePermissionHelper {

    private val PERMISSION_DESCS = HashMap<String, String>()

    init {
        PERMISSION_DESCS[Manifest.permission.CAMERA] = "拍摄图片上传时会涉及该功能服务"
        PERMISSION_DESCS[Manifest.permission.ACCESS_FINE_LOCATION] = "发送当前位置的功能，社区有附近的功能"
        PERMISSION_DESCS[Manifest.permission.WRITE_EXTERNAL_STORAGE] = "一零零一需要获取【存储】权限，读取和写入本地相册或是其他文件操作"
    }



    // ==============================================================================================================
    // ========================================  启动权限  ===========================================
    // ==============================================================================================================

    /**
     * 启动的时候申请2个权限的原理提示
     */
    fun showStartPermissionsRationalDialog(context: Context, onNextClickListener: View.OnClickListener) {
//        StartRuntimePermissionTipsDialog.show(context, onNextClickListener)
    }

    /**
     * 启动时候用户拒绝了权限
     *
     * @param
     * @param onKnownItClickListener 用户点击了知道了
     * @param onSetClickListener 用户点了设置，如果是拒绝点时候点击设置重新请求权限，如果用户是在勾选了不再提示时候点击了设置跳转到app的设置权限界面
     */
    fun showStartPermissionsDeny(context: Context, onKnownItClickListener: View.OnClickListener?, onSetClickListener: View.OnClickListener?) {
        AlertDialog.Builder(context)
                .setTitle("权限设置")
                .setMessage("一零零一使用存储和电话状态权限仅用于校验IMEI码和存储离线书籍、图片等以保证您在APP内功能的正常使用。")
                .setPositiveButton("去设置", DialogInterface.OnClickListener { dialog, which ->
                    onSetClickListener?.onClick(null)
                })
                .setNegativeButton("我知道了", DialogInterface.OnClickListener { dialog, which ->
                    onKnownItClickListener?.onClick(null)
                }).create().show()

    }


    // ==============================================================================================================
    // ==============================================================================================================
    // ==============================================================================================================


    /**
     * 在使用的过程中动态申请权限的权限使用说明，用户点确定后继续走申请流程
     *
     * @param context
     * @param permissionName 要申请的权限名称
     * @param permissions    权限
     * @param permissionDesc 使用说明，如果为空，则根据对应的权限获取一个默认的说明
     */
    fun showRationalDialog(context: Context?, permissionName: String, permissions: Array<String>?, permissionDesc: String?, process: (()->Unit)? = null) {
        var permissionDesc = permissionDesc
        if (context == null) {
            return
        }

        if (TextUtils.isEmpty(permissionDesc) && permissions != null && permissions.isNotEmpty()) {
            permissionDesc = PERMISSION_DESCS[permissions[0]]
        }

        AlertDialog.Builder(context)
                .setTitle("允许使用【$permissionName】功能")
                .setMessage(permissionDesc)
                .setPositiveButton("确定") { dialog, which ->
                    process?.invoke()
                }
                .create().show()

    }


    /**
     * 在使用过程中请求权限，用户选择了拒绝
     */
    fun normalPermissionDenied(activity: Activity, permissionName: String, permission: Array<String>) {
        //do nothing
    }



    /**
     * 在使用的过程中请求权限，用户选择了拒绝且勾选了不再提示
     *
     * 或者用户在上一次勾选了不再提示，重新运行需要权限点方法
     *
     * 提示用户为什么要使用该权限，并有"去设置"和"知道了"两个按钮
     *
     * 点去设置进入系统点设置界面，引导用户开启权限
     * 点知道了啥也不干
     *
     * @param context
     * @param permissionName
     */
    fun refuseForeverPromptOpen(context: Context?, permissionName: String?, permissions: Array<String>, permissionDesc: String?) {
        var permissionDesc = permissionDesc

        if (context == null) {
            return
        }

        if (TextUtils.isEmpty(permissionDesc)) {
            permissionDesc = PERMISSION_DESCS[permissions[0]]
        }

        AlertDialog.Builder(context)
                .setTitle("允许使用【$permissionName】功能")
                .setMessage(permissionDesc)
                .setPositiveButton("去设置") { _, _ ->
                    PermissionPageUtils.jumpPermissionPage(context)
                }
                .setNegativeButton("我知道了") { _, _->}
                .create().show()
    }
}
