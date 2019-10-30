package com.example.testpermission.ad

import android.animation.ObjectAnimator
import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import android.util.AttributeSet
import android.util.Property
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.example.testpermission.DensityUtil


/**
 *
 * 扩张自AppBarLayout的Behavior，
 *
 * @author fortunexiao
 *
 */
class AppBarZoomBehavior(var context: Context, attrs: AttributeSet) : AppBarLayout.Behavior(context, attrs) {


    companion object {

        internal val TAG = "AppBarZoomBehavior"

        private val MAX_ZOOM_HEIGHT = 500f//放大最大高度


        private val OFFSET = object : Property<AppBarLayout.Behavior, Int>(Int::class.java, "offset") {

            override fun set(`object`: AppBarLayout.Behavior, value: Int) {
//                super.set(`object`, value)
                `object`.topAndBottomOffset = value
            }

            override fun get(`object`: AppBarLayout.Behavior): Int {
                return `object`.topAndBottomOffset
            }
        }
    }


    private var mAppBarHeight: Int = 0//记录AppbarLayout原始高度

    private var snapAnimator: ObjectAnimator? = null;

    private var myAppBarLayout: MyAppBarLayout? = null

    private var initOffset = 0

    override fun onLayoutChild(parent: CoordinatorLayout?, abl: AppBarLayout, layoutDirection: Int): Boolean {
        val handled = super.onLayoutChild(parent, abl, layoutDirection)
        init(abl)

//        myAppBarLayout = abl as? MyAppBarLayout
//        mAppBarHeight = abl.height

        return handled
    }

    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        super.onAttachedToLayoutParams(params)



//
//        myAppBarLayout = abl as? MyAppBarLayout
//
//        // appBar的高度
//        mAppBarHeight = abl.height


        // 初始位置
        initOffset = -DensityUtil.dip2px(context, 200f )
        topAndBottomOffset = initOffset
        notifyOffset(topAndBottomOffset)
    }

    private fun init(abl: AppBarLayout) {
//        abl.clipChildren = false

        myAppBarLayout = abl as? MyAppBarLayout

        // appBar的高度
        mAppBarHeight = abl.height


//        // 初始位置
//        initOffset = -DensityUtil.dip2px(abl.context, 100f * 3)
//        topAndBottomOffset = initOffset
//        notifyOffset(topAndBottomOffset)
    }

    override fun onTouchEvent(parent: CoordinatorLayout?, child: AppBarLayout?, ev: MotionEvent): Boolean {
        val superResult =   super.onTouchEvent(parent, child, ev)

        when (ev.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                cancelAnim()
            } MotionEvent.ACTION_UP -> {
                snapBackIfNeed()
            }
            MotionEvent.ACTION_CANCEL -> {
                snapBackIfNeed()
            }
        }
        return superResult
    }


    private fun cancelAnim() {
        snapAnimator?.cancel()
        snapAnimator = null
    }


    override fun onStartNestedScroll(parent: CoordinatorLayout, child: AppBarLayout, directTargetChild: View, target: View, nestedScrollAxes: Int, type: Int): Boolean {
        cancelAnim()
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val newPos = topAndBottomOffset - dy
        if (newPos > initOffset) {
            if (topAndBottomOffset < initOffset) { //还是正常逻辑，但是不能全部滑动
                val newDy = topAndBottomOffset - initOffset
                topAndBottomOffset -= newDy
                notifyOffset(topAndBottomOffset)
            } else {
                if (type == ViewCompat.TYPE_TOUCH) {

                    if (newPos <= 0) {
                        topAndBottomOffset -= dy
                    } else { //
                        if (topAndBottomOffset < 0) {
                            val newDy = topAndBottomOffset - 0
                            topAndBottomOffset -= newDy
//                            notifyOffset(topAndBottomOffset)
                        }
                    }

                    notifyOffset(topAndBottomOffset)

                }
            }
            consumed[1] = dy // 全部消耗事件
        } else { // 正常逻辑
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        }


//        if (newPos < initOffset) { // 正常逻辑
//            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
//        } else  {
//
//            if (topAndBottomOffset < initOffset) { //还是正常逻辑，但是不能全部滑动
//                val newDy = topAndBottomOffset - initOffset
//                topAndBottomOffset -= newDy
////                super.onNestedPreScroll(coordinatorLayout, child, target, dx, newDy, consumed, type)
//            } else {
//                if (type == ViewCompat.TYPE_TOUCH) {
//                    topAndBottomOffset -= dy
//                }
//            }
//
//            consumed[1] = dy // 全部消耗事件
//        }

//
//        if (topAndBottomOffset - dy > initOffset) {
//
//        }
//
//        if (topAndBottomOffset >= initOffset) {// 超过最大开始回弹操作
//            if (type == ViewCompat.TYPE_TOUCH) {
//                topAndBottomOffset -= dy
//            }
//            consumed[1] = dy // 全部消耗事件
//        } else {
//            // 走父类正常逻辑
//            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
//        }
    }


    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)

        snapBackIfNeed()
    }

    private fun snapBackIfNeed() {
        if (topAndBottomOffset > initOffset) {
            snapAnimator = ObjectAnimator.ofInt<AppBarLayout.Behavior>(this, OFFSET, initOffset).apply {
                // 监听动画
                addUpdateListener {
                    notifyOffset(topAndBottomOffset)
                }
                interpolator = DecelerateInterpolator()
                duration = 300
            }

            snapAnimator?.start()
        }
    }

    private fun notifyOffset(offset: Int) {
        myAppBarLayout?.dispatchOffsetUpdates(offset)
    }
}