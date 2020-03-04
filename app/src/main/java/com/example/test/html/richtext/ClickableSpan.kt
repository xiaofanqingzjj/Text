package com.example.test.html.richtext

import android.view.View


/**
 *
 * 可点击的span
 *
 * @author fortune
 */
interface ClickableSpan {

    fun onClick(v: View)

    fun onLongClick(v: View) {}
}