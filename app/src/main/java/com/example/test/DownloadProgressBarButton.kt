package com.example.test

import android.content.Context
import android.graphics.*
import android.text.*
import android.text.style.TextAppearanceSpan
import android.util.*
import android.view.View
import com.fortunexiao.tktx.platforms.dp2px


/**
 * 一个带下载进度条的下载按钮
 *
 * 文字在进度条的左边和右边支持不同颜色
 *
 * @author fortunexiao
 */
class DownloadProgressBarButton  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {


    companion object {

        const val TAG = "DownloadProgressBarButton"

        const val STATE_NORMAL = 0
        const val STATE_DOWNLOADING = 1
        const val STATE_PAUSED = 2
        const val STATE_PENDING = 3


        private fun dp2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }




        /**
         * 定义一个动画属性
         */
        val PROGRESS: Property<DownloadProgressBarButton, Float> = object : Property<DownloadProgressBarButton, Float>(Float::class.java, "progress") {

            override fun set(`object`: DownloadProgressBarButton?, value: Float) {
                `object`?.mProgress = value
            }


            override fun get(`object`: DownloadProgressBarButton?): Float {
                return `object`?.mProgress ?: 0f
            }
        }
    }


    var mNormalText: CharSequence = "下载"
        set(value) {
            field = value
            invalidate()
        }


    /**
     * 当前的状态
     */
    var mState: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    // 外部可以设置的属性
    var mTextSize: Float = 0f

    // 文字颜色
    var mTextProgressColor: Int = 0
    var mTextLeftProgressColor: Int = 0

    // 背景色

    // 正常背景色
    var mNormalBgColor: Int = 0

    // 进度条样式的背景
    var mProgressBgColor: Int = 0

    // 进度条颜色
    var mProgressColor: Int = 0

    var mRightBorderBend: Boolean = false

    /**
     * 当前进度
     */
    var mProgress: Float = 0f
        set(value) {
            Log.d(TAG, "setProgress:$value")
            field = when {
                value < 0 -> 0f
                value > 100 -> 100f
                else -> value
            }
            invalidate()
        }


    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private var mContentRect: Rect = Rect()

    private var mBgRect: RectF = RectF()
    private var mProgressRect: RectF = RectF()
    private var mLeftProgressRect: RectF = RectF()

    /**
     * 设置圆角
     */
    var cornerRadius: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val mPath = Path()

    init {
        mState = STATE_NORMAL

        mProgressColor = Color.BLUE
        mProgressBgColor = Color.CYAN
        mTextProgressColor = Color.CYAN
        mTextLeftProgressColor = Color.BLUE

        cornerRadius = dp2px(context, 20f).toFloat()
        mTextSize = dp2px(context, 14f).toFloat()


        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressBarButton)
            mTextSize = a.getDimensionPixelOffset(R.styleable.DownloadProgressBarButton_dpbb_buttonTextSize, mTextSize.toInt()).toFloat()
            cornerRadius = a.getDimension(R.styleable.DownloadProgressBarButton_dpbb_cornerRadius, cornerRadius)
            mProgressColor = a.getColor(R.styleable.DownloadProgressBarButton_dpbb_progressColor, mProgressColor)
            mProgressBgColor = a.getColor(R.styleable.DownloadProgressBarButton_dpbb_progressBgColor, mProgressBgColor)

            mTextProgressColor = a.getColor(R.styleable.DownloadProgressBarButton_dpbb_progressTextColor, mTextProgressColor)
            mTextLeftProgressColor = a.getColor(R.styleable.DownloadProgressBarButton_dpbb_leftProgressTextColor, mTextLeftProgressColor)

            mRightBorderBend = a.getBoolean(R.styleable.DownloadProgressBarButton_dpbb_right_border_bend, false)
            a.recycle()
        }


        mPaint.run {

        }


        mTextPaint.run {
            textSize = mTextSize
        }
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)


        // 如果不指定高宽，默认大小为220x40
        var measureWidth = widthSize
        var measureHeight = heightSize
        if (widthMode != MeasureSpec.EXACTLY) {
            measureWidth = dp2px(context, 220f)
        }
        if (heightMode !=  MeasureSpec.EXACTLY) {
            measureHeight = dp2px(context, 40f)
        }

        setMeasuredDimension(measureWidth, measureHeight)
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 初始化几个Rect
        mContentRect.set(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)
        mBgRect.set(mContentRect)
        mProgressRect.set(mContentRect)
        mLeftProgressRect.set(mContentRect)

        if (mState == STATE_NORMAL) {
            // 绘制正常的背景色
            mPaint.color = mProgressBgColor
            canvas.drawRoundRect(mBgRect, cornerRadius, cornerRadius, mPaint)

            // 绘制文字
            mTextPaint.color = mTextLeftProgressColor
            drawText(canvas, mNormalText, mTextPaint)
        } else {
            val progressRectWidth = mContentRect.width() * mProgress / 100
            mProgressRect.right = progressRectWidth
            mLeftProgressRect.left = progressRectWidth

            // 绘制进度条背景
            mPaint.color = mProgressBgColor
            canvas.drawRoundRect(mBgRect, cornerRadius, cornerRadius, mPaint)

            // 绘制进度条
            canvas.save()
            mPath.reset()
            mPath.addRoundRect(mBgRect, floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius), Path.Direction.CCW)
            canvas.clipPath(mPath)

            mPaint.color = mProgressColor

            // 进度条要使用裁剪和平移的方式来实现，否则
            canvas.save()
            val transX = - ((100f - mProgress) / 100f * mContentRect.width().toFloat())
            canvas.translate(transX, 0f)

            if (mRightBorderBend) {
                canvas.drawRoundRect(mBgRect, cornerRadius, cornerRadius, mPaint)
            } else {
                canvas.drawRoundRect(mBgRect, 0f, 0f, mPaint)
            }

            canvas.restore()
            canvas.restore()

            val text = when (mState) {
                STATE_DOWNLOADING -> SpannableStringBuilder().apply {
                    append("下载中")
                    append("(${String.format("%.1f", mProgress)}%)", textSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                STATE_PENDING -> "请稍候..."
                else -> "继续"
            }

            // 进度条左边的颜色
            canvas.save()
            canvas.clipRect(mProgressRect)
            mTextPaint.color = mTextProgressColor
            drawText(canvas, text, mTextPaint)
            canvas.restore()

            // 进度条右边的文字颜色
            canvas.save()
            canvas.clipRect(mLeftProgressRect)
            mTextPaint.color = mTextLeftProgressColor
            drawText(canvas, text, mTextPaint)

            canvas.restore()
        }
    }

    val textSpan = TextAppearanceSpan(null, 0, (mTextSize - 3.dp2px(context)).toInt(), null, null )

    /**
     * 在View的中心绘制文字
     */
    private fun drawText(canvas: Canvas, text: CharSequence, paint: TextPaint) {

        canvas.save()
//
//        val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width).apply {
//            setMaxLines(1)
//        }.build()

//        val layout = BoringLayout.make(text,
//                paint,
//                width,
//                Layout.Alignment.ALIGN_CENTER,
//                0f,
//                0f, paint.fontMetrics, false)
        val layout = StaticLayout(text, paint, width, Layout.Alignment.ALIGN_CENTER, 0f, 0f, false)

        // 垂直居中
        canvas.translate(0f, (height - layout.height)  / 2f)

        layout.draw(canvas)

        canvas.restore()

//        paint.textAlign = Paint.Align.CENTER
//        val x = mContentRect.width() / 2
//        val lineHeight = paint.descent() - paint.ascent()
//        val y = (mContentRect.height() - lineHeight) / 2 - paint.ascent()
//        canvas.drawText(text, x.toFloat(), y, paint)
    }

}