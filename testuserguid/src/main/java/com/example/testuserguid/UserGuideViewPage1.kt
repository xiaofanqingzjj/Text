package com.example.testuserguid

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.example.testuserguid.util.runUIThread


/**
 * 新手引导第一页
 *
 * @author fortunexiao
 *
 */
class UserGuideViewPage1(context: Context, attrs: AttributeSet? = null) : UserGuideViewPageBase(context, attrs) {


    // page1

    private val flashStarView: FlashStarBaseView
    private val lottieView: LottieAnimationView

    private val meteorViewBg: MeteorView
    private val meteorViewContent: MeteorView

    private val ivStar1: View
    private val ivStar2: View

    private val ivBookLight: View
    private val ivBookLight2: View

    private val llBookContainer: View

    private val btnNext: View

    private var isAniming = false

    /**
     * 点击进入下一页
     */
    var onClickNext: ((startTime: Long) -> Unit)? = null

    private val bookLightAnim: ValueAnimator
    private val bookLightAnim2: ValueAnimator

    init {

        LayoutInflater.from(context).inflate(R.layout.view_user_guide_page1, this)

        lottieView = findViewById(R.id.lottie_view)

        flashStarView = findViewById(R.id.fsv_page_stars)
        ivStar1 = findViewById(R.id.iv_blue_star)
        ivStar2 = findViewById(R.id.iv_yellow_star)

        ivBookLight = findViewById(R.id.iv_book_light)
        ivBookLight2 = findViewById(R.id.iv_book_bottom_light)

        llBookContainer = findViewById(R.id.ll_book_container)

        btnNext = findViewById(R.id.lav_next)


        meteorViewBg = findViewById(R.id.mv_bg)
        meteorViewContent = findViewById(R.id.mv_book_content)


        btnNext.setOnClickListener {
            if (!isAniming) {
                goNext()
            }
        }

        // 光呼吸
        bookLightAnim = AnimUtil.flash(ivBookLight, values = *floatArrayOf(0.4f, 1f))
        bookLightAnim2 = AnimUtil.flash(ivBookLight2, values = *floatArrayOf(0.4f, 1f))

        bookLightAnim.start()
        bookLightAnim2.start()

        // lottie动画
        runUIThread({
            lottieView.apply {
                imageAssetsFolder = "lotties/userguide/01/images"
                setAnimation("lotties/userguide/01/data.json")
                lottieView.playAnimation()
            }
        }, 0)

        flashStarView.startFlash()
        meteorViewBg.start()

        // 和背景的流星错开
        runUIThread({
            meteorViewContent.start()
        }, 1000)
    }


    private fun goNext() {

        isAniming = true

        // 星球先动
        AnimatorSet().apply {
            val moveSize = 200f
            val scale = 1.8f

            val starAnims = mutableListOf<ObjectAnimator>().apply {

                addAll(AnimUtil.translateTo(ivStar1, Pair(ivStar1.x + dp2px(moveSize), ivStar1.y - dp2px(moveSize))))
                addAll(AnimUtil.scale(ivStar1, scale))

                addAll(AnimUtil.translateTo(ivStar2, Pair(ivStar2.x - dp2px(moveSize), ivStar2.y + dp2px(moveSize))))
                addAll(AnimUtil.scale(ivStar2, scale))

                // 隐藏掉星光
                add(ObjectAnimator.ofFloat(flashStarView, View.ALPHA, 0f))

                // 隐藏掉lottie引导按钮
                add(ObjectAnimator.ofFloat(btnNext, View.ALPHA, 0f))
            }


            playTogether(*starAnims.toTypedArray())
            duration = 1000

        }.start()

        val scaleDuration = 600L

        // 书本再放大
        AnimatorSet().apply {

            val bookAnims = mutableListOf<ObjectAnimator>().apply {
                addAll(AnimUtil.scale(llBookContainer, 3f))
                addAll(AnimUtil.translateTo(llBookContainer, Pair(llBookContainer.x - dp2px(170f), llBookContainer.y + dp2px(80f))))

                // 把lottie也隐藏
                add(ObjectAnimator.ofFloat(lottieView, View.ALPHA, 0f))
            }



            playTogether(*bookAnims.toTypedArray())
            duration = scaleDuration
            startDelay = 200
        }.start()


        // 这个时间要给下一页用
        val alphaStartTime: Long = scaleDuration

        // 最后书渐变消失
        ObjectAnimator.ofFloat(llBookContainer, View.ALPHA, 0f).apply {

            duration = 600
            startDelay = alphaStartTime

            // 动画执行完之后,关闭该关闭的动画
            addEndListener {
                stopAnim()
            }
        }.start()

        onClickNext?.invoke(alphaStartTime)
    }

    private fun stopAnim() {
        flashStarView.stopFlash()
        bookLightAnim.cancel()
        bookLightAnim2.cancel()

        meteorViewBg.stop()
        meteorViewContent.stop()
    }
}