package com.tencent.story.userguide

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.fortunexiao.testbookuserguide.R
import com.fortunexiao.tktx.dp2px
import com.fortunexiao.tktx.mapChild
import com.fortunexiao.tktx.runUIThread
import com.tencent.story.userguide.util.AnimListenerStub
import com.tencent.story.userguide.util.AnimUtil
import kotlinx.android.synthetic.main.layout_category.view.*
import kotlinx.android.synthetic.main.layout_category_cell.view.*

/**
 *
 * 新手引导标签选择页面
 *
 * @author fortunexiao
 *
 */
class CategoryLayout(context: Context, attributes: AttributeSet? = null) : LinearLayout(context, attributes) {


    private var line1Container: LinearLayout
    private var line2Container: LinearLayout

    private val layoutInflater = LayoutInflater.from(context)

    private val allCells: MutableList<View> = mutableListOf()

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_category, this)

        orientation = LinearLayout.VERTICAL

        line1Container = ll_line1_container
        line2Container = ll_line2_container
    }

    /**
     * 设置数据
     */
    fun setData(data: List<Category>) {

        // 根据数组的大小做不通的页面布局
        if (data.size <= 4) { // 1行
            data.forEach {
                addCell(line1Container, it)
            }
        } else if (data.size == 5) {
            for (i in 0..1) {
                addCell(line1Container, data[i])
            }
            for (i in 2..4) {
                addCell(ll_line2_container, data[i])
            }
        } else if (data.size == 6) {
            for (i in 0..2) {
                addCell(line1Container, data[i])
            }
            for (i in 3..5) {
                addCell(ll_line2_container, data[i])
            }
        } else if (data.size == 7) {
            for (i in 0..2) {
                addCell(line1Container, data[i])
            }
            for (i in 3..6) {
                addCell(ll_line2_container, data[i])
            }
        } else if (data.size == 8) {
            for (i in 0..3) {
                addCell(line1Container, data[i])
            }
            for (i in 4..7) {
                addCell(ll_line2_container, data[i])
            }
        }

        // 执行pop动画
        runUIThread(1200) {
            startPopAnim()
        }
    }


    private fun addCell(lineContainer: LinearLayout, category: Category): View {
        val view = layoutInflater.inflate(R.layout.layout_category_cell, lineContainer, false)

        view.alpha = 0f

        // TODO 设置数据
        view.tag = category
        view.setOnClickListener {
            clickCell(view)
        }

        lineContainer.addView(view)

        allCells.add(view)

        return view
    }

    private fun clickCell(view: View) {
        val category = view.tag as Category
        val selectView = view.iv_selected

        if (category.isSelected) {
            animSelectViewShowOrHide(selectView, false)
            category.isSelected = false
        } else {
            animSelectViewShowOrHide(selectView, true)
            category.isSelected = true
        }
    }


    private fun animSelectViewShowOrHide(selectView: View, isShow: Boolean) {
        // 星球先动
        AnimatorSet().apply {
            val starAnims = mutableListOf<ObjectAnimator>().apply {
                addAll(AnimUtil.scale(selectView, 1f, 1.2f, 1f))
                // 隐藏掉lottie引导按钮
                if (isShow) {
                    add(ObjectAnimator.ofFloat(selectView, View.ALPHA, 0f, 1f))
                } else {
                    add(ObjectAnimator.ofFloat(selectView, View.ALPHA, 1f, 0f))
                }
            }
            playTogether(*starAnims.toTypedArray())
            duration = 200
        }.start()
    }



    // -------------------- 下面是播放动画 --------------------
    // -------------------- 下面是播放动画 --------------------
    // -------------------- 下面是播放动画 --------------------


    private fun startPopAnim() {
        allCells.forEachIndexed { index, view ->
            popAnimCell(view, index * 200L)
        }
    }

    private fun popAnimCell(cellView: View, delay: Long) {

        // 星球先动
        AnimatorSet().apply {
            val starAnims = mutableListOf<ObjectAnimator>().apply {
                addAll(AnimUtil.scale(cellView, 1f, 1.2f, 1f))

                // 隐藏掉lottie引导按钮
                add(ObjectAnimator.ofFloat(cellView, View.ALPHA, 0f, 1f))
            }


            playTogether(*starAnims.toTypedArray())
            duration = 500
            this.startDelay = delay
        }.start()
    }


    /**
     * 硬币收起动画的最后一个View
     */
    private var lastMoveView: View? = null


    /**
     * 收起动画
     */
    fun startSelectedTransformAnim() {
        AnimatorSet().apply {
            val anims = mutableListOf<ObjectAnimator>()

            // 向猫爪子方向移动
            val targetX = 200.dp2px(context).toFloat()
            val targetY = -65.dp2px(context).toFloat()

            // 最后一个要移动的View
            var lastView : View? = null

            line1Container.mapChild { i, v ->

                val ivSelected = v.iv_selected

                val category = v.tag as? Category
                if (category?.isSelected == true) { // 用户选择的标签
                    // 移动
                    anims.addAll(AnimUtil.translateTo(v, Pair(targetX, targetY)))
                    // 缩小
                    anims.addAll(AnimUtil.scale(v, 0.7f))

                    // 选中的钩要隐藏掉
                    anims.add(AnimUtil.alpha(ivSelected, 1f, 0f, 0f, 0f))

                    lastView = v
                } else { // 没选择的渐隐
                    anims.add(AnimUtil.alpha(v, 0f))
                }

            }
            line2Container.mapChild { i, v ->

                val ivSelected = v.iv_selected

                val category = v.tag as? Category
                if (category?.isSelected == true) { // 用户选择的标签
                    // 第二行要减去第一行的高度
                    anims.addAll(AnimUtil.translateTo(v, Pair(targetX, targetY - line1Container.height)))

                    // 缩小
                    anims.addAll(AnimUtil.scale(v, 0.7f))

                    // 选中的钩要隐藏掉
                    anims.add(AnimUtil.alpha(ivSelected, 1f, 0f, 0f, 0f))

                    lastView = v
                } else {
                    anims.add(AnimUtil.alpha(v, 0f))
                }
            }

            lastMoveView = lastView

            playTogether(*anims.toTypedArray())
            duration = 400

            addListener(object : AnimListenerStub() {
                override fun onAnimationEnd(animation: Animator?) {
                    allCells?.forEach {
                        if (it != lastView) it.visibility = View.INVISIBLE
                    }
                }
            })
        }.start()
    }


    /**
     * 配合小猫的手把硬币移动一下，并隐藏
     */
    fun moveAndGone() {
        val lastView = lastMoveView ?: return
        ObjectAnimator.ofFloat(lastView,  View.TRANSLATION_Y, lastView.y - 15.dp2px(context)).apply {
            duration = 200
            startDelay = 100
        }.start()

        runUIThread(300) {
            ObjectAnimator.ofFloat(lastMoveView!!, View.ALPHA, 0f).start()
        }

    }

}