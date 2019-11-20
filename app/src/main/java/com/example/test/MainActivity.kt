package com.example.test

import android.os.Bundle
import android.transition.Slide
import com.bedrock.module_base.MenuActivity
import com.example.test.transition.TestTransitionActivity
import com.example.test.card.CardViewActivity
import com.example.test.movementmethod.TestMoveMethod
import com.example.test.testlottie.TestLottieFragment


class MainActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenu("过度动画", TestTransitionActivity::class.java)

        addMenu("CardVIEW", CardViewActivity::class.java)

        addMenuByFragment("Lottie", TestLottieFragment::class.java)

        addMenuByFragment("Test MovementMethod", TestMoveMethod::class.java)
    }

}
