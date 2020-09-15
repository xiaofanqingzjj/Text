package com.tencent.bible.ts

import android.os.IBinder

/**
 * Created by hugozhong on 2020-01-10
 */


/**
 *
 */
interface LeafServiceProvider<LeafService> {
    fun provideBinder(): IBinder
    fun provideClientProxy(binder: IBinder?): LeafService
}

internal object LeafServiceManagerServer {

    private val leafServiceProviderMap = mutableMapOf<String, LeafServiceProvider<*>>()


    /**
     *
     */
    fun <LeafService> registeLeafService(
        leafServiceClass: Class<LeafService>,
        leafServiceProvider: LeafServiceProvider<LeafService>
    ) {
        leafServiceProviderMap[leafServiceClass.name] = leafServiceProvider
    }



    fun getLeafServiceBinder(leafServiceName: String): IBinder? {
        return leafServiceProviderMap[leafServiceName]?.provideBinder()
    }
}


object LeafServiceManager {

    private val leafServiceProviderMap = mutableMapOf<String, LeafServiceProvider<*>>()


    /**
     * 注册对外接口
     *
     * @param leafServiceClass
     *
     */
    fun <LeafService> registeLeafService(
        leafServiceClass: Class<LeafService>,
        leafServiceProvider: LeafServiceProvider<LeafService>
    ) {

        LeafServiceManagerServer.registeLeafService(leafServiceClass, leafServiceProvider)


        leafServiceProviderMap[leafServiceClass.name] = leafServiceProvider
    }


    /**
     * 获取对象
     *
     */
    fun <LeafService> getLeafService(leafServiceClass: Class<LeafService>): LeafService? {

        // 先从内存中获取一次
        val provider: LeafServiceProvider<LeafService>? = leafServiceProviderMap[leafServiceClass.name] as? LeafServiceProvider<LeafService>

        //
        val binder = TreeServiceManager.getLeafServiceBinder(leafServiceClass.name)

        if (binder != null) {
            return provider?.provideClientProxy(binder)
        }
        return null
    }


}