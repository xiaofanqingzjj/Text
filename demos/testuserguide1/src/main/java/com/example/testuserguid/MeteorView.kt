package com.example.testuserguid

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.testuserguid.util.DensityUtil
import com.example.testuserguid.util.runUIThread
import kotlin.random.Random


/**
 * 流星View
 */
class MeteorView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {


    private val mStars: MutableList<View> = mutableListOf()

    private val widthHeightScale: Float = 1.7f

    /**
     * 每次漂流星之间的间隔
     */
    private val time = 5000L

    init {
        LayoutInflater.from(context).inflate(getLayoutId(), this)
        val container = findViewById<ViewGroup>(R.id.container)
        for (i in 0 until container.childCount) {
            mStars.add(container.getChildAt(i))
        }

    }

    fun getLayoutId(): Int {
        return R.layout.view_user_guide_meteor
    }

    private val shootTask: Runnable? = Runnable {
        shoot()
        post()
    }

    private fun post() {
        handler?.postDelayed(shootTask, time)
    }

    fun start() {
        // start的时候不能使用getHandler方法，因为有可能获取到null
        runUIThread({
            post()
        }, 200)
    }

    fun stop() {
        handler?.removeCallbacks(shootTask)
    }

    private fun shoot() {
        val starNum = Random.nextInt(1, mStars.size)
        for (i in 0 until starNum) {
            val startTime = Random.nextLong(2000)
            val startYPos= Random.nextInt(-dp2px(80f), height - dp2px(50f))
            shootSingleStar(mStars[i], startYPos.toFloat(), startTime)
        }
    }


    private fun shootSingleStar(star: View, yPos: Float, delay: Long = 0) {
        val w = width.toFloat()
        val h = (w + star.width * 2) / widthHeightScale
        AnimatorSet().apply {
            playTogether(*mutableListOf<ObjectAnimator>().apply {
                add(ObjectAnimator.ofFloat(star, View.X, w + star.width, -star.width.toFloat()))
                add(ObjectAnimator.ofFloat(star, View.Y, yPos, yPos + h))
            }.toTypedArray())

            this.startDelay = delay
            this.duration = 2000
        }.start()
    }



    private fun dp2px(value: Float): Int {
        return DensityUtil.dip2px(context, value)
    }




}



