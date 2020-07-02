package com.example.testuserguid

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.PointF
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.testuserguid.util.DensityUtil
import com.example.testuserguid.util.runUIThread

/**
 *
 * 新手引导第二页
 *
 * @author fortunexiao
 *
 */
class UserGuideViewPage2(context: Context, attrs: AttributeSet? = null) : UserGuideViewPageBase(context, attrs) {


    companion object {
        const val STATE_SELECT_SEX = 0
        const val STATE_SELECT_TAG = 1
        const val STATE_SELECT_BOOK = 2

        const val TAG = "UserGuideViewPage2"

    }

    private val flashStarView: FlashStarBaseView
    private val meteorView: MeteorView

    // 两颗大星球
    private var ivStarBlue: View
    private var ivStarYellow: ImageView

    // 男女选择
    private var pageSelectSex: UserGuideViewPage2SexSelect

    // 标签选择
    private var pageSelectTag: UserGuideViewPage2TagSelect

    // 书籍推荐
    private var pageRecommendBook: UserGuideViewPage2RecommendBook


    var state: Int = STATE_SELECT_SEX


    // 星球的位置
    var blueStarOrgPos: PointF = PointF()
    var yellowStarOrgPos = PointF()

    private val mMatrix = Matrix()

    // 菱形参数
    private val valueX = dp2px(150f)
    private val valueY = dp2px(-20f)


    private val btnNext: TextView

    /**
     * 用户的性别
     */
    private var sex: Int = 0

    private var mainTag: Int = 0

    private var subTags: MutableList<String> = mutableListOf()

    init {

        LayoutInflater.from(context).inflate(R.layout.view_user_guide_page2, this)

        flashStarView = findViewById(R.id.fsv_page_stars)
        meteorView = findViewById(R.id.meteor_view)

        ivStarBlue = findViewById(R.id.iv_blue_star)
        ivStarYellow = findViewById(R.id.iv_yellow_star)

        pageSelectSex = findViewById(R.id.page_select_sex)
        pageSelectTag = findViewById(R.id.page_select_tag)
        pageRecommendBook = findViewById(R.id.page_recommend_book)

        btnNext = findViewById<TextView>(R.id.btn_next).apply {
            setOnClickListener {
                goNext()
            }
        }

        // 初始状态为不可点击
        enableBtnNext(false)


        pageSelectSex.run {
            // 选择了性别才可以点下一步
            onSexSelected = {
                enableBtnNext(true)
//                btnNext.isEnabled = true
                sex = it
            }
        }

        pageSelectTag.run {
            // 标签展开的时候要把星球移走
            onTagExtendListener = {
                AnimatorSet().apply {
                    val distance = 120f
                    playTogether(*mutableListOf<ObjectAnimator>().apply {
                        addAll(AnimUtil.translateTo(ivStarYellow, Pair(ivStarYellow.x + dp2px(distance).toFloat(), null)))
                        addAll(AnimUtil.translateTo(ivStarBlue, Pair(ivStarBlue.x - dp2px(distance).toFloat(), null)))
                    }.toTypedArray())
                }.start()
            }

            onSubTagSelectListener = { mainTag, subTags ->
                Log.d(TAG, "onTagSelected $mainTag, subs:$subTags")
                enableBtnNext(subTags.size != 0)

                this@UserGuideViewPage2.mainTag = mainTag
                this@UserGuideViewPage2.subTags.run {
                    clear()
                    addAll(subTags)
                }
            }
        }


        // 初始显示性别选择
        pageSelectSex.show()
    }

    private fun goNext() {
        when (state) {
            STATE_SELECT_SEX -> {
                goSelectTags()
            }
            STATE_SELECT_TAG -> {
                goRecommendBook()
            }
        }
    }

    private fun goRecommendBook() {
        pageSelectTag.hide()
        pageRecommendBook.show()

        // 旋转星球
        startStarBallAnim2()

        // 隐藏按钮
        ObjectAnimator.ofFloat(btnNext, View.ALPHA, 0f).start()

        state = STATE_SELECT_BOOK

    }

    private fun enableBtnNext(enable: Boolean) {
        btnNext.isEnabled = enable
        if (enable) {
            btnNext.alpha  = 1f
        } else {
            btnNext.alpha  = 0.5f
        }
    }


