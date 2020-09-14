package com.example.testpermission.ad

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Rect
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.util.Property
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import kotlin.math.max

class MyHeader(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {


    var orgTop = 0


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top + orgTop, right, orgTop + bottom)
        Log.d("MyBehavior", "top:${this.top}, bottom:${this.bottom}, org:$orgTop")
    }



    class MyHeadBehavior(context: Context, attributeSet: AttributeSet) : ViewOffsetBehavior<View>(context, attributeSet) {

        companion object {
            const val TAG = "MyBehavior"



            private val OFFSET = object : Property<MyHeadBehavior, Int>(Int::class.java, "offset") {
                override fun get(`object`: MyHeadBehavior): Int {
                    return `object`.topAndBottomOffset
                }

                override fun set(`object`: MyHeadBehavior, value: Int) {
                    `object`.setTopAndBottomOffset(value)
                }
            }
        }

        var maxScrollDistance = 0

        private var myHeader: MyHeader? = null

        private fun acquireTempRect(): Rect {
            return Rect()
        }

        override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
            val r = super.onLayoutChild(parent, child, layoutDirection)

            Log.d(TAG, "layoutChild:${child.top}, topOffset:$topAndBottomOffset")

//
//            parent.run {
//                val lp = child.layoutParams as MarginLayoutParams
//                val parent = acquireTempRect()
//                parent.set(paddingLeft + lp.leftMargin,
//                        paddingTop + lp.topMargin,
//                        width - paddingRight - lp.rightMargin,
//                        height - paddingBottom - lp.bottomMargin)
//
////                if (mLastInsets != null && ViewCompat.getFitsSystemWindows(this)
////                        && !ViewCompat.getFitsSystemWindows(child)) {
////                    // If we're set to handle insets but this child isn't, then it has been measured as
////                    // if there are no insets. We need to lay it out to match.
////                    parent.left += mLastInsets.getSystemWindowInsetLeft()
////                    parent.top += mLastInsets.getSystemWindowInsetTop()
////                    parent.right -= mLastInsets.getSystemWindowInsetRight()
////                    parent.bottom -= mLastInsets.getSystemWindowInsetBottom()
////                }
//
//                val out = acquireTempRect()
//                GravityCompat.apply(Gravity.TOP, child.measuredWidth, child.measuredHeight, parent, out, layoutDirection)
//
//                Log.d(TAG, "layoutChild:${out.top}, topOffset:$topAndBottomOffset")
//
//                child.layout(out.left, out.top + topAndBottomOffset,
//                        out.right, out.bottom)
//
////                releaseTempRect(parent)
////                releaseTempRect(out)
//            }


//
//            val left = 0
//            val top = 0 // (child as MyHeader)
//            val right = parent.right
//            val bottom = child.measuredHeight
//
//            child.layout(left, top, right, bottom)
//
//
//
//            child.measuredHeight

            this.maxScrollDistance = child.height

//            Log.d(TAG, "layoutChild max:$maxScrollDistance")

            this.myHeader = child as? MyHeader
            return true
        }

        override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
            snapAnimator?.run {
                cancel()
                snapAnimator = null
            }
            return true
        }


        override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed)

//            Log.d(TAG, "onScroll:$dy, child:${child.top}, offset:${topAndBottomOffset}")

