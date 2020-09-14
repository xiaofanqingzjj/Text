package com.example.testpermission

import android.content.Context
import android.graphics.*
import kotlin.random.Random

object AnimPathHelper {



    private fun dp2px(context: Context, dp: Int): Int {
        return DensityUtil.dp2px(context, dp.toFloat())
    }

    fun buildPaths(context: Context, orgPos: Point, count: Int = 10, length: Int = dp2px(context, 200)): MutableList<PathControll> {

        val paths = mutableListOf<PathControll>()

        for (i in 1 .. count) {

            val avg = 180 / count

            val angle = Random.nextInt(avg * i - avg , avg * i) // 角度在小范围内随机
            val radius =  Random.nextInt(length - dp2px(context,10), length + dp2px(context, 10)) // 半径在小范围内随机
            paths.add(createPath(context, orgPos, angle, radius))
        }
        return paths

    }


    private fun createPath(context: Context, orgPos: Point, angle: Int, length: Int): PathControll {

        val matrix = Matrix()

        val orgPoint = FloatArray(2).apply {
            this[0] = orgPos.x.toFloat()
            this[1] = orgPos.y.toFloat()
        }

        val tempPoint = FloatArray(2)

        val sideAngle = if (angle > 90) {
            180 - angle
        } else angle

        val length = length + dp2px(context, sideAngle)


        matrix.setTranslate(length.toFloat(), 0f)
        matrix.postRotate(360 - angle.toFloat(), orgPoint[0], orgPoint[1])

        matrix.mapPoints(tempPoint, orgPoint)

        return PathControll().apply {
            destX = tempPoint[0].toInt()
            destY = tempPoint[1].toInt()

            controlX = orgPos.x
            controlY = orgPos.y - Random.nextInt(length - dp2px(context,20), length)
        }
    }



    class PathControll {
        var destX: Int = 0
        var destY: Int = 0

        var controlX: Int = 0
        var controlY: Int = 0
    }

}