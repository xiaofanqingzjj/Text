package com.fortunexiao.tktx

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**
 *
 * View相关的一些扩展方法
 *
 *
 * @author fortunexiao

        */





fun View.show(withAnim: Boolean = false) {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
    if (withAnim) {
        ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f).apply {
            duration = 300
        }.start()
    }
}

fun View.hide() {
    if (visibility != View.GONE) visibility = View.GONE
}



// =====================================



/**
 * 遍历ViewGroup的Child
 */
fun <T> ViewGroup.mapChild(action:(i: Int, v: View)->T) : List<T> {
    val list = mutableListOf<T>()
    for (i in 0 until this.childCount) {
        list.add(action(i, this.getChildAt(i)))
    }
    return list
}

/**
 * 遍历ViewGroup的Child
 */
fun <T> ViewGroup.forEachChild(action:(i: Int, v: View)->T)  {
    for (i in 0 until this.childCount) {
        action(i, this.getChildAt(i))
    }
}



// =====================================




/**
 * 设置View是否可用，会递归所有的子View
 */
fun View.setEnable(enable: Boolean) {
    isEnabled = enable
    if (this is ViewGroup) {
        this.mapChild { i, v ->
            v.setEnable(enable)
        }
    }
}





// =====================================

private fun View.getColorStateListByResId(colorResId: Int): ColorStateList {
    return context.resources.getColorStateList(colorResId)
}


private fun View.getColorByResId(colorResId: Int): Int {
    return context.resources.getColor(colorResId)
}

/**
 * 使用resId给TextView设置颜色
 */
fun TextView.setTextColorByResId(colorResId: Int) {
//    setTextColor(getColorByResId(colorResId))
    setTextColor(getColorStateListByResId(colorResId))
}

fun View.setBackgroundColorByResId(colorResId: Int) {
    setBackgroundColor(getColorByResId(colorResId))
}


/**
 * 长按View监听器
 */
fun View.setOnLongTouchListener(initTime: Long = 500, tickDuration:Long = 50, longTick: ()->Boolean) {

    val tickTask = object: Runnable{
        override fun run() {
            val r = longTick()
//            Log.d("OnLongTouchListener", "onLongTck:$r")
            if (r) {

                postDelayed(this, tickDuration)
            }
        }
    }

    setOnLongClickListener {
        post(tickTask)
        true
    }

    var touchX: Float = 0f
    var touchY: Float = 0f

    setOnTouchListener { v, event ->
//        Log.d("OnLongTouchListener", "onTouch:${event.action}")
        if (event.action == MotionEvent.ACTION_DOWN) {
//            if (longTick()) {
//                postDelayed(tickTask, initTime)
//            }
            touchX = event.x
            touchY = event.y

        } else if (event.action == MotionEvent.ACTION_MOVE) {
            val x = event.x
            val y = event.y

            if (!pointInView(x, y, getTouchSlop().toFloat())) {
                removeCallbacks(tickTask)
            }
        }  else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL ) {
            removeCallbacks(tickTask)
        }
        false
    }
}

private fun  View.getTouchSlop(): Int {
    return android.view.ViewConfiguration.get(context).scaledTouchSlop
}

private fun View.pointInView(localX: Float, localY: Float, slop: Float): Boolean {
    return localX >= -slop && localY >= -slop && localX < right - left + slop && localY < bottom - top + slop
}






