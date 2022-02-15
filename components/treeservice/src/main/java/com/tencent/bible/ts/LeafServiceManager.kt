package com.tencent.bible.ts

import android.os.IBinder



/**
 * 在实现端注册
 */
interface LeafServiceProvider<LeafService> {
    /**
     *
     */
    fun provideBinder(): IBinder

    /**
     *
     */
    fun provideClientProxy(binder: IBinder?): LeafService
}

internal object LeafServiceManagerServer {

    private val leafServiceProviderMap = mutableMapOf<String, LeafServiceProvider<*>>()


    /**
     * 在实现端注册
     */
    fun <LeafService> registeLeafService(
        leafServiceClass: Class<LeafService>,
        leafServiceProvider: LeafServiceProvider<LeafService>
    ) {
        leafServiceProviderMap[leafServiceClass.name] = leafServiceProvider
    }

    fun  getLeafServiceProvider(leafServiceName: String): LeafServiceProvider<*>? {
        return leafServiceProviderMap[leafServiceName] as LeafServiceProvider<*>?;
    }


//    /**
//     * 在远端调用
//     */
//    fun getLeafServiceBinder(leafServiceName: String): IBinder? {
//        return leafServiceProviderMap[leafServiceName]?.provideBinder()
//    }
}


/**
 *
 * 获取
 *
 */
object LeafServiceManager {
//
//    // 当前进程的
//    private val nativeServiceProviderMap = mutableMapOf<String, LeafServiceProvider<*>>()


    /**
     * 注册对外接口
     *
     * @param leafServiceClass 指定要导出的接口对象
     * @param leafServiceProvider 获取binder，以及把Binder转成LeafService
     *
     */
    fun <LeafService> registeLeafService(
        leafServiceClass: Class<LeafService>,
        leafServiceProvider: LeafServiceProvider<LeafService>
    ) {
        LeafServiceManagerServer.registeLeafService(leafServiceClass, leafServiceProvider)
    }


    /**
     * 在远端调用
     *
     */
    fun <LeafService> getLeafService(leafServiceClass: Class<LeafService>): LeafService? {

        // 先从内存中获取一次
        val provider: LeafServiceProvider<LeafService>? = LeafServiceManagerServer.getLeafServiceProvider(leafServiceClass.name) as LeafServiceProvider<LeafService>?; // nativeServiceProviderMap[leafServiceClass.name] as? LeafServiceProvider<LeafService>

        //
        val binder = RemoteServiceManager.getLeafServiceBinder(leafServiceClass.name)

        // 根据
        if (binder != null) {
            return provider?.provideClientProxy(binder)
        }
        return null
    }


}