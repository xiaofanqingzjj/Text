package com.example.test.test_float_comment

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper


/**
 *
 * 向下滑动关闭当前View
 * @author fortunexiao
 */
class DragDismissView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    companion object {
        const val TAG = "DragDismissView"
    }

    private var dragView: View? = null

    private val viewDragHelper: ViewDragHelper

//    private val orgPos: Point = Point()
//    private val finalPos: Point = Point()

    /**
     * 当前的动画是否动画到结束
     */
    private var isAnimToFinish = false

    /**
     * 当前是否正在执行动画
     */
    private var isAnimming = false


    /**
     * 关闭窗口的回调
     */
    var onDismiss : (()->Unit)? = null

    /**
     * 是否可以拖动先在外面判断一下
     */
    var onCanDrag : (()->Boolean)? = null

    /**
     * 滑动位置监听
     */
    var onScrollChangeListener : ((top: Int, maxRange: Int)->Unit)? = null


    init {
        viewDragHelper = ViewDragHelper.create(this, 1f, object : ViewDragHelper.Callback() {

            /**
             * 判断哪个View可以拖动
             */
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                var canDrag = false
                if (child == dragView) {
                    // 正在执行动画的时候不能拖动
                    canDrag = (onCanDrag?.invoke() ?: true) && !isAnimming
                }
                Log.d(TAG, "tryCaptureView: $canDrag")
                return canDrag;
            }

            /**
             * 拖动的位置
             */
            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                if (screenOrientation() == 0) {
                    if (top > 0) { // 只能向下拖动
                        return top
                    }
                }
                return 0
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                if (screenOrientation() == 1) {
                    if (left > 0) { // 只能向下拖动
                        return left
                    }
                }
                return super.clampViewPositionHorizontal(child, left, dx)
            }

            /**
             * 手指松开
             */
            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                Log.d(TAG, "onViewReleased:${yvel}, releaseViewTop:${releasedChild.top}, height:$height")
                if (screenOrientation() == 0) {
                    if (yvel > 500) {
                        animFinish()
                    } else {
                        val top = releasedChild.top
                        if (top >= height / 3) {
                            animFinish()
                        } else {
                            animReposition()
                        }
                    }
                } else {
                    if (xvel > 500) {
                        animFinish();
                    } else {
                        val left = releasedChild.left
                        if (left >= width / 3) {
                            animFinish()
                        } else {
                            animReposition()
                        }
                    }
                }
            }

            /**
             * 拖动范围
             */
            override fun getViewVerticalDragRange(child: View): Int {
                return child.height
            }

            override fun getViewHorizontalDragRange(child: View): Int {
                return child.width
            }


            override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
                super.onViewPositionChanged(changedView, left, top, dx, dy)
//                Log.d(TAG, "onViewPositionChanged:$top， height: ${height}")
                onScrollChangeListener?.invoke(top, height)
            }

        })
    }


    /**
     * 屏幕方向，0为竖屏，1为横屏
     */
    private fun screenOrientation(): Int {
        return if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            0
        } else {
            1
        }
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            dragView = getChildAt(0)
        }
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        dragView?.run {
//            orgPos.x = left
//            orgPos.y = top
//
//            finalPos.x = left
//            finalPos.y = bottom
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val drag =  viewDragHelper.shouldInterceptTouchEvent(ev)
//        Log.d(TAG, "shouldInterceptTouchEvent:${drag}")
        return drag
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
        return true
    }


    override fun computeScroll() {
        super.computeScroll()
        if (viewDragHelper.continueSettling(true)) {
            invalidate()
        } else { // 动画执行结束
            isAnimming = false

            if (isAnimToFinish) {
                onDismiss?.invoke()
            }
        }
    }


    private fun animReposition() {

        viewDragHelper.settleCapturedViewAt(0, 0)
        invalidate()
        isAnimming = true
    }

    private fun animFinish() {
        if (screenOrientation() == 0) {
            viewDragHelper.settleCapturedViewAt(0, dragView?.height ?: 0)
        } else {
            viewDragHelper.settleCapturedViewAt(dragView?.width ?: 0, 0)
        }


        invalidate()
        isAnimming = true
        isAnimToFinish = true
    }


    interface CanDragListener {
        fun canDrag(): Boolean
    }
}