    // 跳转到标签页
    private fun goSelectTags() {
        pageSelectSex.hide()
        pageSelectTag.show()

        // 标签里也需要用户选择的性别
        pageSelectTag.sex = sex

        startStarBallAnim()
        state = STATE_SELECT_TAG

        enableBtnNext(false)
//        btnNext.isEnabled = false
    }


    // 星球旋转动画
    private fun startStarBallAnim() {
        // 星球的位置
        blueStarOrgPos = AnimUtil.positionOf(ivStarBlue)
        yellowStarOrgPos = AnimUtil.positionOf(ivStarYellow)


        // 计算菱形的4个顶点位置
        val bottomLeft = floatArrayOf(yellowStarOrgPos.x, yellowStarOrgPos.y)
        val bottomRight = floatArrayOf(yellowStarOrgPos.x, yellowStarOrgPos.y)

        val topLeft = floatArrayOf(blueStarOrgPos.x, blueStarOrgPos.y)
        val topRight = floatArrayOf(blueStarOrgPos.x, blueStarOrgPos.y)

        mMatrix.reset()

        // 先左移动
        mMatrix.postTranslate(-valueX.toFloat(), -valueY.toFloat())
        mMatrix.mapPoints(bottomLeft)
        mMatrix.mapPoints(topLeft)

        mMatrix.reset()

        // 右移动
        mMatrix.postTranslate(valueX.toFloat(), valueY.toFloat())
        mMatrix.mapPoints(bottomRight)
        mMatrix.mapPoints(topRight)


        // 黄色星球的轨迹
        val yellowPath = Path().apply {
            moveTo(yellowStarOrgPos.x, yellowStarOrgPos.y)
            cubicTo(bottomRight[0], bottomRight[1], topRight[0], topRight[1], blueStarOrgPos.x, blueStarOrgPos.y)
        }

        // 蓝色星球的轨迹
        val bluePath = Path().apply {
            moveTo(blueStarOrgPos.x, blueStarOrgPos.y)
            cubicTo(topLeft[0], topLeft[1], bottomLeft[0], bottomLeft[1], yellowStarOrgPos.x, yellowStarOrgPos.y)
        }

        // 星球动画
        AnimatorSet().apply {
            playTogether(*mutableListOf<ObjectAnimator>().apply {
                if (Build.VERSION.SDK_INT >= 21) {
                    add(ObjectAnimator.ofFloat(ivStarYellow, AnimUtil.CX, AnimUtil.CY, yellowPath))
                } else {
                    add(ObjectAnimator.ofFloat(ivStarYellow, AnimUtil.CX, blueStarOrgPos.x))
                    add(ObjectAnimator.ofFloat(ivStarYellow, AnimUtil.CY, blueStarOrgPos.y))
                }
                addAll(AnimUtil.scale(ivStarYellow, 1f, 2f, 1f))

                if (Build.VERSION.SDK_INT >= 21) {
                    add(ObjectAnimator.ofFloat(ivStarBlue, AnimUtil.CX, AnimUtil.CY, bluePath))
                } else {
                    add(ObjectAnimator.ofFloat(ivStarBlue, AnimUtil.CX, yellowStarOrgPos.x))
                    add(ObjectAnimator.ofFloat(ivStarBlue, AnimUtil.CY, yellowStarOrgPos.y))
                }
                addAll(AnimUtil.scale(ivStarBlue, 1f, 0.5f, 1f))

                duration = 30050

            }.toTypedArray())
        }.start()

    }

