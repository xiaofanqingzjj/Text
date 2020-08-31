package com.example.test.testattrs

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.FragmentActivity
import com.example.test.R
import kotlinx.android.synthetic.main.activity_my_theme.*


/**
 * android使用主题样式相关的测试代码
 *
 */
class MyThemeActivity: FragmentActivity() {

    companion object {
        const val TAG = "MyThemeActivity##"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_theme)


        val theme = theme

        val attributes = theme.allAttributes()


        Log.d(TAG, "all all:$attributes")

        themeInfo()


//        val a = theme.obtainStyledAttributes(R.styleable.MyThemeAttrs)
//        val titleColor = a.getColor(R.styleable.MyThemeAttrs_titleColor, Color.BLUE)

//        text.background = getThemeDrawableAttribute(R.attr.colorPrimary)

//        text.background = getThemeDrawableAttribute(R.attr.titleColor)

//        text.setTextColor(getThemeColorAttribute(R.attr.titleColor))
        text.setTextColor(getThemeColorStateAttribute(R.attr.titleColor))
//        a.recycle()


        val colorStateList: ColorStateList



        resources

        setTheme(R.style.ActivityAnim)
    }

    private fun themeInfo() {
        val theme = theme
        theme.dump(1, TAG, "b")

//        theme.getAttributeResolutionStack()
    }
}


