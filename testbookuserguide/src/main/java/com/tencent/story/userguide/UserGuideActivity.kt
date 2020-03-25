package com.tencent.story.userguide

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.fortunexiao.testbookuserguide.R
import com.fortunexiao.tktx.runUIThread
import com.tencent.story.userguide.util.AnimListenerStub
import com.tencent.story.userguide.util.AnimUtil
import kotlinx.android.synthetic.main.activity_userguide.*

class UserGuideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userguide)

        requestCategory()

        btn_next.setOnClickListener {
            showNext()
        }

        runUIThread {
            startBeginAnim()
        }
    }


    private fun startBeginAnim() {

        // 初始lottie
        lav_cat1.playAnimation()

        // 小提示
        AnimatorSet().apply {
            val starAnims = mutableListOf<ObjectAnimator>().apply {
                addAll(AnimUtil.scale(iv_select_category_tip, 0.5f, 1.2f, 1f))
                add(AnimUtil.alpha(iv_select_category_tip, 0f, 1f))
            }
            playTogether(*starAnims.toTypedArray())
            duration = 300
            startDelay = 1000
        }.start()

        runUIThread(300) {
            AnimUtil.alphaVisibility(btn_skip, isShow = true)
        }
    }


    /**
     * 点击确定后执行的动画
     */
    private fun showNext() {

        // 选择的硬币收集再一起
        category_layout.startSelectedTransformAnim()
        btn_next.isEnabled = false


        runUIThread(500) {

            // 隐藏掉第一个lottie
            lav_cat1.visibility = View.INVISIBLE

            // 再播放lottie，小猫把收起的硬币拿走
            lav_cat2.visibility = View.VISIBLE
            lav_cat2.playAnimation()


            // 硬币被小猫拿走
            category_layout.moveAndGone()
        }

        // 第一个动画隐藏
        runUIThread (700) {
            AnimUtil.alphaVisibility(iv_select_category_tip, isShow =  false)
        }

        // 隐藏确定按钮
        runUIThread(200) {
            AnimUtil.alphaVisibility(btn_next, isShow =  false)
        }

        lav_cat2.addAnimatorListener(object : AnimListenerStub(){
            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                lav_cat3.visibility = View.VISIBLE
                lav_cat3.playAnimation()

                runUIThread {
                    lav_cat2.visibility = View.GONE
                }
            }
        })

        // 隐藏确定按钮
        runUIThread(4000) {
            AnimUtil.alphaVisibility(iv_select_category_tip2, isShow =  true)
        }

        // 换背景
        runUIThread(3000) {
            iv_bg.visibility = View.VISIBLE
            AnimUtil.alphaVisibility(iv_bg, duration =  800, isShow =  true)
        }
    }


    private fun requestCategory() {
        runUIThread(500) {
            category_layout.setData(testData())

            // 确定按钮
            runUIThread(2500) {
                AnimUtil.alphaVisibility(btn_next, 500, true)
            }
        }
    }

    private fun testData(): MutableList<Category> {
        return mutableListOf<Category>().apply {
            add(Category())
            add(Category())
            add(Category())
            add(Category())
            add(Category())
            add(Category())
            add(Category())
        }
    }
}
