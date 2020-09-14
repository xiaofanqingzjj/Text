package com.tencent.bible.ts.demo.pay

/**
 * Created by hugozhong on 2020-01-13
 */
interface IPayManager {
    fun getPf(): String
    fun getChannelId(): String
}

object PayManager : IPayManager {
    override fun getPf(): String {
        return "pf134552020"
    }

    override fun getChannelId(): String {
        return "channelIdxx1"
    }
}