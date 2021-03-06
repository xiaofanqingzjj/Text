package com.example.test

import android.os.Bundle
import android.view.View
import com.bedrock.module_base.SimpleFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_test_gif.*

class TestGif : SimpleFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_test_gif)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        Glide.with(context!!)
                .asDrawable()
                .load("https://cdn.story.qq.com/images/gameicon/minigame_cat.gif")
                .into(iv)
    }

}