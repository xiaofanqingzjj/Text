package com.example.test.test_float_comment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bedrock.module_base.util.DensityUtil
import com.example.test.R

/**
 * 可以从底部弹出一个Fragment的Dialog
 *
 * 可以通过关闭
 *
 */
class SheetDialogFragment : DialogFragment() {

    companion object {

        const val TAG = "SheetDialogFragment"

        fun show(context: Context?, fragment: Fragment) {
            if (context is FragmentActivity) {
                val fm = context.supportFragmentManager
                val floatDialog = SheetDialogFragment()
                floatDialog.fragment = fragment
                floatDialog.show(fm, "commentListFloatDialog")
            }
        }
    }

    /**
     * 要显示的容器
     */
    var fragment: Fragment? = null

    private lateinit var mRoot: View
    private lateinit var mBg: View
    var mDragDismissView: DragDismissView? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupWindow()
    }

    private fun setupWindow() {
        dialog?.run {
            window?.run {
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height)
                setGravity(Gravity.BOTTOM)

                addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
            setCanceledOnTouchOutside(true)

            // 拦截按钮，所有的按钮都由Fragment自行处理
//            setOnKeyListener(object : OnKe`)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sheet_dialog_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (fragment != null) {
            childFragmentManager.beginTransaction().replace(R.id.fr_content_container, fragment!!).commitNowAllowingStateLoss()
        }

        mRoot = view.findViewById(R.id.root)
        mBg = view.findViewById(R.id.iv_bg)

        mDragDismissView = view.findViewById<DragDismissView>(R.id.drag_dismiss_view)?.apply {
            onDismiss = { // 滑动结束，关闭
                animFinish()
            }

            onCanDrag = {
                val onCanDrag = (fragment as DragDismissView.CanDragListener).canDrag()
                Log.d(TAG, "onCanDrag:$onCanDrag")
                onCanDrag
            }
        }

        playEnterAnim()
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

    private fun playExitAnim() {
        AnimatorSet().apply {
            playTogether(mutableListOf<Animator>().apply {

                if (screenOrientation() == 0) {
                    // 从底部滑出屏幕
                    add(ObjectAnimator.ofFloat(mRoot, View.TRANSLATION_Y, mRoot.height.toFloat()))
                } else {
                    // 从右边滑出屏
                    add(ObjectAnimator.ofFloat(mRoot, View.TRANSLATION_X, mRoot.width.toFloat()))
                }

                add(ObjectAnimator.ofFloat(mBg, View.ALPHA, 0f))
            })
//            playTogether(
//                ObjectAnimator.ofFloat(mRoot, View.TRANSLATION_Y, mRoot.height.toFloat()),
//                ObjectAnimator.ofFloat(mBg, View.ALPHA, 0f)
//            )
            addListener(object : SimpleAnimListener() {
                override fun onAnimationEnd(animation: Animator) {
                    finish()
                }
            })
        }.start()
    }


    private fun animFinish() {
        playExitAnim()
    }

    private fun finish() {
        dismissAllowingStateLoss();
    }

    private fun playEnterAnim() {
        mRoot.alpha = 1f
        val ctx = context ?: return

        AnimatorSet().apply {

            playTogether(mutableListOf<Animator>().apply {

                if (screenOrientation() == 0) {
                    // 从底部滑入
                    val height = if (mRoot.height != 0) mRoot.height else ctx.resources.displayMetrics.heightPixels
                    add(ObjectAnimator.ofFloat(mRoot, View.TRANSLATION_Y, height.toFloat(), 0f))
                } else {
                    val width = if (mRoot.height != 0) mRoot.width else DensityUtil.dip2px(context!!, 380f)
                    // 从右边滑入
                    add(ObjectAnimator.ofFloat(mRoot, View.TRANSLATION_X, width.toFloat(), 0f))
                }

                add(  ObjectAnimator.ofFloat(mBg, View.ALPHA, 0f, 1f))
            })


//            playTogether(
//                    ObjectAnimator.ofFloat(mRoot, View.TRANSLATION_Y, height.toFloat(), 0f),
//                    ObjectAnimator.ofFloat(mBg, View.ALPHA, 0f, 1f)
//            )
        }.start()
    }



    override fun getTheme(): Int {
        return android.R.style.Theme_Panel
    }

    internal open class SimpleAnimListener : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    }
}