package com.example.test

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import com.bedrock.module_base.MenuActivity
import com.example.test.alarm.TestAlarm
import com.example.test.transition.TestTransitionActivity
import com.example.test.card.CardViewActivity
import com.example.test.expendtextview.TextExpendTextView
import com.example.test.install.InstallApkSessionApi
import com.example.test.movementmethod.TestMoveMethod
import com.example.test.testattrs.MyThemeActivity
import com.example.test.testattrs.TestAttributes
import com.example.test.testlottie.TestLottieFragment


class MainActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenu("过度动画", TestTransitionActivity::class.java)

        addMenu("CardVIEW", CardViewActivity::class.java)

        addMenuByFragment("Lottie", TestLottieFragment::class.java)

        addMenuByFragment("Test MovementMethod", TestMoveMethod::class.java)

        addMenu("Install") {
            startActivity(Intent(this@MainActivity, InstallApkSessionApi::class.java).apply {

            })
        }

        addMenuByFragment("Btn bg", BtnBg::class.java)

        addMenuByFragment("Alarm", TestAlarm::class.java)

        addMenuByFragment("ExpendTextView", TextExpendTextView::class.java)

        addMenuByFragment("TestViewPager", TestViewPager::class.java)

        addMenuByFragment("TestAttrs", TestAttributes::class.java)

        addMenu("TestTheme", MyThemeActivity::class.java)


        setupWindowAnimations()
    }


    private fun setupWindowAnimations() {
        window.enterTransition = Fade()
        window.exitTransition = Fade()
    }
}
