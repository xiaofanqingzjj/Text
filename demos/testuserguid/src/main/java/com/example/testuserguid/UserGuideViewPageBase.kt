package com.example.testuserguid

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.example.testuserguid.util.DensityUtil


/**
 *
 */
abstract class UserGuideViewPageBase(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {


    companion object {

    }

    init {

    }


    fun dp2px(value: Float): Int {
        return DensityUtil.dip2px(context, value)
    }

}