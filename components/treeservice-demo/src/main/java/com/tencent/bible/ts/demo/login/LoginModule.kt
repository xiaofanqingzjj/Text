package com.tencent.bible.ts.demo.login

import android.app.Application
import android.os.IBinder
import com.tencent.bible.ts.LeafServiceManager
import com.tencent.bible.ts.LeafServiceProvider
import com.tencent.bible.ts.demo.ILoginLeafService
import com.tencent.bible.ts.demo.util.ModuleLauncherDelegate

/**
 * Created by hugozhong on 2020-01-13
 */

class LoginLeafServiceStub : ILoginLeafService.Stub() {
    override fun getAccessToken(): String {
        return LoginManager.getAccessToken()
    }

    override fun getUserId(): Long {
        return LoginManager.getUserId()
    }

    override fun getUserType(): Int {
        return LoginManager.getUserType()
    }

}

class LoginModule : ModuleLauncherDelegate() {


    override fun onCreate(application: Application?) {
        super.onCreate(application)

        /**
         * 在进程中注册接口给别的进程调用
         * 注册的是个什么东东呢？
         *
         *
         */
        LeafServiceManager.registeLeafService(ILoginmanager::class.java,
            object : LeafServiceProvider<ILoginmanager> {
                override fun provideBinder(): IBinder {
                    return LoginLeafServiceStub()
                }

                override fun provideClientProxy(binder: IBinder?): ILoginmanager {
                    return object : ILoginmanager {

                        private val service: ILoginLeafService? =
                            if (binder == null) null else ILoginLeafService.Stub.asInterface(binder)

                        override fun getUserId(): Long {
                            // step1.尝试远程获取
                            // step2.从内存获取
                            // step3.从文件缓存获取
                            if (service != null) {
                                return service.userId
                            }
                            return 0
                        }

                        override fun getAccessToken(): String {
                            if (service != null) {
                                return service.accessToken
                            }
                            return "memory-access_token"
                        }

                        override fun getUserType(): Int {
                            if (service != null) {
                                return service.userType
                            }
                            return 0
                        }

                    }
                }

            })
    }

}