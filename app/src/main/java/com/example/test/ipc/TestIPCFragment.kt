package com.example.test.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import com.bedrock.module_base.MenuFragment
import com.bedrock.module_base.SimpleFragment


class TestIPCFragment : MenuFragment() {

    companion object {
        const val TAG = "TestIPCFragment"
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        addMenu("getAllBooksFromRemote") {
            RemoteServiceManager.getInstance(context!!).getAllBooks()
        }

        addMenu("Set Book Name") {
            RemoteServiceManager.getInstance(context!!).setBookName()
        }

    }


}