package com.example.test.test_float_comment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bedrock.module_base.recycleradapter.BaseViewTypeAdapter
import com.example.test.R
import com.example.test.test_float_comment.InputPanelHelper.showInputPanel
import java.util.*

class CommentListFragment : Fragment(), DragDismissView.CanDragListener {

//    private lateinit var mRoot: View
//    private lateinit var mBg: View
//    var mDragDismissView: DragDismissView? = null

    private var recyclerView: RecyclerView? = null

    /**
     * 通知容器关闭当前的界面
     */
//    var onFinish: (()->Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_float_comment_list, container, false)
    }


//
//    private fun playExitAnim() {
//
//        val animatorSet = AnimatorSet()
//
//        val builder = animatorSet.play(ObjectAnimator.ofFloat(mRoot, View.TRANSLATION_Y, mRoot.height.toFloat()))
//        if (mBg != null) {
//            builder.with(ObjectAnimator.ofFloat(mBg, View.ALPHA, 0f))
//        }
//
//        animatorSet.addListener(object : SimpleAnimListener() {
//            override fun onAnimationEnd(animation: Animator) {
//                finish()
//            }
//        })
//
//        animatorSet.start()
//
//
//    }
//
//
//    fun animFinish() {
//        playExitAnim()
//    }
//
//    fun finish() {
//        onFinish?.invoke()
//    }
//
//
//    private fun playEnterAnim() {
//        mRoot?.alpha = 1f
//        val ctx = context ?: return
//        val animatorSet = AnimatorSet()
//
//        val height = if (mRoot.height != 0) mRoot.height else ctx.resources.displayMetrics.heightPixels
//
//        val builder = animatorSet.play(ObjectAnimator.ofFloat(mRoot, View.TRANSLATION_Y, height.toFloat(), 0f))
//
//        if (mBg != null) {
//            builder.with(ObjectAnimator.ofFloat(mBg, View.ALPHA, 0f, 1f))
//        }
//
//        animatorSet.start()
//    }
//
//
//    internal open class SimpleAnimListener : Animator.AnimatorListener {
//        override fun onAnimationStart(animation: Animator) {}
//        override fun onAnimationEnd(animation: Animator) {}
//        override fun onAnimationCancel(animation: Animator) {}
//        override fun onAnimationRepeat(animation: Animator) {}
//    }


    fun canPullDown(): Boolean {
        return recyclerView?.canScrollVertically(1) ?: false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mRoot = view.findViewById(R.id.root)
//        mBg = view.findViewById(R.id.iv_bg)
        recyclerView = view.findViewById(R.id.list_view)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        val data: MutableList<String> = ArrayList()
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        data.add("aaa")
        val adapter = MyAdapter(context, data)
        recyclerView?.adapter = adapter
        val input = view.findViewById<View>(R.id.btn_input_comment)
        input.setOnClickListener { v -> showInputPanel(v.context) }

//        mDragDismissView = view.findViewById<DragDismissView>(R.id.drag_dismiss_view)
//
//        mDragDismissView = view.findViewById<DragDismissView>(R.id.drag_dismiss_view)?.apply {
//            onDismiss = { // 滑动结束，关闭
//                animFinish()
//            }
//        }
//
//
////        runUIThread(50) {
////            mRoot?.alpha = 1f
//            playEnterAnim()
////        }
    }

//    override fun onKeyEvent(code: Int, event: KeyEvent?) {
//        if (code == KeyEvent.KEYCODE_BACK) {
//            animFinish()
//        }
//    }

    class MyAdapter(context: Context?, data: List<String>?) : BaseViewTypeAdapter<String?>(context, data) {


        class ViewHolder : ViewTypeViewHolder<String?>() {
            override fun onCreate() {
                super.onCreate()
                setContentView(android.R.layout.simple_list_item_1)
            }

            protected override fun bindData(position: Int, data: String?) {
                val tv = itemView.findViewById<TextView>(android.R.id.text1)
                tv.text = data
            }
        }

        init {
            addViewType(ViewHolder::class.java)
        }
    }

    override fun canDrag(): Boolean {
        return recyclerView?.canScrollVertically(1) ?: false
    }
}