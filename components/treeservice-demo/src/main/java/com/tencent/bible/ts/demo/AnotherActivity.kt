package com.tencent.bible.ts.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tencent.bible.ts.LeafServiceManager
import com.tencent.bible.ts.demo.login.ILoginmanager
import com.tencent.bible.ts.demo.pay.IPayManager
import com.tencent.bible.ts.demo.usermanager.IUserManager
import kotlinx.android.synthetic.main.activity_another.*

/**
 * Created by hugozhong on 2020-01-13
 */
class AnotherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_another)
        login_manager_btn.setOnClickListener {

            val loginMgr = LeafServiceManager.getLeafService(ILoginmanager::class.java)

            val userId = loginMgr?.getUserId()
            val accessToken = loginMgr?.getAccessToken()
            val userType = loginMgr?.getUserType()
            tv.text = "userId:${userId} accessToken:${accessToken} userType:$userType"
        }

        pay_manager_btn.setOnClickListener {
            val payMgr = LeafServiceManager.getLeafService(IPayManager::class.java)
            val pf = payMgr?.getPf()
            val channelId = payMgr?.getChannelId()
            tv.text = "pf:${pf} channelId:${channelId}"
        }
        third_activity.setOnClickListener {
            startActivity(Intent(this,ThirdActivity::class.java))
        }

        btn_getUserInfo.setOnClickListener {
            val userInfo = LeafServiceManager.getLeafService(IUserManager::class.java)?.getUserInfo();
            tv.text = userInfo?.toString();
        }
    }
}