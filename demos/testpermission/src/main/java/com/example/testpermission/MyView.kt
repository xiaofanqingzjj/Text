package com.example.testpermission

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class MyView(context: Context, attributeSet: AttributeSet? = null) : View(context, attributeSet) {


    val ortPos = Point(DensityUtil.dip2px(context, 200f), DensityUtil.dip2px(context, 200f))

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var mPathControllers : MutableList<AnimPathHelper.PathControll>


    init {

        paint.color = Color.RED
        paint.style = Paint.Style.STROKE

        mPathControllers = AnimPathHelper.buildPaths(context, ortPos)

    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawPoint(ortPos.x, ortPos.y, canvas)

        mPathControllers.map {

            val path = Path()
            path.moveTo(ortPos.x.toFloat(), ortPos.y.toFloat())

            path.quadTo(it.controlX.toFloat(), it.controlY.toFloat(), it.destX.toFloat(), it.destY.toFloat())

            canvas?.drawPath(path, paint)


            drawPoint(it.destX, it.destY, canvas)

        }

    }


    private fun drawPoint(x: Int, y: Int, canvas: Canvas?){
        canvas?.drawCircle(x.toFloat(), y.toFloat(), 10f, paint)
    }

}