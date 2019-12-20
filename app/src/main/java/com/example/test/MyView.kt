package com.example.test

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider

class MyView(context: Context, attributeSet: AttributeSet? = null) : View(context, attributeSet) {

    init {

    }

    override fun getOutlineProvider(): ViewOutlineProvider {
        return super.getOutlineProvider()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)



//        canvas.clipPath()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}