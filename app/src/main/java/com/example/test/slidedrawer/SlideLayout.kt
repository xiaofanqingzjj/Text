package com.example.test.slidedrawer

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.example.test.R
import java.lang.RuntimeException


/**
 *
 * 一个抽屉
 *
 * @author fortunexiao
 */
class SlideLayout(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    companion object {
        const val TAG = "DragDismissView"
    }

    private var dragView: View? = null

    private val viewDragHelper: ViewDragHelper

    private val orgPos: Point = Point()
    private val finalPos: Point = Point()

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
     * 滑动位置监听
     */
    var onScrollChangeListener : ((top: Int, maxRange: Int)->Unit)? = null

    /**
     *
     */
    var leftSize: Int = 0

    /**
     * 手柄View
     */
    var thumbView: View? = null

    private var thumbId: Int = 0


    /**
     * 一个开光，是否允许打开或关闭
     */
    var enableSlide = false


    /**
     * 初始是否是打开状态
     */
    var isInitOpen = false


    init {

        val t = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout)

        leftSize = t.getDimension(R.styleable.SlideLayout_sl_closeSize, 100f).toInt()
        thumbId = t.getResourceId(R.styleable.SlideLayout_sl_thumb, 0)

        t.recycle()


        viewDragHelper = ViewDragHelper.create(this, 1f, object : ViewDragHelper.Callback() {

            /**
             * 判断哪个View可以拖动
             */
            override fun tryCaptureView(child: View, pointerId: Int, x: Int, y: Int): Boolean {

                if (!enableSlide) { // 先判断开关
                    return false
                }

                val thumb = thumbView ?: return false
                if (child == dragView && isTouchInView(child, thumb, x, y)) {
                    // 正在执行动画的时候不能拖动
                    return !isAnimming
                }
                return false
            }



            /**
             * 拖动的位置
             */
            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                Log.d(TAG, "clampViewPositionVertical:$top")
                if (top >= orgPos.y && top <= finalPos.y) { // 只能向下拖动
                    return top
                }
                return child.top
            }

            /**
             * 手指松开弹到目标位置
             */
            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                Log.d(TAG, "onViewReleased:${yvel}, releaseViewTop:${releasedChild.top}")
                if (yvel > 500) {
                    open()
                } else if (yvel < -500) {
                    close()
                } else {
                    val top = releasedChild.top
                    if (top >= (orgPos.y + finalPos.y) / 2) {
                        open()
                    } else {
                        close()
                    }
                }
            }

            /**
             * 拖动范围
             */
            override fun getViewVerticalDragRange(child: View): Int {
                return child.height - leftSize
            }

            override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
                super.onViewPositionChanged(changedView, left, top, dx, dy)
                onScrollChangeListener?.invoke(top, height)
            }
        })

    }

    private fun isTouchInView(parent: View,  child: View,  x: Int, y: Int): Boolean {

        Log.d(TAG, "onTouchInView pos:($x,$y), view:${child.left},${child.top}, ${child.right}, ${child.bottom}")

        if (x >= (child.left + parent.left ) && x < child.right  + parent.left  && y >= child.top  + parent.top && y < child.bottom + parent.top) {
            return true
        }
        return false
    }



    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount != 2) {
            throw RuntimeException("must two child")
        }

        dragView = getChildAt(1)
        (dragView?.layoutParams as? FrameLayout.LayoutParams)?.gravity = Gravity.BOTTOM

        if (thumbId != 0) {
            thumbView = dragView?.findViewById(thumbId)
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)


        dragView?.let {
            orgPos.x = 0
            orgPos.y = 0

            finalPos.x = 0
            finalPos.y = measuredHeight - leftSize
        }

        if (isInitOpen) {
            currentTop = finalPos.y
        }
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        offset(currentTop)
        isInitOpen = false
        Log.e(TAG, "onLayout: org:$orgPos, final:$finalPos， dragViewTop:${dragView?.top}")
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev)
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

    private var currentTop = 0


    fun close(withAnim: Boolean = true) {
        if (withAnim) {
            if (dragView != null) {
                viewDragHelper.smoothSlideViewTo(dragView!!, orgPos.x, orgPos.y)
            }
//            viewDragHelper.settleCapturedViewAt(orgPos.x, orgPos.y)
            invalidate()
            isAnimming = true
        } else {
            offset(orgPos.y)
        }
        currentTop = orgPos.y
    }


    private fun offset(targetPos: Int) {
        dragView?.run {
            offsetTopAndBottom(targetPos - top)

            Log.w(TAG, "offset:${top}")
        }
    }

    fun open(withAnim: Boolean = true) {
        if (withAnim) {
            if (dragView != null) {
                viewDragHelper.smoothSlideViewTo(dragView!!, finalPos.x, finalPos.y)
            }
//            viewDragHelper.settleCapturedViewAt(finalPos.x, finalPos.y)
            invalidate()
            isAnimming = true
            isAnimToFinish = true
        } else {
            offset(finalPos.y)
        }
        currentTop = finalPos.y
    }

}