package com.example.testpermission2.my

import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.util.Log


/**
 * 一个动态申请Android敏感权限的类
 *
 * @author fortunexiao
 */
class Permissions {

    companion object {
        const val TAG = "Permissions"
    }


    /**
     * 执行一段需要权限的代码
     */
    fun doWithPermissions(activity: FragmentActivity, permissions: Array<String>, action: ()->Unit) {
        if (permissions.isNullOrEmpty()) {
            action.invoke()
            return
        }

        request(activity, permissions = permissions, onGranted = {
            action.invoke()
        })
    }

    /**
     * 请求某一组敏感权限
     *
     * @param activity
     * @param permissions 权限列表
     * @param isShowRationale 申请权限之前是否显示权限使用说明
     * @param onShowRationale 弹权限说明回调
     * @param onGranted 正确授权
     * @param onDeny 用户选择了拒绝
     * @param onDenyAndNeverAskAgain 用户选择了拒绝且勾选了不再提示
     *
     */
    @JvmOverloads fun request(activity: FragmentActivity,
                permissions: Array<String>,
                isShowRationale: Boolean = false,
                onShowRationale: ((request: PermissionRequest)->Unit)? = null,
                onGranted:(()->Unit)? = null,
                onDeny: (()->Unit)? = null,
                onDenyAndNeverAskAgain: (()->Unit) ? = null) {

        if (permissions.isNullOrEmpty()) { // 如果是空列表直接授权
            onGranted?.invoke()
            return
        }

        val fragment = getPermissionsFragment(activity.supportFragmentManager)

        fragment.onShowRationale = onShowRationale
        fragment.onGranted = onGranted
        fragment.onDeny = onDeny
        fragment.onDenyAndNeverAskAgain = onDenyAndNeverAskAgain

        fragment.requestPermissions(permissions, isShowRationale)
    }

    private fun getPermissionsFragment(fragmentManager: FragmentManager): PermissionProxyFragment {

        var permissionsFragment= fragmentManager.findFragmentByTag(FRAGMENT_TAG) as PermissionProxyFragment?

        Log.d(TAG, "getPermissionsFragment$permissionsFragment")

        if ( permissionsFragment == null) {
            permissionsFragment = PermissionProxyFragment()

            fragmentManager
                    .beginTransaction()
                    .add(permissionsFragment, FRAGMENT_TAG)
                    .commitNowAllowingStateLoss()
        }
        return permissionsFragment
    }


    private  val FRAGMENT_TAG = "PermissionsFragment"

    interface PermissionRequest {
        fun proceed()
        fun cancel()
    }
}
