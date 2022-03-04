package com.example.opengl

import android.app.Activity
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.ImageView

class AnimActivity : Activity() {

    lateinit var d : AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_anim)

        val iv = findViewById<ImageView>(R.id.iv)
        d = iv.drawable as AnimationDrawable

    }

    override fun onResume() {
        super.onResume()
        d.start()
    }

    override fun onPause() {
        super.onPause()
        d.stop()
    }
}