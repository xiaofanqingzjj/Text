package com.example.test.movementmethod

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bedrock.module_base.SimpleBaseListFragment
import com.bedrock.module_base.recycleradapter.quickAdapter
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_test_movementmethod.*


class TestMoveMethod : SimpleBaseListFragment() {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = mutableListOf<String>()

        data.add("a")
        data.add("a")
        data.add("a")
        data.add("a")

        mListView.quickAdapter(
            data =  data,
            itemLayoutId = R.layout.fragment_test_movementmethod
        ) {data, itemView ->
            val textView = itemView?.findViewById<TextView>(R.id.textview)
            textView?.setTextIsSelectable(true)
        }
    }
}