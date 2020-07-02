package com.example.test.sticklayout

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.OverScroller
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import com.fortunexiao.tktx.runUIThread


/**
 * 一个简单的实现头部回弹的Layout
 *
 * Created by fortunexiao on 2017/11/6.
 */
class AutoHideFooterLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs), NestedScrollingParent {

    companion object {

        internal val TAG = AutoHideFooterLayout::class.java.simpleName

        private const val STATE_OPENED = 0
        private const val STATE_CLOSED = 1
        private const val STATE_CENTER = 2
    }

    private var mHideView: View? = null
    private var contentView: View? = null

    // 往上推的时候最大能推的距离
    private var maxScrollLength: Int = 0

    private var state = STATE_CLOSED

    private var mScroller: OverScroller = OverScroller(context)

    private var mTouchSlop: Int = 0

    private var mNestedScrollChild: View? = null

    private var onScrollListeners: MutableList<OnScrollListener> = mutableListOf()

//    var onRefreshListener: OnRefreshListener? = null

    init {
        ViewConfiguration.get(context).run {
            mTouchSlop = scaledTouchSlop
        }
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 1) {

            contentView = getChildAt(0)
            mHideView = getChildAt(1)

            hideHead()
        }
    }

    /**
     * 隐藏头部
     */
    private fun hideHead() {
//        scrollY = mHideView?.layoutParams?.height ?: 0
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (mHideView != null && contentView != null) {
            val headHeight = mHideView!!.measuredHeight
            maxScrollLength = headHeight

            // 如果content设置了marginTop值，需要把marginTop减掉
            val lp = contentView!!.layoutParams as FrameLayout.LayoutParams
            val contentHeight = measuredHeight - (headHeight - maxScrollLength) - lp.topMargin
            contentView!!.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY))
        }
    }

    /**
     * 外部可以设置头部View 的高度
     */
    fun setHeadHeight(height: Int) {
        runUIThread {
            // 很神奇，这里如果没有post会出现莫名其妙的问题，界面显示的不正确
            Log.d(TAG, "setHeaderHeight:$height")
            mHideView?.layoutParams?.height = height
            hideHead()

            forceLayout()
            requestLayout()
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop

        // content
        contentView?.run {
            val lp = this.layoutParams as FrameLayout.LayoutParams
            val l = paddingLeft + lp.leftMargin
            val t = paddingTop + lp.topMargin
            val r = l +  this.measuredWidth
            val b = t +  this.measuredHeight
            layout(l, t, r, b)
        }

        // footer
        mHideView?.run {
            val lp = this.layoutParams as FrameLayout.LayoutParams
            val l = paddingLeft + lp.leftMargin
            val t = contentView?.measuredHeight ?: 0 + lp.topMargin

            val r = l + this.measuredWidth
            val b = t + this.measuredHeight
            layout(l, t, r, b)
        }

    }

    private fun scrollByInner(y: Int) {
        scrollToInner(scrollY + (y * 0.5).toInt() )
    }

    private fun scrollToInner(y: Int) {

        Log.d(TAG, "scrollToInner:$y")
        var y = y





        if (y <= 0) {  // 向下滑
            y = 0

            if (state == STATE_CLOSED) { // 如果已经滑到顶了， 则不让滑了
                return
            }
        }

        if (y >= maxScrollLength) {
            y = maxScrollLength


            if (state == STATE_OPENED) {
                return
            }
        }

        state = when (y) {
            0 -> {
                STATE_CLOSED
            }
            maxScrollLength -> {
                STATE_OPENED
            }
            else -> STATE_CENTER
        }

//        onScrollListener?.onScroll(maxScrollLength - y, maxScrollLength)
        notifyListener(y)
        super.scrollTo(0, y)
    }

    private fun notifyListener(y: Int) {
        onScrollListeners.map {
            it.onScroll(maxScrollLength - y, maxScrollLength)
        }
    }

    fun addOnScrollListener(l: OnScrollListener) {
        onScrollListeners.add(l)
    }

    fun removeOnScrollListener(l: OnScrollListener) {
        onScrollListeners.remove(l)
    }


//    private fun autoAnim() {
//        animClose()
//    }

    private fun animClose() {
        Log.d(TAG, "animClose")
        val dy = 0 - scrollY
        var duration = Math.abs(dy * 3)
        if (duration > 800) {
            duration = 800
        }
        mScroller.startScroll(scrollX, scrollY, 0, dy, duration)
        ViewCompat.postInvalidateOnAnimation(this)
//        invalidate()
    }


    override fun computeScroll() {
        if (!mScroller.isFinished && mScroller.computeScrollOffset()) {
            val oldY = scrollY
            val y = mScroller.currY
            if (oldY != y) {
                scrollToInner(y)
            }

//            postInvalidate()

            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

//    override fun computeScroll() {
//        mIsScrollStarted = true
//        if (!mScroller.isFinished && mScroller.computeScrollOffset()) {
//            val oldX = scrollX
//            val oldY = scrollY
//            val x = mScroller.currX
//            val y = mScroller.currY
//
//
//            Log.d(TAG, "computeScoll x:$x, oldX:$oldX")
//
//            if (oldX != x || oldY != y) {
//                scrollTo(x, y)
//                if (!pageScrolled(x)) {
//                    mScroller.abortAnimation()
//                    scrollTo(0, y)
//                }
//            }
//
//
//            // Keep on drawing until the animation has finished.
//            ViewCompat.postInvalidateOnAnimation(this)
//            return
//        }
//
//        // Done with scroll, clean up state.
//        completeScroll(true)
//    }


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
        if (Build.VERSION.SDK_INT >= 21) {
            super.onStopNestedScroll(child)
        }

        animClose()
        Log.d(TAG, "scrollY:$scrollY")

        val currentScrollY = scrollY
        if (currentScrollY < 5) { // 如果在顶端松开手指，触发下拉刷新
//            onRefreshListener?.onRefresh()
        }
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

        when (state) {
            STATE_OPENED -> if (dy < 0) { // down
                scroll(dy, consumed)
            } else if (dy > 0) { // up

            }
            // 收起状态
            STATE_CLOSED -> if (dy < 0) { // 如果是向下滑动的话，先检查Content是否可以滑动，如果Content可以向下滑的话，让Content自己先滑，否则Parent才滑

            } else if (dy > 0) { // up
                if (contentView?.canScrollVertically(1) == false) {
                    scroll(dy, consumed)
                }
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

    /**
     * 下拉刷新
     */
    interface OnRefreshListener {
        fun onRefresh()
    }

}