    // 进入图书推荐页面的动画
    private fun startStarBallAnim2() {

        // 黄色的球在最外层，使用最黄色球来做动画

        // 首先把黄球挪到蓝球位置
        ivStarYellow.x = ivStarBlue.x
        ivStarYellow.y = ivStarBlue.y
        ivStarYellow.setImageResource(R.drawable.img_user_guide_start2_b)

        val blueStarPos = AnimUtil.positionOf(ivStarYellow)

        val topRight = floatArrayOf(yellowStarOrgPos.x, yellowStarOrgPos.y)
        val bottomRight = floatArrayOf(blueStarPos.x, blueStarPos.y)

        mMatrix.reset()

        // 右移动
        mMatrix.postTranslate(valueX.toFloat(), valueY.toFloat())
        mMatrix.mapPoints(bottomRight)
        mMatrix.mapPoints(topRight)

        // 蓝色星球的轨迹
        val bluePath = Path().apply {
            moveTo(blueStarPos.x, blueStarPos.y)
            cubicTo(bottomRight[0], bottomRight[1], topRight[0], topRight[1], blueStarOrgPos.x, blueStarOrgPos.y)
        }

        // 星球动画
        AnimatorSet().apply {
            playTogether(*mutableListOf<ObjectAnimator>().apply {

                if (Build.VERSION.SDK_INT >= 21) {
                    add(ObjectAnimator.ofFloat(ivStarYellow, AnimUtil.CX, AnimUtil.CY, bluePath))
                } else {
                    add(ObjectAnimator.ofFloat(ivStarYellow, AnimUtil.CX, blueStarOrgPos.x))
                    add(ObjectAnimator.ofFloat(ivStarYellow, AnimUtil.CY, blueStarOrgPos.y))
                }
                addAll(AnimUtil.scale(ivStarYellow, 1f, 1.5f, 2f, 2.8f, 1f))

                duration = 30050

            }.toTypedArray())
        }.start()

    }


    fun show(animStartTime: Long) {
        visibility = View.VISIBLE

        // 渐变显示
        ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f).apply {
            duration = 1000
            startDelay = animStartTime
        }.start()

        flashStarView.startFlash()
        meteorView.start()
    }
}


/*=============================================================================================================================*/
/*=============================================================================================================================*/

// 下面是子界面

/*=============================================================================================================================*/
/*=============================================================================================================================*/


/**
 * 新手引导界面第二页子界面的基类
 */
