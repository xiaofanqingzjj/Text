package com.example.testpermission

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class MyRecyclerView(context: Context, attrs: AttributeSet? = null): RecyclerView(context, attrs) {


    var contentScrollX = 0
    var contentScrollY = 0

    init {

    }


    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        contentScrollX += dy
        contentScrollY += dy

    }
}