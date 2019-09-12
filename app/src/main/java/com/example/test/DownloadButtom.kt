package com.example.test

import android.content.Context
import android.graphics.*
import android.util.*
import android.view.View


/**
 * 一个带下载进度条的下载按钮
 *
 * 文字在进度条的左边和右边支持不同颜色
 *
 * @author fortunexiao
 */
class DownloadButtom(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {


    companion object {

        const val TAG = "DownloadButtom"

        const val STATE_NORMAL = 0
        const val STATE_DOWNLOADING = 1
        const val STATE_PAUSED = 2


        private fun dp2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }


        /**
         * 定义一个动画属性
         */
        val PROGRESS: Property<DownloadButtom, Float> = object : Property<DownloadButtom, Float>(Float::class.java, "progress") {

            override fun set(`object`: DownloadButtom?, value: Float) {
                `object`?.setProgress(value)
            }


            override fun get(`object`: DownloadButtom?): Float {
                return `object`?.mProgress ?: 0f
            }
        }
    }


    private var mState: Int = 0

    // 外部可以设置的属性
    var mTextSize: Float = 0f
    var mProgressBgColor: Int = 0
    var mProgressColor: Int = 0

    private var mProgress: Float = 5f


    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mContentRect: Rect = Rect()

    private var mBgRect: RectF = RectF()
    private var mProgressRect: RectF = RectF()
    private var mLeftProgressRect: RectF = RectF()

    private var cornerRadius: Float = 0f

    private val mPath = Path()

    init {
        mState = STATE_DOWNLOADING

        mProgressColor = Color.BLUE
        mProgressBgColor = Color.CYAN

        cornerRadius = dp2px(context, 20f).toFloat()
        mTextSize = dp2px(context, 14f).toFloat()


        if (attrs != null) {

        }


        mPaint.run {

        }


        mTextPaint.run {
            textSize = mTextSize
        }
    }


    /**
     * 设置进度
     */
    fun setProgress(progress: Float) {
        mProgress = when {
            progress < 0 -> 0f
            progress > 100 -> 100f
            else -> progress
        }
        invalidate()
    }

    /**
     * 设置状态
     */
    fun setState(state:Int) {
        mState = state
        invalidate()
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
            mTextPaint.color = mProgressBgColor
            drawText(canvas, "下载", mTextPaint)
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
            canvas.drawRoundRect(mBgRect, cornerRadius, cornerRadius, mPaint)
            canvas.restore()

            canvas.restore()

            val text = if (mState == STATE_DOWNLOADING) "${String.format("%.1f", mProgress)}%" else "继续"

            // 进度条左边的颜色
            canvas.save()
            canvas.clipRect(mProgressRect)
            mTextPaint.color = mProgressBgColor
            drawText(canvas, text, mTextPaint)
            canvas.restore()

            // 进度条右边的文字颜色
            canvas.save()
            canvas.clipRect(mLeftProgressRect)
            mTextPaint.color = mProgressColor
            drawText(canvas, text, mTextPaint)

            canvas.restore()
        }
    }

    /**
     * 在View的中心绘制文字
     */
    private fun drawText(canvas: Canvas, text: String, paint: Paint) {
        paint.textAlign = Paint.Align.CENTER
        val x = mContentRect.width() / 2
        val lineHeight = paint.descent() - paint.ascent()
        val y = (mContentRect.height() - lineHeight) / 2 - paint.ascent()
        canvas.drawText(text, x.toFloat(), y, paint)
    }

}