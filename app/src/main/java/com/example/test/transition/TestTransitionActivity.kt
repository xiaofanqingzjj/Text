package com.example.test.transition

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.transition.Scene
import android.transition.TransitionManager
import com.example.test.R
import kotlinx.android.synthetic.main.activity_test_transition.*

class TestTransitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test_transition)

        btn_test.setOnClickListener {

            val scene = Scene(root, image_view)
            scene.enter()

//            TransitionManager.go(scene)
        }
    }
}