package com.tencent.bible.ts

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

/**
 * Created by hugozhong on 2020-01-10
 */
object TreeServiceManager {

    private const val TAG = "TreeServiceManager"

    private lateinit var context: Context

    private var serviceBinder: ITreeService? = null
    private var treeServiceClass: Class<out TreeService> = TreeService::class.java

    private val SERVICE_LOCK = Object() // 服务连接同步锁

    fun getLeafServiceBinder(serviceName: String?, retryOnFail: Boolean = true): IBinder? {
        try {
            return obtainServiceManager()?.getLeafServiceBinder(serviceName)
        } catch (e: Throwable) {
            Log.e(TAG, "getLeafServiceBinder failed  :${e.message}, retryOnFail:$retryOnFail", e)
            unbindService()
            if (retryOnFail) {
                Log.i(TAG,"start to retry getLeafServiceBinder")
                return getLeafServiceBinder(serviceName, false)
            }
        }
        return null
    }

    fun sayHello() {
        obtainServiceManager()?.sayHello()
    }

    fun start(
        context: Context,
        treeServiceClass: Class<out TreeService> = TreeService::class.java
    ) {
        // zhc ue
        this.context = context.applicationContext
        this.treeServiceClass = treeServiceClass
        bindService()
    }


    private val serviceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBinder = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            serviceBinder = ITreeService.Stub.asInterface(service)
            synchronized(SERVICE_LOCK) {
                SERVICE_LOCK.notifyAll()
            }
        }
    }

    private fun bindService(): Boolean {
        return context.bindService(
            Intent(context, treeServiceClass),
            serviceConnection,
            Service.BIND_AUTO_CREATE
        )
    }

    private fun unbindService() {
        try {
            context.unbindService(serviceConnection)
            serviceBinder = null
        } catch (tr: Throwable) {
        }
    }

    private fun obtainServiceManager(): ITreeService? {
        if (!isServiceManagerAlive()) {
            var count = 0
            bindService()
            // 如果服务无效，则启动服务，等待服务连接事件
            while (!isServiceManagerAlive()) {
                try {
                    if (++count > 5) {
                        //                        throw new IllegalStateException("failed to bind TreeService(reach max retry times).");
                        break
                    }
                    synchronized(SERVICE_LOCK) {
                        try {
                            SERVICE_LOCK.wait(300L)
                        } catch (e: InterruptedException) {

                        }

                    }
                } catch (e: Exception) {
                    Log.e(TAG, "bindService(Reason.Restart) exception  :" + e.message)
                    //throw new IllegalStateException("failed to bind TreeService(by exception).", e);
                }

            }
        }
        return serviceBinder
    }

    private fun isServiceManagerAlive(): Boolean {
        var isAlive = serviceBinder != null && serviceBinder!!.asBinder().isBinderAlive
        if (isAlive) {
            // test again
            try {
                serviceBinder!!.sayHello()
            } catch (e: Throwable) {
                Log.e(TAG, "say hello failed  :${e.message}", e)
                isAlive = false
                unbindService()
            }
        }
        return isAlive
    }

}