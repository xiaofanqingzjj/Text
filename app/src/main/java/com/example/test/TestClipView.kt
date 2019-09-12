package com.example.test

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

class TestClipView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    companion object {

        const val TAG = "TestClipView"
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {

        paint.color = Color.BLUE


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


        canvas?.translate(200f, 200f)
        canvas?.rotate(30f)

        canvas?.clipRect(0, 0, 200, 200)
        canvas?.clipRect(150f, 150f, 400f, 400f, Region.Op.UNION)

        canvas?.clipRect(150f, 150f, 800f, 200f, Region.Op.UNION)


        canvas?.drawCircle(200f, 200f, 100f, paint)

        val rect = Rect()
        canvas?.getClipBounds(rect)


        Log.d(TAG, "rect:$rect")
    }
}