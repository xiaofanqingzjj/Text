package com.example.test

import android.os.Bundle
import android.transition.Slide
import com.bedrock.module_base.FragmentContainerActivity
import com.bedrock.module_base.MenuActivity
import com.example.test.transition.TestTransitionActivity
import com.example.test.card.CardViewActivity


class MainActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenu("过度动画", TestTransitionActivity::class.java)

        addMenu("CardVIEW", CardViewActivity::class.java)

        addMenu("Lottie") {
            FragmentContainerActivity.show(this@MainActivity, "Test Lottie", TestLottie::class.java)
        }


    }

    private fun setupWindowAnimations() {
        window.exitTransition = Slide()
    }

}
