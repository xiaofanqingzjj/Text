package com.example.testpermission2

import android.Manifest
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.example.testpermission2.my.Permissions


/**
 * 使用PermissionDispatcher申请权限例子
 *
 * @author  fortunexiao
 */
class PermissionByMyPermissionsActivity : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permision_dispatcher)

        val permisions = Permissions()
        permisions.request(activity = this,
                permissions = arrayOf(Manifest.permission.READ_CONTACTS),
                onShowRationale = {
                    RuntimePermissionHelper.showRationalDialog(this, "读电话状态和写文件", arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE), null) {
                        it.proceed()
                        null
                    }
                },
                onGranted = {
                    toast("已正确授权")
                },
                onDeny = {
                    toast("用户选了拒绝")
                },
                onDenyAndNeverAskAgain =  {
                    toast("用户选了拒绝且勾选了不再提示")
//                    RuntimePermissionHelper.refuseForeverPromptOpen(this, "读电话状态和写文件", arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE), null) {
//                        it.proceed()
//                        null
//                    }
                })
    }


    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


}
