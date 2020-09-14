package com.fortunexiao.tktx.platforms

import android.content.Context
import android.util.DisplayMetrics


/**
 * 提供一些单位转换的扩展方法
 */


/**
 *  dp转px
 */
fun Int.dp2px(context: Context?): Int {
    return this.toFloat().dp2px(context)
}

fun Float.dp2px(context: Context?): Int {
    val scale = getScreenDensity(context)
    return (this * scale + 0.5f).toInt()
}


/**
 * px转dp
 */

fun Int.px2dp(context: Context?): Int {
    return this.toFloat().px2dp(context)
}

fun Float.px2dp(context: Context?): Int {
    val scale = getScreenDensity(context)
    return (this / scale + 0.5f).toInt()
}


/**
 * sp转px
 */

fun Int.sp2px(context: Context?): Float {
    return this.toFloat().sp2px(context)
}

fun Float.sp2px(context: Context?): Float {
    val scale = getScreenDensity(context)
    return this * scale + 0.5f
}




private fun getScreenDensity(context: Context?): Float {
    return context?.resources?.displayMetrics?.density ?: DisplayMetrics.DENSITY_DEFAULT.toFloat()
}