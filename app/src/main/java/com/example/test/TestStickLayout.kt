package com.example.test

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.bedrock.module_base.SimpleFragment
import com.bedrock.module_base.recycleradapter.QuickAdapter
import com.bedrock.module_base.recycleradapter.quickAdapter
import kotlinx.android.synthetic.main.fragment_test_center_drawable.*
import kotlinx.android.synthetic.main.fragment_test_sticky_layout.*

class TestStickLayout : SimpleFragment() {

    companion object {
        const val TAG = "TestDrawerLayout"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_test_sticky_layout)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        rv.quickAdapter(
                data = mutableListOf<String>().apply {
                    repeat(30) {
                        add("Item")
                    }
                },
                itemLayoutId = android.R.layout.simple_list_item_1,
                bindData = {position, data, itemView ->
                    itemView?.run {
                        findViewById<TextView>(android.R.id.text1).text = "$position, $data"
                    }
                }
        )

    }


}


