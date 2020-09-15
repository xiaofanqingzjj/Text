package com.tencent.widget.superimageview


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


/**
 * 一个简单的支持圆角的图片组件
 *
 * @author fortune
 */
class SimpleRoundedImageView(context: Context, attrs: AttributeSet? = null) : AppCompatImageView(context, attrs) {

    companion object {
        const val TAG = "SimpleRoundedImageView"
    }

    var roundedImageHelper: RoundedImageHelper = RoundedImageHelper(this,  attrs)

    @SuppressLint("MissingSuperCall")
    override fun draw(canvas: Canvas?) {
        roundedImageHelper.draw(canvas) {
            super@SimpleRoundedImageView.draw(canvas)
        }
    }
}

