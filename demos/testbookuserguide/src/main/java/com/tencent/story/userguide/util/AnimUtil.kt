package com.tencent.story.userguide.util

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Point
import android.graphics.PointF
import android.util.Property
import android.view.View


object AnimUtil {


    /**
     * 返回做缩放动画的集合
     */
    fun scale(v: View, vararg values: Float): List<ObjectAnimator> {
        return mutableListOf<ObjectAnimator>().apply {
            add(ObjectAnimator.ofFloat(v, View.SCALE_X, *values))
            add(ObjectAnimator.ofFloat(v, View.SCALE_Y, *values))
        }
    }

    /**
     * 返回做缩放动画的集合
     */
    fun alpha(v: View, vararg values: Float): ObjectAnimator {
        return ObjectAnimator.ofFloat(v, View.ALPHA, *values)
    }

    /**
     * 移动一段距离
     */
    fun translateBy(v: View, vararg values: Pair<Float?, Float?>): List<ObjectAnimator> {
        return translate(v, false, *values)
    }


    /**
     * 平移到目标位置
     */
    fun translateTo(v: View, vararg values: Pair<Float?, Float?>): List<ObjectAnimator> {
        return translate(v, true, *values)
    }


    /**
     * 平移到目标位置
     */
    fun translate(v: View, toOrBy: Boolean, vararg values: Pair<Float?, Float?>): List<ObjectAnimator> {

        val translateX = FloatArray(values.size) {0f}
        val translateY = FloatArray(values.size) {0f}

        var xEmpty = true
        var yEmpty = true

        values.mapIndexed { index, pair ->

            val first = pair.first
            val second = pair.second

            if (first != null) {
                translateX[index] = first
                xEmpty = false
            }

            if (second != null) {
                translateY[index] = second
                yEmpty = false
            }
        }


        return mutableListOf<ObjectAnimator>().apply {
            if (!xEmpty) {
                add(ObjectAnimator.ofFloat(v, if (toOrBy) View.X else View.TRANSLATION_X, *translateX))
            }

            if (!yEmpty) {
                add(ObjectAnimator.ofFloat(v, if (toOrBy) View.Y else View.TRANSLATION_Y, *translateY))
            }
        }
    }



    /**
     * 闪烁View
     *
     * @param v
     * @param duration 时间
     * @param delay 开始时间
     * @param values 闪烁范围
     *
     * @return
     */
    fun flash(v: View, duration: Long = 2000, delay: Long = 0, vararg values: Float): ObjectAnimator {

        val flashValues = if (values.isEmpty()) {
            floatArrayOf(0f, 1f)
        } else {
            values
        }

        return ObjectAnimator.ofFloat(v, View.ALPHA, *flashValues).apply {
            this.duration = duration
            this.startDelay = delay
            indefiniteReverseAnim()
        }
    }


    /**
     * 渐变显示或隐藏
     */
    fun alphaVisibility(v: View, duration: Long = 800, isShow: Boolean = true): ObjectAnimator {

        val value = if (isShow) 1f else 0f

        return ObjectAnimator.ofFloat(v, View.ALPHA, value).apply {
            this.duration = duration
            start()
        }
    }

    /**
     * 缩放显示或隐藏
     */
    fun scaleVisibility(v: View, duration: Long = 300, delay: Long = 0, isShow: Boolean = true): AnimatorSet {

        val value = if (isShow) 1f else 0f

        return AnimatorSet().apply {
            this.duration = duration
            this.startDelay = delay

            playTogether(*mutableListOf<ObjectAnimator>().apply {
                add(ObjectAnimator.ofFloat(v, View.SCALE_X, value))
                add(ObjectAnimator.ofFloat(v, View.SCALE_Y, value))
            }.toTypedArray())
            start()
        }
    }



    fun positionOf(v: View): PointF {
        return PointF((v.x + v.width / 2), (v.y + v.height / 2))
    }

    fun point2array(p: PointF): FloatArray {
        return floatArrayOf(p.x.toFloat(), p.y.toFloat())
    }


    /**
     * 根据中心点来定位点属性
     */
    val CX = object : Property<View, Float>(Float::class.java,"cx") {
        override fun set(view: View, value: Float) {
            view.x = value - view.width / 2
        }

        override fun get(view: View): Float {
            return view.x + view.width / 2
        }
    }
    val CY = object : Property<View, Float>(Float::class.java,"cy") {
        override fun set(view: View, value: Float) {
            view.y = value - view.height / 2
        }

        override fun get(view: View): Float {
            return view.y + view.height / 2
        }
    }


}


abstract class AnimListenerStub: Animator.AnimatorListener{
     override fun onAnimationRepeat(animation: Animator?) {
     }

     override fun onAnimationEnd(animation: Animator?) {
     }

     override fun onAnimationCancel(animation: Animator?) {
     }

     override fun onAnimationStart(animation: Animator?) {
     }
}

/**
 * 一个监听动画结束的快捷方法
 */
fun Animator.addEndListener(onAnimationEnd: ()->Unit) {
    addListener(object : AnimListenerStub() {
        override fun onAnimationEnd(animation: Animator?) {
            onAnimationEnd.invoke()
        }
    })
}


/**
 * 设置某个动画无限重复播放
 *
 *
 */
fun ValueAnimator.indefiniteReverseAnim(): ValueAnimator {
    repeatMode = ObjectAnimator.REVERSE
    repeatCount = ValueAnimator.INFINITE
    return this
}


