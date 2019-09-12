package com.example.testpermission.ad

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.testpermission.R


/**
 *
 * 首页顶部广告栏背景View
 *
 * @author fortune
 */
class HomeHeadAdBgView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs){

    companion object {
        const val TAG = "HomeHeadAdBgView"
    }


    private val ivBg: ImageView

    private val ivRole1: ImageView
    private val ivRole2: ImageView

    private var maxScrollSize = 0

    private var scrollPos: Int = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_head_ad_bg, this)

        ivBg = findViewById(R.id.iv_bg)
        ivRole1 = findViewById(R.id.iv_role1)
        ivRole2 = findViewById(R.id.iv_role2)
    }


    fun setData(data: HomeHeadAd) {

    }



    fun setCurrentScrollPos(pos: Int) {


        val dy = -pos

        // 移动背景
        scroll(ivBg, dy, 0.4f)
        scroll(ivRole2, dy, 0.2f)
        scroll(ivRole1, dy, 0.1f)

        scrollPos = dy

        Log.d(TAG, "setCurrent:$scrollPos")

    }

    private fun scroll(view: View, scrollPos: Int, scale: Float = 1f) {
        view.translationY =  scrollPos * scale
    }

}