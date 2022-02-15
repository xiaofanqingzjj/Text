package com.tencent.bible.ts

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 *
 * 一个
 *
 * Created by hugozhong on 2020-01-10
 */
class RemoteService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return object : ITreeService.Stub() {
            override fun sayHello() {

            }

            override fun getLeafServiceBinder(serviceName: String): IBinder? {
                return LeafServiceManagerServer.getLeafServiceProvider(serviceName)?.provideBinder()
            }
        }
    }
}
