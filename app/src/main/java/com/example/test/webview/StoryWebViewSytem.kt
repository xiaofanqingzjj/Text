package com.example.test.webview

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView



/**
 * 新的小说的app
 *
 * @author fortune
 */
class StoryWebViewSytem(context: Context, attrs: AttributeSet? = null) : WebView(context, attrs) {

    companion object {
        const val TAG = "StoryWebView"
    }

//    private val mNestedScrollHelper = X5NestedScrollingViewHelper(this)

    init {
//        enableNestedScroll()
    }
//
//    /**
//     * 开启嵌套滑动
//     */
//    fun enableNestedScroll() {
//        isNestedScrollingEnabled = true
//        mNestedScrollHelper.setNestedScrollingEnabled(true)
//    }
//
//    override fun hasNestedScrollingParent(): Boolean {
//        return if (mNestedScrollHelper.isNestedScrollingEnabled()) {
//            mNestedScrollHelper.hasNestedScrollingParent()
//        } else {
//            super.hasNestedScrollingParent()
//        }
//    }
//
//    override fun hasNestedScrollingParent(type: Int): Boolean {
//        return mNestedScrollHelper.hasNestedScrollingParent(type)
//    }
//
//    override fun startNestedScroll(axes: Int): Boolean {
//        return if (mNestedScrollHelper.isNestedScrollingEnabled()) {
//            mNestedScrollHelper.startNestedScroll(axes)
//        } else {
//            super.startNestedScroll(axes)
//        }
//    }
//
//    override fun startNestedScroll(axes: Int, type: Int): Boolean {
//        return mNestedScrollHelper.startNestedScroll(axes, type)
//    }
//
//    override fun stopNestedScroll() {
//        if (mNestedScrollHelper.isNestedScrollingEnabled()) {
//            mNestedScrollHelper.stopNestedScroll()
//        } else {
//            super.stopNestedScroll()
//        }
//    }
//
//    override fun stopNestedScroll(type: Int) {
//        return mNestedScrollHelper.stopNestedScroll(type)
//    }
//
//    override fun dispatchNestedPreScroll(
//            dx: Int,
//            dy: Int,
//            consumed: IntArray?,
//            offsetInWindow: IntArray?
//    ): Boolean {
//        return if (mNestedScrollHelper.isNestedScrollingEnabled()) {
//            mNestedScrollHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
//        } else {
//            super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
//        }
//    }
//
//    override fun dispatchNestedPreScroll(
//            dx: Int,
//            dy: Int,
//            consumed: IntArray?,
//            offsetInWindow: IntArray?,
//            type: Int
//    ): Boolean {
//        return mNestedScrollHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
//    }
//
//    override fun dispatchNestedScroll(
//            dxConsumed: Int,
//            dyConsumed: Int,
//            dxUnconsumed: Int,
//            dyUnconsumed: Int,
//            offsetInWindow: IntArray?
//    ): Boolean {
//        return if (mNestedScrollHelper.isNestedScrollingEnabled()) {
//            mNestedScrollHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
//        } else {
//            super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
//        }
//    }
//
//    override fun dispatchNestedScroll(
//            dxConsumed: Int,
//            dyConsumed: Int,
//            dxUnconsumed: Int,
//            dyUnconsumed: Int,
//            offsetInWindow: IntArray?,
//            type: Int
//    ): Boolean {
//        return mNestedScrollHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type)
//    }
//
//    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
//        return mNestedScrollHelper.dispatchNestedPreFling(velocityX, velocityY)
//    }
//
//    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
//        return mNestedScrollHelper.dispatchNestedFling(velocityX, velocityY, consumed)
//    }
//
//    override fun computeScroll() {
//        if (mNestedScrollHelper.isNestedScrollingEnabled()) {
//            if (!mNestedScrollHelper.computeScroll()) super.computeScroll()
//        } else {
//            super.computeScroll()
//        }
//    }

//    override fun evaluateJavascript(script: String?, resultCallback: WebViewJavaScriptPlugin.WebViewAware.ValueCallback<String?>?) {
//        evaluateJavascript(script) {
//            resultCallback?.onReceiveValue(it)
//        }
//    }
}