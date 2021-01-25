package com.tencent.bible.ts.demo.pay

import android.app.Application
import android.os.IBinder
import com.tencent.bible.ts.LeafServiceManager
import com.tencent.bible.ts.LeafServiceProvider
import com.tencent.bible.ts.demo.IPayService
import com.tencent.bible.ts.demo.util.ModuleLauncherDelegate

/**
 * Created by hugozhong on 2020-01-13
 */

class PayServiceStub : IPayService.Stub() {
    override fun getPf(): String {
        return PayManager.getPf()
    }

    override fun getChannelId(): String {
        return PayManager.getChannelId()
    }

}

class PayModule : ModuleLauncherDelegate() {
    override fun onCreate(application: Application?) {
        super.onCreate(application)
        LeafServiceManager.registeLeafService(IPayManager::class.java,
            object : LeafServiceProvider<IPayManager> {
                override fun provideBinder(): IBinder {
                    return PayServiceStub()
                }

                override fun provideClientProxy(binder: IBinder?): IPayManager {

                    // 这个方法是在新进程里跑， 负责把Binder转成对应的业务接口
                    return object : IPayManager {
                        private var service: IPayService? =
                            if (binder == null) null else IPayService.Stub.asInterface(binder)

                        override fun getPf(): String {
                            if (service != null) {
                                return service!!.pf
                            }
                            return "memory-pf"
                        }

                        override fun getChannelId(): String {
                            if (service != null) {
                                return service!!.channelId
                            }
                            return "memory-channelId"
                        }

                    }
                }

            })
    }
}