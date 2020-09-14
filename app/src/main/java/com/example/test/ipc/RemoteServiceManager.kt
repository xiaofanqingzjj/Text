package com.example.test.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.fortunexiao.tktx.async


/**
 * 一个调用远程进程方法的简单封装
 *
 * @author fortunexiao
 */
class RemoteServiceManager(private var context: Context)  {

    companion object {
        const val TAG = "RemoteServiceManager"

        private var instance: RemoteServiceManager? = null

        fun getInstance(context: Context): RemoteServiceManager {
            val appContext = context.applicationContext
            if (instance == null) {
                instance = RemoteServiceManager(appContext)
            }
            return instance!!
        }

        val lock = java.lang.Object()
    }

    /**
     *
     */
    private var mRemoteServiceInterface: IRemoteServiceInterface? = null

    private val remoteServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected")
            mRemoteServiceInterface = IRemoteServiceInterface.Stub.asInterface(service)

            synchronized(lock) {
                Log.d(TAG, "onServiceConnected thread:${Thread.currentThread()}")
                lock.notifyAll()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e(TAG, "onServiceDisconnected")
            mRemoteServiceInterface = null
        }
    }



    init {
        bindService(context)
    }



//    private fun



    private fun bindService(context: Context) {
        async {
            Log.d(TAG, "Begin BindService")
            val service = Intent(context, RemoteService::class.java)
            context.bindService(service, remoteServiceConnection, Context.BIND_AUTO_CREATE)
            Log.d(TAG, "after bindService")
        }

    }





    private val requestBooksCallback = object : IRequestBooksCallback.Stub() {
        override fun onSuccess(books: MutableList<Book>?) {
            Log.d(TAG, "getAllBooks from remote service:$books")
        }
    }


    fun setBookName() {

        checkConnectionState()

        val book = Book(1, "b")
        mRemoteServiceInterface?.setBookName(book, null, null, "bbb")
        Log.d(TAG, "afterSet:$book")
    }


    fun getAllBooks() {

        checkConnectionState()

        // 这里是不能想正常的异步调用那样传递匿名对象的

        // 这里需要在这边的进程中实现一个IBinder回传过去，所以要扩张Stub的方式来创建aidl接口的实现

        // 不能传匿名对象过去

        // 奇怪，这里一直取不到对方进程回调过来的数据

        // 原来是aidl的参数tag弄错了
        mRemoteServiceInterface?.requestAllBooks(object : IRequestBooksCallback.Stub() {
            override fun onSuccess(books: MutableList<Book>?) {
                Log.d(TAG, "getAllBooks from remote service:$books")
            }
        })

        val resutl = mRemoteServiceInterface?.add(3, 5)

        Log.d(TAG, "result:$resutl")

//        IRequestBooksCallback.Stub.as

    }



    private fun checkConnectionState() {
        if (!isServiceAlive(mRemoteServiceInterface?.asBinder())) {
            // 如果连接不存在，则重新bind下服务
            bindService(context)

            while (!isServiceAlive(mRemoteServiceInterface?.asBinder())) {

                Log.d(TAG, "remoteServiceInterface:$mRemoteServiceInterface")

                synchronized(lock) {
                    Log.d(TAG, "checkConnectionState thread:${Thread.currentThread()}")
                    lock.wait(300L)
                }
            }

        }
    }


    private fun isServiceAlive(binder: IBinder?): Boolean {
        Log.d(TAG, "isServiceAlive:$binder")
        return binder != null && binder.isBinderAlive && binder.pingBinder()
    }




}