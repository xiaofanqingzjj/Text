package com.example.test.testconstraint

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.test.R


/**
 *
 */
class SupportMaxHeightConstraintLayout(context: Context, attributeSet: AttributeSet? = null) : ConstraintLayout(context, attributeSet) {

//    private var maxHeight = 0

    init {

        val t = context.obtainStyledAttributes(attributeSet, R.styleable.SupportMaxHeightConstraintLayout)

        maxHeight = t.getDimensionPixelSize(R.styleable.SupportMaxHeightConstraintLayout_mcl_maxHeight, 0)

        t.recycle()

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {



        super.onMeasure(widthMeasureSpec, heightMeasureSpec)


    }

}