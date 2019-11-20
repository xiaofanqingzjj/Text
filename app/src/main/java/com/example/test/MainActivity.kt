package com.example.test

import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import com.example.module_base.MenuActivity
import com.example.test.transition.TestTransitionActivity
import android.transition.TransitionInflater
import androidx.cardview.widget.CardView
import com.example.module_base.FragmentContainerActivity
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
