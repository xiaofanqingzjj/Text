package com.example.test.card

import android.graphics.Outline
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R
import kotlinx.android.synthetic.main.activity_card_view.*

class CardViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_card_view)

        mcv.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {

                val rectF = RectF(0f, 0f, 100f, 200f)
                val path = Path()
                path.addRoundRect(rectF, 10f, 10f, Path.Direction.CW)

                outline?.setConvexPath(path)

                val rect = rectF.run {
                    Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                }

//                outline?.setRect(rect)

                outline?.setOval(rect)

            }

        }
    }
}