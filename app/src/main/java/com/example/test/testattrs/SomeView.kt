package com.example.test.testattrs

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import com.example.test.R

class SomeView(context: Context, attrs: AttributeSet? = null): TextView(context, attrs) {

    init {
//        val t = context.obtainStyledAttributes(attrs, intArrayOf(R.attr.my_attr))
//        val color = t.getColor(0, Color.BLACK)
//        setTextColor(color)
//        t.recycle()


        val t = context.obtainStyledAttributes(attrs, R.styleable.AttrGroup)
        val color1 = t.getColor(R.styleable.AttrGroup_attr3, Color.BLACK)
        val color2 = t.getColor(R.styleable.AttrGroup_attr4, Color.BLACK)
        setTextColor(color1)
        setBackgroundColor(color2)
        t.recycle()
    }
}

