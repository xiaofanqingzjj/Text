package com.example.testpermission.ad

import android.content.Context
import android.os.Build
import androidx.core.view.NestedScrollingParent
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.OverScroller


/**
 * 一个简单的实现头部回弹的Layout
 *
 * Created by fortunexiao on 2017/11/6.
 */
class AutoHideHeaderLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs), NestedScrollingParent {

    companion object {

        internal val TAG = AutoHideHeaderLayout::class.java.simpleName

        private const val STATE_OPENED = 0
        private const val STATE_CLOSED = 1
        private const val STATE_CENTER = 2
    }

    private var headView: View? = null
    private var contentView: View? = null

    // 往上推的时候最大能推的距离
    private var maxScrollLength: Int = 0

    private var state = STATE_CLOSED

    private var mScroller: OverScroller = OverScroller(context)

    private var mTouchSlop: Int = 0

    private var mNestedScrollChild: View? = null

    var onScrollListener: OnScrollListener? = null

    init {
        ViewConfiguration.get(context).run {
            mTouchSlop = scaledTouchSlop
        }
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 1) {
            headView = getChildAt(0)
            contentView = getChildAt(1)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (headView != null && contentView != null) {
            val headHeight = headView!!.measuredHeight
            maxScrollLength = headHeight

            // 如果content设置了marginTop值，需要把marginTop减掉
            val lp = contentView!!.layoutParams as FrameLayout.LayoutParams
            val contentHeight = measuredHeight - (headHeight - maxScrollLength) - lp.topMargin
            contentView!!.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY))

            // 初始隐藏头部View
//            scrollByInner(maxScrollLength)
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        // header
        headView?.run {
            val lp = this.layoutParams as FrameLayout.LayoutParams
            val l = paddingLeft + lp.leftMargin
            val t = paddingTop + lp.topMargin
            val r = l + this.measuredWidth
            val b = t + this.measuredHeight
            layout(l, t, r, b)
        }

        // content
        contentView?.run {
            val lp = this.layoutParams as FrameLayout.LayoutParams
            val l = paddingLeft + lp.leftMargin
            val t = paddingTop + (headView?.height ?: 0) + lp.topMargin //content在Header的下方
            val r = l +  this.measuredWidth
            val b = t +  this.measuredHeight
            layout(l, t, r, b)
        }



    }

    private fun scrollByInner(y: Int) {
        scrollToInner(scrollY + y)
    }

    private fun scrollToInner(y: Int) {
        var y = y
        if (y <= 0) {
            y = 0
            if (state == STATE_OPENED) {
                return
            }
        }

        if (y >= maxScrollLength) {
            y = maxScrollLength
            if (state == STATE_CLOSED) {
                return
            }
        }

        state = when (y) {
            0 -> {
                STATE_OPENED
            }
            maxScrollLength -> {
                STATE_CLOSED
            }
            else -> STATE_CENTER
        }

        onScrollListener?.onScroll(maxScrollLength - y, maxScrollLength)
        super.scrollTo(0, y)
    }


    private fun autoAnim() {
        animClose()
    }

    private fun animClose() {
        val dy = maxScrollLength - scrollY
        var duration = Math.abs(dy * 3)
        if (duration > 500) {
            duration = 500
        }
        mScroller.startScroll(scrollX, scrollY, 0, dy, duration)
        invalidate()
    }


    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            val y = mScroller.currY
            scrollToInner(y)
            invalidate()
        }
    }


    // impl NestedScrollingParent

    // 子View请求开启嵌套滑动流程
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        mNestedScrollChild = target

        // 要嵌套滑动
        return true
    }

    private var axes = 0

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        if (Build.VERSION.SDK_INT >= 21)
            super.onNestedScrollAccepted(child, target, axes)

        this.axes = axes
        if (!mScroller.isFinished) {
            mScroller.abortAnimation()
        }
    }

    override fun onStopNestedScroll(child: View) {
        if (Build.VERSION.SDK_INT >= 21)
            super.onStopNestedScroll(child)
        autoAnim()
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        if (Build.VERSION.SDK_INT >= 21)
            super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        handleScroll(dy, consumed)
    }

    private fun handleScroll(dy: Int, consumed: IntArray?) {
        if (!mScroller.isFinished) {
            mScroller.abortAnimation()
        }

//        var dy = check(scrollY, dy, 0, maxScrollLength)
//        scrollBy(0, dy)

        Log.d(TAG, "handleScroll:$dy")
        when (state) {
            STATE_OPENED -> if (dy < 0) { // down

            } else if (dy > 0) { // up
                scroll(dy, consumed)
            }
            STATE_CLOSED // 收起状态
            -> if (dy < 0) { // 如果是向下滑动的话，先检查Content是否可以滑动，如果Content可以向下滑的话，让Content自己先滑，否则Parent才滑
                if (contentView?.canScrollVertically(-1) == false) {
                    scroll(dy, consumed)
                }
            } else if (dy > 0) { // up

            }
            STATE_CENTER -> scroll(dy, consumed)
        }
    }



    private fun scroll(dy: Int, consumed: IntArray?) {
        scrollByInner(dy)
        if (consumed != null) {
            consumed[1] = dy
        }
    }

    override fun getNestedScrollAxes(): Int {
        if (Build.VERSION.SDK_INT >= 21)
            return axes
        return super.getNestedScrollAxes()
    }


    /**
     * 头部滑动监听器
     */
    interface OnScrollListener {

        /**
         * 头部滑动监听
         *
         * @param currentScrollLength 当前滑动的距离
         * @param maxScrollLength     最大滑动距离
         */
        fun onScroll(currentScrollLength: Int, maxScrollLength: Int)
    }

}
