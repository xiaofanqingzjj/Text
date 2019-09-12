package com.example.testpermission.ad

import android.content.Context
import android.os.Looper
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.util.Property
import android.view.View
import android.widget.FrameLayout
import android.widget.OverScroller
import com.example.testpermission.ad.my.ViewOffsetBehavior
import java.util.logging.Handler


//@CoordinatorLayout.DefaultBehavior(MyHeadLayout.MyBehavior::class)
class MyHeadLayout(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    init {

    }


    class MyBehavior(context: Context, attrs: AttributeSet?) : ViewOffsetBehavior<MyHeadLayout>(context, attrs) {

        companion object {

            const val TAG = "MyBehavior"

            internal const val STATE_OPENED = 0
            internal const val STATE_CLOSED = 1
            internal const val STATE_CENTER = 2


//            private val OFFSET = object : Property<MyBehavior, Int>(Int::class.java, "offset") {
//
//                override fun set(`object`:MyBehavior, value: Int) {
////                super.set(`object`, value)
//                    `object`.topAndBottomOffset = value
//                }
//
//                override fun get(`object`: MyBehavior): Int {
//                    return `object`.topAndBottomOffset
//                }
//            }
        }

        private var mScroller: OverScroller

        private var state = STATE_CLOSED


        private var height = 0

        init {
            mScroller = OverScroller(context)
        }

        private fun isContentCanScroll(child: View): Boolean {
            return child.canScrollVertically(-1)
        }


        override fun onLayoutChild(parent: CoordinatorLayout?, child: MyHeadLayout, layoutDirection: Int): Boolean {

            val r = super.onLayoutChild(parent, child, layoutDirection)
            height = 300// child.height
            return r
        }

        override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: MyHeadLayout, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
            return true
        }

//        override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: MyHeadLayout, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
//            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
//
//            if (type == V)
//
//        }

        override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: MyHeadLayout, target: View, dx: Int, dy: Int, consumed: IntArray) {
//            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed)
            handleScroll(child, dy, consumed, target)
        }


        private fun handleScroll(child: MyHeadLayout, dy: Int, consumed: IntArray, target: View) {
            if (!mScroller.isFinished) {
                mScroller.abortAnimation()
            }


            val newDy = -dy
//            Log.d(TAG, "handleScroll state:$state, dy:$newDy, top:${child.top}")
//
//            val min = -height
//            val max = 0
//
//            val top = child.top
//
//
//            if (newDy > 0) { // down
//
//
//
//            } else if (newDy < 0) { // up
//
//            }
//
//            if (top == min) { // 只能下滑
//                if (newDy > 0) {
//
//                }
//
//            }
//
//
//
//            child.offsetTopAndBottom(newDy)
//
//            consumed[1] = dy


            topAndBottomOffset  += newDy



//            when (state) {
//
//                STATE_CLOSED-> {
//                    if (newDy > 0) { // down
//                        if (!isContentCanScroll(target)) {
//                            scroll(child, target, newDy, consumed)
//                        }
//                    }
//                }
//
//                STATE_OPENED-> {
//                    if (newDy < 0) { // up
//                        scroll(child, target, newDy, consumed)
//                    }
//                }
//                STATE_CENTER -> scroll(child, target, newDy, consumed)
//
////                AutoHideHeaderLayout.STATE_OPENED -> {
////
////
////
////
////                    if (newDy < 0) { // down
////
////                    } else if (newDy > 0) { // up
////                        scroll(child, newDy, consumed)
////                    }
////                }
////                AutoHideHeaderLayout.STATE_CLOSED-> { // 收起状态
////                    if (dy < 0) { // 如果是向下滑动的话，先检查Content是否可以滑动，如果Content可以向下滑的话，让Content自己先滑，否则Parent才滑
////                        if (!isContentCanScroll(child)) {
////                            scroll(child, newDy, consumed)
////                        }
////                    } else if (newDy > 0) { // up
////
////                    }
////                }
////                AutoHideHeaderLayout.STATE_CENTER -> scroll(child, newDy, consumed)
//            }
        }

        private fun scroll(child: MyHeadLayout, taget: View, dy: Int, consumed: IntArray) {
            scrollByInner(dy, child, taget)

            consumed[1] = dy
        }


        private fun scrollByInner(y: Int, child: MyHeadLayout, taget: View) {
            scrollToInner(child.top + y, child, taget)
        }

        private fun scrollToInner(y: Int, child: MyHeadLayout, taget: View) {


            var y = y

            if (y <= -height) {
                y = -height
                if (state == STATE_CLOSED) {
                    return
                }
            }

            if (y >= 0) {
                y = 0
                if (state == STATE_OPENED) {
                    return
                }
            }

//            state = STATE_CENTER

            state = when (y) {
                0 -> {
                    STATE_OPENED
                }
                -height -> {
                    STATE_CLOSED
                }
                else -> STATE_CENTER
            }

//
//            state = when (y) {
//                0 -> {
//                    onStickStateChangeListener?.invoke(this, false)
//                    AutoHideHeaderLayout.STATE_OPENED
//                }
//                maxScrollLength -> {
//                    onStickStateChangeListener?.invoke(this, true)
//                    AutoHideHeaderLayout.STATE_CLOSED
//                }
//                else -> AutoHideHeaderLayout.STATE_CENTER
//            }
//
//
//
//            if (y <=  0) {
//                y = 0
//                if (state == AutoHideHeaderLayout.STATE_OPENED) {
//                    return
//                }
//            }
//
//            if (y >= maxScrollLength) {
//                y = maxScrollLength
//                if (state == AutoHideHeaderLayout.STATE_CLOSED) {
//                    return
//                }
//            }
//
//            state = when (y) {
//                0 -> {
//                    onStickStateChangeListener?.invoke(this, false)
//                    AutoHideHeaderLayout.STATE_OPENED
//                }
//                maxScrollLength -> {
//                    onStickStateChangeListener?.invoke(this, true)
//                    AutoHideHeaderLayout.STATE_CLOSED
//                }
//                else -> AutoHideHeaderLayout.STATE_CENTER
//            }
//
//            if (onStickyScrollListener != null) {
//                onStickyScrollListener!!.onScroll(y, maxScrollLength)
//            }

            topAndBottomOffset = y - topAndBottomOffset
//            ViewCompat.offsetTopAndBottom(child, y - child.top)

            Log.d(TAG, "offsetTopAndBottom.bottom:${child.bottom}, listViewTop:${taget.top}")
//            child.offsetTopAndBottom(y - child.top)

//            super.scrollTo(0, y)
        }

