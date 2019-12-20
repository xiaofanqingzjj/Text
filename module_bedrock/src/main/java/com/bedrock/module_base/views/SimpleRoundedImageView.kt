package com.bedrock.module_base.views


import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import com.bedrock.module_base.R
import com.bedrock.module_base.util.DensityUtil


/**
 * 一个简单的支持圆角的图片组件
 *
 * @author fortune
 */
class SimpleRoundedImageView(context: Context, attrs: AttributeSet? = null) : AppCompatImageView(context, attrs) {

    companion object {
        const val TAG = "SimpleRoundedImageView"
    }

    private val cornerRadiuses = FloatArray(4)

    private var hasCorner = false

//    private var

    var cornerRadius: Int = 0
        set(value) {
            field = value
            applyCorner()
        }

    init {
        attrs?.run {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SimpleRoundedImageView)
            cornerRadius = a.getDimensionPixelSize(R.styleable.SimpleRoundedImageView_sri_corner_radius, DensityUtil.dip2px(context, 8f))

            val radius = FloatArray(4)
            radius[0] = a.getDimensionPixelSize(R.styleable.SimpleRoundedImageView_sri_corner_radius_top_left, 0).toFloat()
            radius[1] = a.getDimensionPixelSize(R.styleable.SimpleRoundedImageView_sri_corner_radius_top_right, 0).toFloat()
            radius[2] = a.getDimensionPixelSize(R.styleable.SimpleRoundedImageView_sri_corner_radius_bottom_right, 0).toFloat()
            radius[3] = a.getDimensionPixelSize(R.styleable.SimpleRoundedImageView_sri_corner_radius_bottom_left, 0).toFloat()

            a.recycle()

//            var hasCorner = false
            radius.forEach {
                if (it != 0f) {
                    hasCorner = true
                }
            }

        }
        clipToOutline = true
    }

    private fun applyCorner() {
        if (cornerRadius != 0) {
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    view?.run {
                        if (cornerRadius != 0) {
                            outline?.setRoundRect(0, 0, width, height, cornerRadius.toFloat())
                        }
                    }
                }
            }

        }
    }

    override fun draw(canvas: Canvas?) {

        val outline = Outline()
        outlineProvider?.getOutline(this, outline)
        if (!outline.canClip()) { // 如果out不能裁剪，则自己来裁剪

        }

        super.draw(canvas)

    }

//    override fun setImageDrawable(drawable: Drawable?) {
//        super.setImageDrawable(drawable)
//        Log.i(TAG, "setImageDrawable: $this,  $drawable")
//    }
//
//    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
//        Log.w(TAG, "setFrame: $this, $l,$t,$r,$b")
//        return super.setFrame(l, t, r, b)
//    }
//
//    override fun invalidateDrawable(dr: Drawable) {
//        super.invalidateDrawable(dr)
//    }
//
//    override fun setImageMatrix(matrix: Matrix?) {
//        super.setImageMatrix(matrix)
//    }


}

