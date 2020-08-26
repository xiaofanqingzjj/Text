package com.example.test.image

import android.os.Bundle
import android.view.View
import com.bedrock.module_base.SimpleFragment
import com.bumptech.glide.Glide
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_test_images.*


class TestImages : SimpleFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_test_images)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(context!!).asDrawable().into(iv1)
    }

}