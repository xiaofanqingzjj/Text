package com.example.test.sticklayout

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.OverScroller
import androidx.core.view.NestedScrollingParent
import androidx.core.view.NestedScrollingParentHelper
import com.bedrock.module_base.util.DensityUtil


/**
 * 一个简单的实现头部可以收缩控件
 * Created by fortunexiao on 2017/11/6.
 */
class StickyLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs), NestedScrollingParent {

    companion object {

        internal val TAG = StickyLayout::class.java.simpleName

        internal const val STATE_OPENED = 0
        internal const val STATE_CLOSED = 1
        internal const val STATE_CENTER = 2
    }

    private var headView: View? = null
    private var contentView: View? = null

    // 往上推的时候最大能推的距离
    private var maxScrollLength: Int = 0

    private var state = STATE_OPENED

    private var mScroller: OverScroller

    private var nestedScrollingParentHelper: NestedScrollingParentHelper? = null

    private var mVelocityTracker: VelocityTracker? = null
    private var mLastTouchY: Float = 0.toFloat()
    private var mTouchSlop: Int = 0
    private var mMinFlingVelocity: Int = 0
    private var mMaxFlingVelocity: Int = 0

    /**
     * 该标志为true表示当前的View自己在处理Touch事件，即正在拖动
     */
    private var mDragging = false

    /**
     * 关闭的时候顶部剩余的空间
     */
    private var headLeftSize = 0

    private var mNestedScrollChild: View? = null

    private var onContentReadForScrollListener: OnContentReadForScrollListener? = null

    private var onStickyScrollListener: OnStickyScrollListener? = null

    private var onStartNestedScrollListener: OnNestedScrollListener? = null

