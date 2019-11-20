package com.example.testpermission2

import android.content.Intent
import android.os.Bundle

class MainActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenu("System API") {
            startActivity(Intent(this@MainActivity, PermissionBySystemApiActivity::class.java))
        }

        addMenu("PermissionsDispatcher") {
            startActivity(Intent(this@MainActivity, PermissionByDispatcherActivity::class.java))
        }

        addMenu("My PermissionsRequestor") {
            startActivity(Intent(this@MainActivity, PermissionByMyPermissionsActivity::class.java))
        }
    }

}
