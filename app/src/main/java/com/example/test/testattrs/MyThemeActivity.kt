package com.example.test.testattrs

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.FragmentActivity
import com.example.test.R
import kotlinx.android.synthetic.main.activity_my_theme.*

class MyThemeActivity: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_theme)

        val a = obtainStyledAttributes(R.styleable.MyThemeAttrs)
        val titleColor = a.getColor(R.styleable.MyThemeAttrs_titleColor, Color.BLUE)
        text.setTextColor(titleColor)
        a.recycle()
    }
}