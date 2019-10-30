package com.example.test

import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import com.example.module_base.MenuActivity
import com.example.test.transition.TestTransitionActivity
import android.transition.TransitionInflater



class MainActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        addMenu("过度动画") {
            startActivity(Intent(this@MainActivity, TestTransitionActivity::class.java))
        }

        setupWindowAnimations()
    }

    private fun setupWindowAnimations() {
        window.exitTransition = Slide()
    }

}
