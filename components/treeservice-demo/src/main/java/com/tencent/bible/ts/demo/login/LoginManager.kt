package com.tencent.bible.ts.demo.login

/**
 * Created by hugozhong on 2020-01-10
 */

interface ILoginmanager {
    fun getUserId(): Long
    fun getAccessToken(): String
    fun getUserType(): Int

}

object LoginManager : ILoginmanager {
    override fun getUserId(): Long {
        return 18888
    }

    override fun getAccessToken(): String {
        return "accessToken18888"
    }

    override fun getUserType(): Int {
        return 1
    }

}