package com.example.testuserguid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout


/**
 * 新手引导View
 *
 * @author fortunexiao
 */
class UserGuideView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    // 第一页展示一本书
    private val page1: UserGuideViewPage1

    // 第二页选择性别、选择tag、推荐书
    private val page2: UserGuideViewPage2


    init {

        LayoutInflater.from(context).inflate(R.layout.view_user_guide, this)

        page1 = findViewById(R.id.page1)
        page2 = findViewById(R.id.page2)


        // 第一页点击引导按钮后动画隐藏第一页
        page1.onClickNext = {
            page2.show(it)
        }
    }


    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        val matrix: Matrix = Matrix()

//        matrix.mapPoints()


    }
}