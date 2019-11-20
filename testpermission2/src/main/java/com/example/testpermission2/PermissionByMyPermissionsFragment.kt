package com.example.testpermission2

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tencent.permissionsrequestor.PermissionsRequestor


/**
 * 使用PermissionDispatcher申请权限例子
 *
 * @author  fortunexiao
 */
class PermissionByMyPermissionsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val fragmentActivity = activity!!

        val permissions = arrayOf(Manifest.permission.READ_CONTACTS)
        val permissionsRequestor = PermissionsRequestor(this)
        permissionsRequestor.request(
                permissions = permissions,
                onShowRationale = {
                    RuntimePermissionHelper.showRationalDialog(fragmentActivity, "读电话状态和写文件", arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE), null) {
                        it.proceed()
                        null
                    }
                },
                onGranted = {
                    toast("已正确授权")
                },
                onDeny = {
                    if (!it) {
                        toast("用户选了拒绝")
                        RuntimePermissionHelper.showStartPermissionsDeny(context = fragmentActivity, onKnownItClickListener = View.OnClickListener {


                        }, onSetClickListener = View.OnClickListener {

                            // 继续请求
                            permissionsRequestor.requestDirect(permissions)

                        })
                    } else  {
                        toast("用户选了拒绝且勾选了不再提示")
                    }
                })
    }


    private fun toast(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}
