package com.bedrock.module_base.util

import android.content.Context
import android.util.Size


/**
 * 和屏幕相关的一些工具类
 *
 *
 */
object ScreenUtil {

    fun isLandscape(context: Context) : Boolean {
        val dm = context.applicationContext.resources.displayMetrics
        return dm.widthPixels > dm.heightPixels
    }

    /**
     * 屏幕分辨率
     */
    fun resolution(context: Context): Size {
        val a = context.resources.displayMetrics
        return Size(a.widthPixels, a.heightPixels)
    }
}