abstract class UserGuideViewPage2SubPageBase(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {


    var lastSelectLightView: View? = null

    var lastSelectLightShowAnim: ObjectAnimator? = null
    var lastSelectLightHideAnim: ObjectAnimator? = null

    var lastSelectLightFlashAnim: ObjectAnimator? = null


    init {

    }


    /**
     * 选中并点亮一个View
     */
    fun selectLightView(currentSelectLight: View) {

        // 选取消上一个选择
        lastSelectLightFlashAnim?.cancel() // 关闭呼吸动画
        lastSelectLightShowAnim?.cancel() // 关闭上一个显示动画

        val scaleTime = 400L

        var hideAnim: ObjectAnimator? = null
        if (lastSelectLightView != null) { // 上一个选择关闭的时候开启一个消失光晕效果
            hideAnim = AnimUtil.alphaVisibility(lastSelectLightView!!, scaleTime, isShow = false)
        }


        // 选中新的
        lastSelectLightHideAnim?.cancel() // 如果关闭还没执行完的话取消
        lastSelectLightShowAnim = AnimUtil.alphaVisibility(currentSelectLight, scaleTime) // 首次放大

        val animFlash = AnimUtil.flash(currentSelectLight, 2000, scaleTime, 1f, 0.5f) // 放大完了之后开启呼吸
        animFlash.start()


        lastSelectLightFlashAnim = animFlash
        lastSelectLightView = currentSelectLight
        lastSelectLightHideAnim = hideAnim

    }


    fun dp2px(value: Float): Int {
        return DensityUtil.dip2px(context, value)
    }

    open fun show() {


    }

    open fun hide() {

    }


}


/**
 * 性别选择界面
 */
class UserGuideViewPage2SexSelect(context: Context, attrs: AttributeSet? = null) : UserGuideViewPage2SubPageBase(context, attrs) {

    // 男女选择
    private var vgGirl: View
    private var ivGirlLight: View

    private var vgBoy: View
    private var ivBoyLight: View

    private var lottieView: LottieAnimationView

    /**
     * 性别选择监听器
     */
    var onSexSelected: ((sex: Int) -> Unit)? = null


    init {

        LayoutInflater.from(context).inflate(R.layout.view_user_guide_page2_select_sex, this)

        lottieView = findViewById(R.id.lottie_view)

        vgGirl = findViewById<View>(R.id.fl_girl)
        ivGirlLight = findViewById<View>(R.id.iv_girl_light)

        vgBoy = findViewById(R.id.fl_boy)
        ivBoyLight = findViewById(R.id.iv_boy_light)

        vgGirl.setOnClickListener {
            selectSex(ivGirlLight, 1)
        }

        vgBoy.setOnClickListener {
            selectSex(ivBoyLight, 0)
        }

    }


    private fun selectSex(currentSelect: View, sex: Int) {
        // 选取消上一个选择
        selectLightView(currentSelect)

        onSexSelected?.invoke(sex)


    }

    override fun show() {
        // 延迟lottie
        // lottie动画
        runUIThread({
            lottieView.apply {
                imageAssetsFolder = "lotties/userguide/02/images"
                setAnimation("lotties/userguide/02/data.json")
                lottieView.playAnimation()
            }
        }, 1600)

    }


    override fun hide() {
        AnimatorSet().apply {
            val anims = mutableListOf<ValueAnimator>()

            anims.addAll(AnimUtil.translateBy(vgGirl, Pair(-dp2px(200f).toFloat(), null)))
            anims.addAll(AnimUtil.translateBy(vgBoy, Pair(dp2px(200f).toFloat(), null)))
            anims.add(ObjectAnimator.ofFloat(lottieView, View.ALPHA, 0f))

            playTogether(*anims.toTypedArray())

            // 动画结束后隐藏View
            addEndListener {
                visibility = View.INVISIBLE
            }
        }.start()
    }

}


/**
 * 标签选择界面
 */
class UserGuideViewPage2TagSelect(context: Context, attrs: AttributeSet? = null) : UserGuideViewPage2SubPageBase(context, attrs) {

    companion object {
        const val TAG_LOVE = 0
        const val TAG_STORY = 1
    }

    private var vgLove: View
    private var ivLoveLight: View
    private var ivLove: View

    private lateinit var tvLoveSubTag1: TextView
    private lateinit var tvLoveSubTag2: TextView
    private lateinit var tvLoveSubTag3: TextView

    private var vgStory: View
    private var ivStoryLight: View
    private var ivStory: View

    private lateinit var tvStorySubTag1: TextView
    private lateinit var tvStorySubTag2: TextView
    private lateinit var tvStorySubTag3: TextView

    private var lottieView: LottieAnimationView

    var sex: Int = 1 // 0为男，1为女
        set(value) {
            field = value

            setupSubTagBg(tvLoveSubTag1)
            setupSubTagBg(tvLoveSubTag2)
            setupSubTagBg(tvLoveSubTag3)

            setupSubTagBg(tvStorySubTag1)
            setupSubTagBg(tvStorySubTag2)
            setupSubTagBg(tvStorySubTag3)
        }


    private var mLoveSubTags: MutableList<String> = mutableListOf<String>().apply {
        add("都市幻想")
        add("欧美日韩")
        add("古风幻想")
    }

    private var mStorySubTags: MutableList<String> = mutableListOf<String>().apply {
        add("都市2想")
        add("欧美2韩")
        add("古风2想")
    }


    /**
     * 用户选择的一级标签
     */
    private var currentSelectMainTag: Int = -1

    /**
     * 用户选择的二级标签
     */
    private val mCurrentSelectTags: SparseArray<MutableList<String>> = SparseArray<MutableList<String>>().apply {
        put(TAG_LOVE, mutableListOf())
        put(TAG_STORY, mutableListOf())
    }




    /**
     * tab展开监听器
     */
    var onTagExtendListener: (() -> Unit)? = null


    /**
     * 标签选择监听器
     */
    var onSubTagSelectListener: ((mainTag: Int,  subTags: MutableList<String>) -> Unit)?=null


    // 当一个标签展开时候另一个标签向下挪动的距离
    val tranlateY = 200f

    // 子标签距离父标签的距离
    val subTagDistance = 140f

    /**
     * 子标签点击监听器
     */
    private val onSubTagClickListener: OnClickListener = OnClickListener {
        it.isSelected = !it.isSelected

        val selectTags = mCurrentSelectTags[currentSelectMainTag]
        val subTag = it.tag as? String?

        if (it.isSelected) {
            if (!subTag.isNullOrEmpty()) {
                selectTags.add(subTag)
            }
        } else {
            selectTags.remove(subTag)
        }

        notifyTagSelected()

//        when (it) {
//            tvLoveSubTag1-> {
//                if (it.isSelected) {
//                    if (!subTag.isNullOrEmpty()) {
//                        mCurrentSelectTags.add(subTag)
//                    }
//                } else {
//                    mCurrentSelectTags.remove(subTag)
//                }
//            }
//            tvLoveSubTag2-> {
//
//            }
//            tvLoveSubTag3-> {
//
//            }
//
//            tvStorySubTag1-> {
//
//            }
//            tvStorySubTag2-> {
//
//            }
//            tvStorySubTag3-> {
//
//            }
//        }
    }


    init {
        LayoutInflater.from(context).inflate(R.layout.view_user_guide_page2_select_tags, this)

        lottieView = findViewById(R.id.lottie_view)

        vgLove = findViewById<View>(R.id.fl_love)
        ivLoveLight = findViewById<View>(R.id.iv_love_light)
        ivLove = findViewById(R.id.iv_love)

        tvLoveSubTag1 = findViewById(R.id.iv_tag1_sub1)
        tvLoveSubTag2 = findViewById(R.id.iv_tag1_sub2)
        tvLoveSubTag3 = findViewById(R.id.iv_tag1_sub3)

        tvLoveSubTag1.setOnClickListener(onSubTagClickListener)
        tvLoveSubTag2.setOnClickListener(onSubTagClickListener)
        tvLoveSubTag3.setOnClickListener(onSubTagClickListener)


        vgStory = findViewById(R.id.fl_story)
        ivStoryLight = findViewById(R.id.iv_story_light)
        ivStory = findViewById(R.id.iv_story)

        tvStorySubTag1 = vgStory.findViewById(R.id.iv_tag2_sub1)
        tvStorySubTag2 = vgStory.findViewById(R.id.iv_tag2_sub2)
        tvStorySubTag3 = vgStory.findViewById(R.id.iv_tag2_sub3)

        tvStorySubTag1.setOnClickListener(onSubTagClickListener)
        tvStorySubTag2.setOnClickListener(onSubTagClickListener)
        tvStorySubTag3.setOnClickListener(onSubTagClickListener)

        ivLove.setOnClickListener {
            selectTag1()
        }

        ivStory.setOnClickListener {
            selectTag2()
        }

        setupSubTagDatas()
    }

    private fun setupSubTagBg(tvubTag: TextView) {
        when (sex) {
            0-> {
                tvubTag.setBackgroundResource(R.drawable.selector_user_guide_sub_tag_bg_boy)
            }
            else -> {
                tvubTag.setBackgroundResource(R.drawable.selector_user_guide_sub_tag_bg_girl)
            }
        }
    }



    private fun setupSubTagDatas() {
        when (mLoveSubTags.size) {
            0-> {
                tvLoveSubTag1.visibility = View.GONE
                tvLoveSubTag2.visibility = View.GONE
                tvLoveSubTag3.visibility = View.GONE
            }
            1-> {
                tvLoveSubTag1.visibility = View.GONE

                tvLoveSubTag2.text = mLoveSubTags[0]
                tvLoveSubTag2.tag = mLoveSubTags[0]

                tvLoveSubTag3.visibility = View.GONE
            }
            2-> {
                tvLoveSubTag1.text = mLoveSubTags[0]
                tvLoveSubTag2.tag = mLoveSubTags[0]

                tvLoveSubTag2.visibility = View.GONE

                tvLoveSubTag3.text = mLoveSubTags[1]
                tvLoveSubTag3.tag = mLoveSubTags[0]
            }
            else -> {
                tvLoveSubTag1.text = mLoveSubTags[0]
                tvLoveSubTag1.tag = mLoveSubTags[0]

                tvLoveSubTag2.text = mLoveSubTags[1]
                tvLoveSubTag2.tag = mLoveSubTags[1]

                tvLoveSubTag3.text = mLoveSubTags[2]
                tvLoveSubTag3.tag = mLoveSubTags[2]
            }
        }

        when (mStorySubTags.size) {
            0-> {
                tvStorySubTag1.visibility = View.GONE
                tvStorySubTag2.visibility = View.GONE
                tvStorySubTag3.visibility = View.GONE
            }
            1-> {
                tvStorySubTag1.visibility = View.GONE

                tvStorySubTag2.text = mStorySubTags[0]
                tvStorySubTag2.tag = mStorySubTags[0]

                tvStorySubTag3.visibility = View.GONE
            }
            2-> {
                tvStorySubTag1.text = mStorySubTags[0]
                tvStorySubTag1.tag = mStorySubTags[0]


                tvStorySubTag2.visibility = View.GONE


                tvStorySubTag3.text = mStorySubTags[1]
                tvStorySubTag3.tag = mStorySubTags[1]
            }
            else -> {
                tvStorySubTag1.text = mStorySubTags[0]
                tvStorySubTag1.tag = mStorySubTags[0]

                tvStorySubTag2.text = mStorySubTags[1]
                tvStorySubTag2.tag = mStorySubTags[1]

                tvStorySubTag3.text = mStorySubTags[2]
                tvStorySubTag3.tag = mStorySubTags[2]
            }
        }
    }

    override fun show() {
        visibility = View.VISIBLE
        vgLove.scaleX = 0f
        vgLove.scaleY = 0f
        vgStory.scaleX = 0f
        vgStory.scaleY = 0f
        AnimUtil.scaleVisibility(vgLove)
        AnimUtil.scaleVisibility(vgStory)


        // lottie动画
        runUIThread({
            lottieView.apply {
                imageAssetsFolder = "lotties/userguide/03/images"
                setAnimation("lotties/userguide/03/data.json")
                lottieView.playAnimation()
            }
        }, 300)

    }

    override fun hide() {

        // 先收子tag
        AnimatorSet().apply {
            playTogether(*mutableListOf<ObjectAnimator>().apply {

                addAll(subTagAnim(tvLoveSubTag1, Pair(null, 0f)))
                addAll(subTagAnim(tvLoveSubTag2, Pair(0f, 0f)))
                addAll(subTagAnim(tvLoveSubTag3, Pair(0f, null)))

                addAll(subTagAnim(tvStorySubTag1, Pair(null, 0f)))
                addAll(subTagAnim(tvStorySubTag2, Pair(0f, 0f)))
                addAll(subTagAnim(tvStorySubTag3, Pair(0f, null)))

            }.toTypedArray())
            duration = 300
        }.start()

        // 再挪出去
        AnimatorSet().apply {
            playTogether(*mutableListOf<ObjectAnimator>().apply {
                addAll(AnimUtil.translateBy(vgLove, Pair(dp2px(-200f).toFloat(), null)))
                addAll(AnimUtil.translateBy(vgStory, Pair(dp2px(200f).toFloat(), null)))


                // 隐藏lottie
                ObjectAnimator.ofFloat(lottieView, View.ALPHA, 0f)
            }.toTypedArray())

            duration = 300
            startDelay = 300 // 等子tag收回来之后才挪出去

        }.start()

    }


    /**
     * sub tag使用特定的插值器
     */
    private fun subTagAnim(v: View, vararg pair: Pair<Float?, Float?>): List<ObjectAnimator> {
        val p2 = mutableListOf<Pair<Float?, Float?>>()
        pair.map {
            p2.add(Pair(if (it.first != null) v.x + it.first!! else null, if (it.second != null) v.y + it.second!! else null))
        }

        val anims = AnimUtil.translateBy(v, *pair)
        anims.map {
            //            it.interpolator = OvershootInterpolator()
        }
        return anims
    }

    private fun selectTag1() {

        // 点亮tag1
        selectLightView(ivLoveLight)

        AnimatorSet().apply {
            playTogether(*mutableListOf<ObjectAnimator>().apply {

                // 把当前位置挪回来
                add(ObjectAnimator.ofFloat(vgLove, View.TRANSLATION_Y, 0f))

                // 把子tab展开


                addAll(subTagAnim(tvLoveSubTag1, Pair(null, -dp2px(subTagDistance).toFloat())))

                val tag2D = subTagDistance * Math.sin(45.0).toFloat()
                addAll(subTagAnim(tvLoveSubTag2, Pair(dp2px(tag2D).toFloat(), -dp2px(tag2D).toFloat())))

                addAll(subTagAnim(tvLoveSubTag3, Pair(dp2px(subTagDistance).toFloat(), null)))


                // 把tag2挪开，并把子tag收起来
                add(ObjectAnimator.ofFloat(vgStory, View.TRANSLATION_Y, dp2px(tranlateY).toFloat()))

                addAll(subTagAnim(tvStorySubTag1, Pair(null, 0f)))
                addAll(subTagAnim(tvStorySubTag2, Pair(0f, 0f)))
                addAll(subTagAnim(tvStorySubTag3, Pair(0f, null)))

                add(ObjectAnimator.ofFloat(lottieView, View.TRANSLATION_Y, -dp2px(300f).toFloat()))

            }.toTypedArray())

            duration = 300


        }.start()

        currentSelectMainTag = TAG_LOVE

        notifyOutExtend()
        notifyTagSelected()
    }

    private fun selectTag2() {

        // 点亮tag2
        selectLightView(ivStoryLight)


        AnimatorSet().apply {
            playTogether(*mutableListOf<ObjectAnimator>().apply {

                // 把当前位置挪回来
                add(ObjectAnimator.ofFloat(vgStory, View.TRANSLATION_Y, 0f))

                // 把子tab展开
                addAll(subTagAnim(tvStorySubTag1, Pair(null, -dp2px(subTagDistance).toFloat())))

                val tag2D = subTagDistance * Math.sin(45.0).toFloat()
                addAll(subTagAnim(tvStorySubTag2, Pair(-dp2px(tag2D).toFloat(), -dp2px(tag2D).toFloat())))

                addAll(subTagAnim(tvStorySubTag3, Pair(-dp2px(subTagDistance).toFloat(), null)))


                // 把tag1挪开，并把子tag收起来
                add(ObjectAnimator.ofFloat(vgLove, View.TRANSLATION_Y, dp2px(tranlateY).toFloat()))

                addAll(subTagAnim(tvLoveSubTag1, Pair(null, 0f)))
                addAll(subTagAnim(tvLoveSubTag2, Pair(0f, 0f)))
                addAll(subTagAnim(tvLoveSubTag3, Pair(0f, null)))

                add(ObjectAnimator.ofFloat(lottieView, View.TRANSLATION_Y, -dp2px(300f).toFloat()))


            }.toTypedArray())

            duration = 300

        }.start()

        currentSelectMainTag = TAG_STORY

        notifyOutExtend()
        notifyTagSelected()
    }

    private fun notifyTagSelected() {
        if (currentSelectMainTag != -1) {
            val tags = mCurrentSelectTags[currentSelectMainTag]
            onSubTagSelectListener?.invoke(currentSelectMainTag, tags)
        }
    }

    private fun notifyOutExtend() {
        onTagExtendListener?.invoke()
    }


}


/**
 * 书籍推荐页
 */
class UserGuideViewPage2RecommendBook(context: Context, attrs: AttributeSet? = null) : UserGuideViewPage2SubPageBase(context, attrs) {

    val ivBook1: View
    val ivBook2: View

    private var lottieView: LottieAnimationView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_user_guide_page2_book_recomend, this)


        lottieView = findViewById(R.id.lottie_view)

        ivBook1 = findViewById(R.id.iv_book1)
        ivBook2 = findViewById(R.id.iv_book2)


    }

    override fun show() {
        visibility = View.VISIBLE

        // 第一本书
        AnimatorSet().apply {
            playTogether(*mutableListOf<ObjectAnimator>().apply {


                // 把tag1挪开，并把子tag收起来
                add(ObjectAnimator.ofFloat(ivBook1, View.TRANSLATION_Y, dp2px(200f).toFloat(), 0f))
                add(ObjectAnimator.ofFloat(ivBook1, View.ALPHA, 0f, 1f))


            }.toTypedArray())

            startDelay = 300

        }.start()


        AnimatorSet().apply {
            playTogether(*mutableListOf<ObjectAnimator>().apply {
                // 把tag1挪开，并把子tag收起来
                add(ObjectAnimator.ofFloat(ivBook2, View.TRANSLATION_Y, dp2px(200f).toFloat(), 0f))
                add(ObjectAnimator.ofFloat(ivBook2, View.ALPHA, 0f, 1f))

            }.toTypedArray())

            startDelay = 500
        }.start()


        // lottie动画
        runUIThread({
            lottieView.apply {
                imageAssetsFolder = "lotties/userguide/04/images"
                setAnimation("lotties/userguide/04/data.json")
                lottieView.playAnimation()
            }
        }, 300)



    }
}