//        override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: MyHeadLayout, target: View, type: Int) {
//            super.onStopNestedScroll(coordinatorLayout, child, target, type)
//        }

        override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: MyHeadLayout, target: View) {
            super.onStopNestedScroll(coordinatorLayout, child, target)
            Log.d(TAG, "onStopNestedScroll.bottom:${child.bottom}, listViewTop:${target.top}")
        }


    }


    class MyScrollingViewBehavior(context: Context, attrs: AttributeSet) : AppBarLayout.ScrollingViewBehavior(context, attrs) {

        private val handler = android.os.Handler()

        override fun layoutDependsOn(parent: CoordinatorLayout?, child: View?, dependency: View?): Boolean {
            return dependency is MyHeadLayout
        }


        override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
            offsetChildAsNeeded(parent, child, dependency!!)
            return true
        }


//        private fun offsetChildAsNeeded(parent: CoordinatorLayout, child: View, dependency: View) {
//            val behavior = (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior
//            if (behavior is Behavior) {
//                // Offset the child, pinning it to the bottom the header-dependency, maintaining
//                // any vertical gap and overlap
//                val ablBehavior = behavior as AppBarLayout.Behavior?
//                ViewCompat.offsetTopAndBottom(child, (dependency.bottom - child.top
//                        + ablBehavior!!.mOffsetDelta
//                        + getVerticalLayoutGap()) - getOverlapPixelsForOffset(dependency))
//            }
//        }

        private fun offsetChildAsNeeded(parent: CoordinatorLayout, child: View, dependency: View) {
            Log.e(MyBehavior.TAG, "offsetChildAsNeeded.bottom:${dependency.bottom}, listViewTop:${child.top}")

//            handler.post {
                ViewCompat.offsetTopAndBottom(child, (dependency.bottom - child.top))
//            }

        }
    }
}