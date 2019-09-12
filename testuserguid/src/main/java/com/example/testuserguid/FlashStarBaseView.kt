package com.example.testuserguid

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import com.example.testuserguid.util.runUIThread
import kotlin.random.Random


/**
 * 星光View
 */
abstract class FlashStarBaseView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {


    private val mStars: MutableList<View> = mutableListOf()

    private val mAnims: MutableList<Animator> = mutableListOf()

    init {

        LayoutInflater.from(context).inflate(getLayoutId(), this)


        val container = findViewById<ViewGroup>(R.id.container)

        for (i in 0 until container.childCount) {
            mStars.add(container.getChildAt(i))
        }



    }

    abstract fun getLayoutId(): Int





    fun startFlash() {
        val initDelayTime = 200

        mStars.map {
            val startTime = Random.nextLong(2000)
            flashStar(it, initDelayTime + startTime, 3000)
        }
    }

    fun stopFlash() {
        mAnims.map {
            it.cancel()
        }
    }


    private fun flashStar(v: View, startTime: Long = 0, duration: Long = 3000) {

        mAnims.clear()

        runUIThread({

            v.pivotX = (v.width / 2).toFloat()
            v.pivotY = (v.height / 2).toFloat()

            val arrayValue = floatArrayOf(0f, 0f,   1f, 1f)

            val animAlpha = ObjectAnimator.ofFloat(v, View.ALPHA, *arrayValue).indefiniteReverseAnim()

            val animScaleX = ObjectAnimator.ofFloat(v, View.SCALE_X, *arrayValue).indefiniteReverseAnim()
            val animScaleY = ObjectAnimator.ofFloat(v, View.SCALE_Y, *arrayValue).indefiniteReverseAnim()

            val set = AnimatorSet().apply {
                interpolator = AccelerateDecelerateInterpolator()
                this.duration = duration
                playTogether(animAlpha, animScaleX, animScaleY)
            }

            mAnims.add(set)

            set.start()
        }, startTime)

    }





}