//            Log.e(TAG, "childTop:${child.top}, height:${child.height}, dy:${dy}")

            var dy = -dy // 让dy转个方向，更适合计算，>0向下，<0向上
            val offset = topAndBottomOffset // 当前的offset

            // 检测边界，超过边界后修改dy的值
            dy = check(offset, dy, 0, maxScrollDistance)

            if ((dy > 0 && !target.canScrollVertically(-1)) // 向下滑动，且内容不可滑的时候滑动
                    || dy < 0) { // 向上滑动的时候
                setTopAndBottomOffset(topAndBottomOffset + dy)
//                notifyOffset(topAndBottomOffset, maxScrollDistance)
                consumed[1] = -dy // 把dy的方向复原
            }


        }

        override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
            super.onStopNestedScroll(coordinatorLayout, child, target, type)
            snapBackIfNeed()
        }

        override fun setTopAndBottomOffset(offset: Int): Boolean {



            if (topAndBottomOffset != offset) {
                notifyOffset(offset, maxScrollDistance)
            }
            return super.setTopAndBottomOffset(offset)
        }


        private var snapAnimator: ObjectAnimator? = null

        private fun snapBackIfNeed() {
            if (topAndBottomOffset > -maxScrollDistance) {
                snapAnimator = ObjectAnimator.ofInt<MyHeadBehavior>(this, OFFSET, 0).apply {

//                    // 回弹的时候也需要通知监听器
//                    addUpdateListener {
//                        notifyOffset(it.animatedValue as Int, maxScrollDistance)
//                    }

                    interpolator = DecelerateInterpolator()
                    duration = 300
                }

                snapAnimator?.start()
            }

        }

        /**
         * 检测当一个值变化一定的值时的边界检测
         * @param current 当前值
         * @param dalt 变化值
         * @param min 左边界
         * @param max 右边界
         * @return 能在范围变化的合理值
         */
        fun check(current: Int, dalt: Int, min: Int, max: Int): Int {
            val destValue = current + dalt

            return if (destValue > max) {
                max - current
            } else if (destValue < min) {
                min - current
            } else dalt
        }


        private fun notifyOffset(offset: Int, max: Int) {
            myHeader?.dispatchOffsetUpdates(offset, max)
        }

    }


    /**
     * 底部View的Behavior
     */
    class MyScrollBehavior(context: Context, attributeSet: AttributeSet?) : CoordinatorLayout.Behavior<View>(context, attributeSet) {

        override fun layoutDependsOn(parent: CoordinatorLayout?, child: View?, dependency: View?): Boolean {
            return dependency is MyHeader
        }

        override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {

            Log.w("MyBehavior", "onDependentViewChanged:${dependency.bottom}, top:${dependency.top}")
            offsetChildAsNeeded(parent, child, dependency)

            (dependency as? MyHeader)?.orgTop = dependency.top

            return super.onDependentViewChanged(parent, child, dependency)
        }


        private fun offsetChildAsNeeded(parent: CoordinatorLayout, child: View, dependency: View) {
            ViewCompat.offsetTopAndBottom(child, (dependency.bottom - child.top))
        }
    }


    interface OnOffsetChangedListener {
        fun onOffsetChanged(verticalOffset: Int, maxOffset: Int)
    }

    private var mListeners: MutableList<OnOffsetChangedListener> = mutableListOf()

    fun addOnOffsetChangedListener(listener: OnOffsetChangedListener) {
        mListeners.add(listener)
    }

    fun removeOnOffsetChangedListener(listener: OnOffsetChangedListener) {
        mListeners.remove(listener)
    }


    fun dispatchOffsetUpdates(offset: Int, maxScrollDistance: Int) {
        mListeners.map {
            it.onOffsetChanged(offset, maxScrollDistance)
        }
    }
}


/**
 * Behavior will automatically sets up a [ViewOffsetHelper] on a [View].
 */
 open class ViewOffsetBehavior<V : View> constructor(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<V>(context, attrs) {

    private var mViewOffsetHelper: ViewOffsetHelper? = null

    private var mTempTopBottomOffset = 0
    private var mTempLeftRightOffset = 0

    val topAndBottomOffset: Int
        get() = if (mViewOffsetHelper != null) mViewOffsetHelper!!.getTopAndBottomOffset() else 0

    val leftAndRightOffset: Int
        get() = if (mViewOffsetHelper != null) mViewOffsetHelper!!.getLeftAndRightOffset() else 0


    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        // First let lay the child out
        layoutChild(parent, child, layoutDirection)

        if (mViewOffsetHelper == null) {
            mViewOffsetHelper = ViewOffsetHelper(child)
        }
        mViewOffsetHelper?.onViewLayout()

        if (mTempTopBottomOffset != 0) {
            mViewOffsetHelper!!.setTopAndBottomOffset(mTempTopBottomOffset)
            mTempTopBottomOffset = 0
        }
        if (mTempLeftRightOffset != 0) {
            mViewOffsetHelper!!.setLeftAndRightOffset(mTempLeftRightOffset)
            mTempLeftRightOffset = 0
        }

        return true
    }

    protected open fun layoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int) {
        parent.onLayoutChild(child, layoutDirection)
    }

    open fun setTopAndBottomOffset(offset: Int): Boolean {
        if (mViewOffsetHelper != null) {
            return mViewOffsetHelper!!.setTopAndBottomOffset(offset)
        } else {
            mTempTopBottomOffset = offset
        }
        return false
    }

    open fun setLeftAndRightOffset(offset: Int): Boolean {
        if (mViewOffsetHelper != null) {
            return mViewOffsetHelper!!.setLeftAndRightOffset(offset)
        } else {
            mTempLeftRightOffset = offset
        }
        return false
    }
}

internal class ViewOffsetHelper(private val mView: View) {

    private var layoutTop: Int = 0
    private var layoutLeft: Int = 0

    private var mOffsetTop: Int = 0
    private var mOffsetLeft: Int = 0

    fun onViewLayout() {
        // Now grab the intended top
        layoutTop = mView.top
        layoutLeft = mView.left

        // And offset it as needed
        updateOffsets()
    }

    private fun updateOffsets() {
        ViewCompat.offsetTopAndBottom(mView, mOffsetTop - (mView.top - layoutTop))
        ViewCompat.offsetLeftAndRight(mView, mOffsetLeft - (mView.left - layoutLeft))
    }

    fun setTopAndBottomOffset(offset: Int): Boolean {
        if (mOffsetTop != offset) {
            mOffsetTop = offset
            updateOffsets()
            return true
        }
        return false
    }

    fun setLeftAndRightOffset(offset: Int): Boolean {
        if (mOffsetLeft != offset) {
            mOffsetLeft = offset
            updateOffsets()
            return true
        }
        return false
    }

    fun getTopAndBottomOffset(): Int {
        return mOffsetTop
    }

    fun getLeftAndRightOffset(): Int {
        return mOffsetLeft
    }
}