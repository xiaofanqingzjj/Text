package com.tencent.bible.ts.demo.usermanager

import android.os.IBinder
import com.tencent.bible.ts.LeafServiceManager
import com.tencent.bible.ts.LeafServiceProvider


interface IUserManager {
    fun getUserInfo():UserInfo?
}

object UserManager : IUserManager{
    override fun getUserInfo(): UserInfo? {
        return UserInfo(name = "Xiao", age = 10)
    }
}

class  UserManagerStubImpl : com.tencent.bible.treeservice.UserManager.Stub() {
    override fun getUserInfo(): UserInfo? {
        return UserManager.getUserInfo();
    }
}

object UserManagerInitor {
    fun init() {

        LeafServiceManager.registeLeafService<IUserManager>(IUserManager::class.java, object : LeafServiceProvider<IUserManager> {
            override fun provideBinder(): IBinder {
                // 需要透传的Binder对象
                return  UserManagerStubImpl();
            }

            override fun provideClientProxy(binder: IBinder?): IUserManager {
                // 从Binder转成用户接口的方式
                return object : IUserManager {
                    override fun getUserInfo(): UserInfo? {
                        return com.tencent.bible.treeservice.UserManager.Stub.asInterface(binder).userInfo
                    }

                }
            }
        })
    }
}