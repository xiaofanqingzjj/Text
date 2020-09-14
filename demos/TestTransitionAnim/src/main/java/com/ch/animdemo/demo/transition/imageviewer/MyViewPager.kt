package com.ch.animdemo.demo.transition.imageviewer

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

class MyViewPager(context: Context, attributeSet: AttributeSet? = null): ViewPager(context, attributeSet) {

    companion object {
        const val TAG = "MyViewPager"
    }

    init {

    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val isIntercpet =  super.onInterceptTouchEvent(ev)
        Log.d(TAG, "isIntercpet:$isIntercpet")
        return isIntercpet
    }

    override fun canScroll(v: View?, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        val canScall =  super.canScroll(v, checkV, dx, x, y)
        Log.e(TAG, "canScroll:$canScall")
        return canScall
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return super.canScrollHorizontally(direction)
    }




}