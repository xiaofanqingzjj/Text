package com.ch.animdemo.demo.transition.imageviewer

import android.content.Context
import android.graphics.Point
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.bedrock.module_base.util.toPx
import com.ch.animdemo.demo.transition.imageviewer.helper.ViewDragHelper
import com.ch.animdemo.phoneView.PhotoView
import kotlin.math.abs
import kotlin.math.absoluteValue


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

    private val orgPos: Point = Point()
    private val finalPos: Point = Point()

    /**
     * 当前的动画是否动画到结束
     */
//    private var isAnimToFinish = false

    /**
     * 当前是否正在执行动画
     */
    private var isAnimming = false


    /**
     * 关闭窗口的回调
     */
    var onDismiss : (()->Unit)? = null


    /**
     * 向下拖动的时候需要把图片变小
     *
     * 这个距离是把图片滑动到最小的距离
     */
    val maxScoll = 300f.toPx(context)


    /**
     * 滑动位置监听
     */
    var onScrollChangeListener : ((top: Int, maxRange: Int)->Unit)? = null

    init {
        viewDragHelper = ViewDragHelper.create(this, 0.15f, object : ViewDragHelper.Callback() {

            /**
             * 判断哪个View可以拖动
             */
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                if (child == dragView) {


                    // 正在执行动画的时候不能拖动
                    return !isAnimming
                }
                return false
            }

            /**
             * 拖动的位置
             */
            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
////                if (top > 0) { // 只能向下拖动
////                    return top
////                }
//
//                if (top > 0) { // 向下拖动了， 图片缩小，背景变淡
//
//                    val scale  = 0.5f + (maxScoll - top).toFloat() / maxScoll.toFloat() * 0.5f
//                    Log.d(TAG, "top: $top, dy:$dy scale:$scale, test${(maxScoll - top).toFloat() / maxScoll.toFloat() * 0.5f}")
////                    dragView?.run {
////                        scaleX = scale
////                        scaleY = scale
////                    }
//                } else {
//                    dragView?.run {
//                        scaleX = 1f
//                        scaleY = 1f
//                    }
//                }

                return top
            }


            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {



                return left
//                return super.clampViewPositionHorizontal(child, left, dx)
            }

            /**
             * 手指松开
             */
            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                Log.d(TAG, "onViewReleased:${yvel}, releaseViewTop:${releasedChild.top}")
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
            }

            /**
             * 拖动范围
             */
            override fun getViewVerticalDragRange(child: View): Int {



                return child.height
            }


            override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
                super.onViewPositionChanged(changedView, left, top, dx, dy)
                onScrollChangeListener?.invoke(top, maxScoll)

                if (top > 0) { // 向下拖动了， 图片缩小，背景变淡
                    var s =  (maxScoll - top).toFloat() / maxScoll.toFloat()
                    if (s < 0) {
                        s = 0f
                    }
                    val scale  = 0.5f + s * 0.5f
//                    Log.d(TAG, "top: $top, dy:$dy scale:$scale")
                    dragView?.run {
                        scaleX = scale
                        scaleY = scale
                    }
                } else {
                    changedView?.run {
                        scaleX = 1f
                        scaleY = 1f
                    }
                }
            }

            override fun checkTouchSlop(child: View?, dx: Float, dy: Float): Boolean {



                if (abs(dy) > abs(dx)) { // 竖着滑动

                    val attacher = (dragView as? PhotoView)?.attacher ?: return true
                    val rect: RectF = attacher.getDisplayRect(attacher.drawMatrix) ?: return true

                    Log.d(TAG, "checkTouchSlop:$dx, $dy, rect:$rect")

                    if (dy > 0) { // 向下滑动
                        if (rect.top < 0) {
                            return false
                        }
                    } else if (dy < 0) { // 向上滑动
                        if (rect.bottom > dragView?.height ?: 0) {
                            return  false
                        }
                    }

                    return super.checkTouchSlop(child, dx, dy)
                }

                return false
            }

        })
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
            orgPos.x = left
            orgPos.y = top

            finalPos.x = left
            finalPos.y = bottom
        }
    }

    private fun isDonNotIntercept(): Boolean {

        val attacher = (dragView as? PhotoView)?.attacher ?: return false
        val rect: RectF = attacher.getDisplayRect(attacher.drawMatrix) ?: return false

        val height = rect.height()
        val width = rect.width()

        return (dragView as? PhotoView)?.attacher?.isMutiTouch == true
    }

    var mode = 0

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        when (ev.getAction() and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> mode = 1
            MotionEvent.ACTION_UP -> mode = 0

            MotionEvent.ACTION_POINTER_DOWN -> mode += 1
            MotionEvent.ACTION_POINTER_UP -> mode -= 1

        }




        val drag =  viewDragHelper.shouldInterceptTouchEvent(ev)

        val isDragViewIsMutiTouch = isDonNotIntercept()
        Log.d(TAG, "onInterceptTouchEvent:$mode， shouldDrag:$drag, :action:${ev.action and MotionEvent.ACTION_MASK}，isDragViewIsMutiTouch：$isDragViewIsMutiTouch")

        if (isDragViewIsMutiTouch) { // 如果当前的View上是多点触控，则不拦截
            return false
        }
        return drag
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.getAction() and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> mode = 1
            MotionEvent.ACTION_UP -> mode = 0

            MotionEvent.ACTION_POINTER_DOWN -> mode += 1
            MotionEvent.ACTION_POINTER_UP -> mode -= 1

        }


        Log.e(TAG, "onTouchEvent:$mode， :action:${event.action and MotionEvent.ACTION_MASK}")

        viewDragHelper.processTouchEvent(event)
        return true
    }


    override fun computeScroll() {
        super.computeScroll()
        if (viewDragHelper.continueSettling(true)) {
            invalidate()
        } else { // 动画执行结束
            isAnimming = false

//            if (isAnimToFinish) {
//                onDismiss?.invoke()
//            }
        }
    }


    private fun animReposition() {
        viewDragHelper.settleCapturedViewAt(orgPos.x, orgPos.y)
        invalidate()
        isAnimming = true
    }

    private fun animFinish() {
        onDismiss?.invoke()
//        viewDragHelper.settleCapturedViewAt(finalPos.x, finalPos.y)
//        invalidate()
//        isAnimming = true
//        isAnimToFinish = true
    }

}