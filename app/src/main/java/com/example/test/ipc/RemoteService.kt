package com.example.test.ipc

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log


class RemoteService : Service() {

    companion object {

        const val TAG = "RemoteService##"
    }



    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "onStartCommand intent:$intent, flags:$flags, startId:$startId")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")

        super.onDestroy()
    }




    override fun onBind(intent: Intent?): IBinder? {


        val binder =  MyInterface()
        Log.d(TAG, "onBind: $binder")
        return  binder
    }


    /**
     * 给外部调用的接口
     */
     class MyInterface : IRemoteServiceInterface.Stub() {

        private val handler = Handler(Looper.getMainLooper())

        override fun add(a: Int, b: Int): Int {
            return a + b
        }

        override fun setBookName(book: Book?, book2: Book?, book3: Book?, name: String?) {
            Log.d(TAG, "setBookName:$book")

            book?.name = name
        }

        override fun requestAllBooks(callback: IRequestBooksCallback?) {

            handler.postDelayed(Runnable {


                callback?.onSuccess(mutableListOf<Book>().apply {
                    add(Book(1, "Book1"))
                    add(Book(2, "Book2"))
                    add(Book(3, "Book3"))
                    add(Book(45, "Book4"))
                })
            }, 300)

//            callback?.onSuccess(mutableListOf<Book>().apply {
//                add(Book(1, "Book1"))
//                add(Book(2, "Book2"))
//                add(Book(3, "Book3"))
//                add(Book(45, "Book4"))
//            })
        }

    }


}