    init {
        nestedScrollingParentHelper = NestedScrollingParentHelper(this)
        maxScrollLength = DensityUtil.dip2px(context, 200f)// headView.getMeasuredHeight();
        mScroller = OverScroller(context)

        if (attrs != null) {
//            val a = context.obtainStyledAttributes(attrs, R.styleable.StickyLayout)
//            headLeftSize = a.getDimension(R.styleable.StickyLayout_headLeftSize, 0f).toInt()
//            a.recycle()
        }

        val vc = ViewConfiguration.get(context)
        mTouchSlop = vc.scaledTouchSlop
        mMinFlingVelocity = vc.scaledMinimumFlingVelocity
        mMaxFlingVelocity = vc.scaledMaximumFlingVelocity
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
            maxScrollLength = headHeight - headLeftSize

            // 如果content设置了marginTop值，需要把marginTop减掉
            val lp = contentView!!.layoutParams as FrameLayout.LayoutParams
            val contentHeight = measuredHeight - (headHeight - maxScrollLength) - lp.topMargin
            contentView!!.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(contentHeight, View.MeasureSpec.EXACTLY))
        }
    }

    private val isContentCanScroll: Boolean
        get() {
            return onContentReadForScrollListener?.onContentReadForScroll()
                    ?: mNestedScrollChild?.canScrollVertically(-1)
                    ?: contentView?.canScrollVertically(-1)
                    ?: true
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

    private fun initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        if (ev.action == MotionEvent.ACTION_DOWN) {
        }

        return super.onInterceptTouchEvent(ev)


    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

//        TLog.d("dispatchTouchEvent", "onInterceptTouchEvent:")
        if (ev.action == MotionEvent.ACTION_DOWN) {
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        initVelocityTrackerIfNotExists()
        val y = event.y
        val actionMasked = event.actionMasked
        when (actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                }

                // 记住点击的位置
                mLastTouchY = y.toInt().toFloat()
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = y - mLastTouchY

                // 触发拖动
                if (!mDragging && Math.abs(dy) > mTouchSlop) {
                    mDragging = true
                }

                if (mDragging) {
                    handleScroll((-dy).toInt(), null)
                    mLastTouchY = y
                }
            }
            MotionEvent.ACTION_UP -> {

                val velocityTracker = mVelocityTracker
                velocityTracker!!.computeCurrentVelocity(1000, mMaxFlingVelocity.toFloat())
                val initialVelocity = velocityTracker.yVelocity.toInt()

                if (Math.abs(initialVelocity) > mMinFlingVelocity) {
                    handleFling((-initialVelocity).toFloat())
                } else {
                    autoAnim()
                }

                mDragging = false
            }
            MotionEvent.ACTION_CANCEL -> {
                autoAnim()
                mDragging = false
            }
            else -> {
            }
        }

        if (mVelocityTracker != null) {
            mVelocityTracker!!.addMovement(event)
        }
        return true
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
                onStickStateChangeListener?.invoke(this, false)
                STATE_OPENED
            }
            maxScrollLength -> {
                onStickStateChangeListener?.invoke(this, true)
                STATE_CLOSED
            }
            else -> STATE_CENTER
        }

        if (onStickyScrollListener != null) {
            onStickyScrollListener!!.onStickyScroll(y, maxScrollLength)
        }


        super.scrollTo(0, y)
    }

    private fun fling(velocityY: Float): Boolean {
        mScroller.fling(0, scrollY, 0, velocityY.toInt(), 0, 0, 0, maxScrollLength)
        invalidate()
        return true
    }

    private fun autoAnim() {
        if (!mScroller.isFinished) { // 正在执行动画，表示Fling已经触发了动画了
            return
        }
        val currentScrollY = scrollY
        if (currentScrollY > maxScrollLength / 2) {
            animClose()
        } else {
            animOpen()
        }
    }

    fun animClose() {
        val dy = maxScrollLength - scrollY
        var duration = Math.abs(dy * 3)
        if (duration > 500) {
            duration = 500
        }
        mScroller.startScroll(scrollX, scrollY, 0, dy, duration)
        invalidate()


        onStickStateChangeListener?.invoke(this, true)
    }

    fun animOpen() {
        val dy = 0 - scrollY
        var duration = Math.abs(dy * 3)
        if (duration > 500) {
            duration = 500
        }
        mScroller.startScroll(scrollX, scrollY, 0, dy, duration)
        invalidate()

        onStickStateChangeListener?.invoke(this, false)
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            val y = mScroller.currY
            scrollToInner(y)
            invalidate()
        }
    }




    // impl NestedScrollingParent

    /**
     * 当前是否正在做嵌套滑动
     */
    var isNestedScrolling = false

    // 子View请求开启嵌套滑动流程
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        Log.d(TAG, "onStartNestedScroll:$child, target:$target, nest:$nestedScrollAxes")

        // 外部来决定是否开启嵌套滑动
        val isNestedScroll = onStartNestedScrollListener?.onStartNestedScroll(child, target, nestedScrollAxes) ?: true

        if (isNestedScroll) {
            mNestedScrollChild = target
        }

        isNestedScrolling = isNestedScroll

        // 要嵌套滑动
        return isNestedScroll
    }

    private var axes = 0;

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

        Log.d(TAG, "onStopNestedScoll:")
        isNestedScrolling = false
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

        Log.d(TAG, "state:$state")

        when (state) {
            STATE_OPENED -> if (dy < 0) { // down

            } else if (dy > 0) { // up
                scroll(dy, consumed)
            }
            STATE_CLOSED // 收起状态
            -> if (dy < 0) { // 如果是向下滑动的话，先检查Content是否可以滑动，如果Content可以向下滑的话，让Content自己先滑，否则Parent才滑
                if (!isContentCanScroll) {
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


    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        Log.d(TAG, "fling:$velocityY")
        return handleFling(velocityY)
    }

    var isHandleFling = true

    private fun handleFling(velocityY: Float): Boolean {
        Log.d(TAG, "fling:$velocityY")

        if (velocityY > 0) { // 上滑
            if (state != STATE_CLOSED) { // 没有关闭的话，触发拉起动画，拦截fling操作
                animClose()
                return isHandleFling
            }
        } else if (velocityY < 0) { // 下滑
            if (!isContentCanScroll) { // ListView没有滑动到顶部的话优先子View自己处理
                animOpen()
                //                return true;
            }
        }
        when (state) {
            STATE_OPENED -> {
            }
            STATE_CLOSED -> {
            }
            STATE_CENTER -> {
            }
        }
        return false
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= 21)
            return super.onNestedFling(target, velocityX, velocityY, consumed)
        return  false
    }

    override fun getNestedScrollAxes(): Int {
        if (Build.VERSION.SDK_INT >= 21)
            return axes
        return super.getNestedScrollAxes()
    }

    /**
     *
     */
    fun setOnContentReadForScrollListener(action: OnContentReadForScrollListener) {
        this.onContentReadForScrollListener = action
    }

    fun setOnStickyScrollListener(onStickyScrollListener: OnStickyScrollListener) {
        this.onStickyScrollListener = onStickyScrollListener
    }

    fun setOnStartNestedScrollListener(onStartNestedScrollListener: OnNestedScrollListener) {
        this.onStartNestedScrollListener = onStartNestedScrollListener
    }


    /**
     * 展开状态监听
     */
    var onStickStateChangeListener: ((v: StickyLayout, isClose: Boolean)->Unit)? = null

    /**
     * 头部滑动监听器
     */
    interface OnStickyScrollListener {

        /**
         * 头部滑动监听
         *
         * @param currentScrollLength 当前滑动的距离
         * @param maxScrollLength     最大滑动距离
         */
        fun onStickyScroll(currentScrollLength: Int, maxScrollLength: Int)
    }

    /**
     * 当头部收起来的时候，用户向下滑动的时候我们希望Content先滑动，当Content不能滑动的时候才滑动头部
     */
    interface OnContentReadForScrollListener {
        fun onContentReadForScroll(): Boolean
    }

    interface OnNestedScrollListener {
        fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean
        fun onStopNestedScroll(child: View)
    }
}
