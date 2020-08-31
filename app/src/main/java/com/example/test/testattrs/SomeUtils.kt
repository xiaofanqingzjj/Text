package com.example.test.testattrs

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import java.lang.Exception


/**
 * 和主题相关的一些工具方法
 */

fun Resources.Theme.allAttributes(): IntArray? {

    try {
        val method = this.javaClass.getDeclaredMethod("getAllAttributes")
        return method.invoke(this) as IntArray
    } catch (e: Exception) {

        Log.e("SomeUtils", "class:${this} getAllAttr:${e.message}", e)

    }
    return null
}


//fun Resources.Theme.

fun Context.getThemeColorAttribute(themeId: Int, attrId: Int): Int {
    val attrs = intArrayOf(attrId)
    val a = obtainStyledAttributes(themeId, attrs)
    val color = a.getColor(0, 0)
    a.recycle()
    return color
}

fun Context.getThemeColorStateAttribute(themeId: Int, attrId: Int): ColorStateList? {
    val attrs = intArrayOf(attrId)
    val a = obtainStyledAttributes(themeId, attrs)
    val color = a.getColorStateList(0)
    a.recycle()
    return color
}

fun Context.getThemeDrawableAttribute(themeId: Int, attrId: Int): Drawable? {
    val attrs = intArrayOf(attrId)
    val a = obtainStyledAttributes(themeId, attrs)
    val value = a.getDrawable(0)
    a.recycle()
    return value
}

fun Context.getThemeDimensionAttribute(themeId: Int, attrId: Int): Float {
    val attrs = intArrayOf(attrId)
    val a = obtainStyledAttributes(themeId, attrs)
    val value = a.getDimension(0, 0f)
    a.recycle()
    return value
}

/**
 * 获取当前主题的指定属性的颜色
 */
fun Context.getThemeColorAttribute(attrId: Int): Int {
    val attrs = intArrayOf(attrId)
    val a = obtainStyledAttributes(attrs)
    val color = a.getColor(0, 0)
    a.recycle()
    return color
}

fun Context.getThemeColorStateAttribute(attrId: Int): ColorStateList? {
    val attrs = intArrayOf(attrId)
    val a = obtainStyledAttributes(attrs)
    val color = a.getColorStateList(0)
    a.recycle()
    return color
}

fun Context.getThemeDrawableAttribute(attrId: Int): Drawable? {
    val attrs = intArrayOf(attrId)
    val a = obtainStyledAttributes(attrs)
    val value = a.getDrawable(0)
    a.recycle()
    return value
}

fun Context.getThemeDimensionAttribute(attrId: Int): Float {
    val attrs = intArrayOf(attrId)
    val a = obtainStyledAttributes(attrs)
    val value = a.getDimension(0, 0f)
    a.recycle()
    return value
}