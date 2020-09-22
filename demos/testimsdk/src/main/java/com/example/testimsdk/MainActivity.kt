package com.example.testimsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tencent.imsdk.*
import com.tencent.imsdk.ext.message.TIMMessageRevokedListener
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initIMSDK()
    }


    /**
     * 初始化IMSDK
     */
    private fun initIMSDK() {
        val timManager = TIMManager.getInstance()

        // SDK 初始化
        val config = TIMSdkConfig(1400199290).apply {

            setLogListener { i, s, s1 ->

            }

            logLevel = 0

            enableLogPrint(true).logPath = null
        }
        timManager.init(this.applicationContext, config)


        // 用户设置初始化
        val userConfig = TIMUserConfig().apply {
            userStatusListener = object : TIMUserStatusListener {
                override fun onForceOffline() {

                }

                override fun onUserSigExpired() {

                }
            }
            connectionListener = object : TIMConnListener {
                override fun onConnected() {

                }

                override fun onDisconnected(i: Int, s: String) {

                }

                override fun onWifiNeedAuth(s: String) {

                }
            }

            groupEventListener = TIMGroupEventListener { }

            uploadProgressListener = TIMUploadProgressListener { timMessage, i, i1, i2 ->


            }
        }


        // 用户扩张配置
        val userConfigMsgExt = TIMUserConfigMsgExt(userConfig)
                .enableStorage(true) // 开启存储


        // 回话刷新监听
        userConfigMsgExt.refreshListener = object : TIMRefreshListener {
            override fun onRefresh() {

            }

            override fun onRefreshConversation(list: List<TIMConversation>) {

            }
        }

        // 消息撤回监听
        userConfigMsgExt.messageRevokedListener = TIMMessageRevokedListener { }



        timManager.userConfig = userConfigMsgExt


        // 新消息监听
        timManager.addMessageListener {
            false
        }

        //
    }


    /**
     * 登录
     */
    internal fun loginIMSDK() {
        TIMManager.getInstance().login("", "", object : TIMCallBack {
            override fun onError(i: Int, s: String) {

            }

            override fun onSuccess() {

            }
        })
    }


    /**
     * 收发消息
     */

    // 文本消息的发送
    fun sendText(conversation: TIMConversation, text: String) {

        conversation.sendMessage(
                TIMMessage().apply {
                    addElement(TIMTextElem().apply {
                        setText(text)
                    })
                },
                object : TIMValueCallBack<TIMMessage> {
                    override fun onSuccess(p0: TIMMessage?) {
                    }

                    override fun onError(p0: Int, p1: String?) {
                    }
                })
    }


}
