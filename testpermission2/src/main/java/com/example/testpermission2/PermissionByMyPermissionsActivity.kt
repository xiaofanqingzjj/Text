package com.example.testpermission2

import android.os.Bundle
import androidx.core.app.FragmentActivity


/**
 * 使用PermissionDispatcher申请权限例子
 *
 * @author  fortunexiao
 */
class PermissionByMyPermissionsActivity : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permision_dispatcher)

        supportFragmentManager.beginTransaction().add(R.id.container, PermissionByMyPermissionsFragment()).commitNowAllowingStateLoss()


    }


}
