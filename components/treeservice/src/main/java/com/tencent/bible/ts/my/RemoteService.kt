package com.tencent.bible.ts.my

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.fortunexiao.treeservice.IRemoteService
import com.tencent.bible.ts.ITreeService
import com.tencent.bible.ts.RemoteServiceManager






// 对外的接口




class RemoteService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return object : IRemoteService.Stub() {
            override fun getRemoteService(name: String?): IBinder? {
                // 返回这个进程的一个接口对象
                return null;
            }

            override fun sayHello(hello: String?) {
            }
        }
    }
}

//@RemoteInterface
//interface  LoginManager {
//    fun getUserId(): String?
//}

/**
 * 对外提供的aidl使用类
 *
 * 也就是一个Aidl的代理类
 *
 * 这个类依赖一个服务，依赖一个aidl接口
 *
 */
object  RemoteServiceManager : IRemoteService {

    private const val TAG = "RemoteServiceProxy"

    private val SERVICE_LOCK = Object() // 服务连接同步锁

    private var context: Context? = null

    // 远程服务Binder
    private  var remoteServiceBinder: IRemoteService? = null;



    /**
     * 启动远程服务
     */
    fun start(context: Context) {
        this.context = context.applicationContext;
        bindService()
    }



    override fun getRemoteService(name: String?): IBinder? {
        return getRemoteService()?.getRemoteService(name)
    }

    override fun sayHello(hello: String?) {
        getRemoteService()?.sayHello(hello)
    }

    override fun asBinder(): IBinder? {
        throw java.lang.Exception("Don't call this method")
        return null;
    }


    /**
     * 获取远程服务的Aidl的实现对象
     *
     */
    fun getRemoteService(): IRemoteService? {
        // 先看服务是否存在，且是否存活
        if (!isRemoteServiceAlive()) {

            // 如果服务无效，则启动服务，等待服务连接事件
            var count = 0
            bindService()

            // 等服务连接成功
            while (!isRemoteServiceAlive()) {
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
        return remoteServiceBinder;
    }


    private val serviceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            remoteServiceBinder = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            remoteServiceBinder = IRemoteService.Stub.asInterface(service)

            synchronized(SERVICE_LOCK) {
                SERVICE_LOCK.notifyAll()
            }
        }
    }

    /**
     * 启动服务
     */
    private fun bindService(): Boolean {
        return context?.bindService(
                Intent(context, RemoteService::class.java),
                serviceConnection,
                Service.BIND_AUTO_CREATE   ) ?: false
    }



    /**
     * 服务是否存活
     */
    private fun isRemoteServiceAlive(): Boolean {
        var isAlive = remoteServiceBinder?.asBinder()?.isBinderAlive ?: false
        if (isAlive) {
            try {
                remoteServiceBinder?.sayHello("Hello")
            } catch (e: Throwable) {
                isAlive = false
                unbindService()
            }
        }
        return isAlive
    }

    /**
     * 断开连接
     */
    private fun unbindService() {
        try {
            context?.unbindService(serviceConnection)
            remoteServiceBinder = null
        } catch (tr: Throwable) {
        }
    }


}