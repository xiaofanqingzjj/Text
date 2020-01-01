package com.example.test.slidedrawer

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.bedrock.module_base.SimpleBaseListFragment
import com.bedrock.module_base.recycleradapter.quickAdapter

class SomeListFragment : SimpleBaseListFragment() {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = mutableListOf<String>()

        for (i in 0..100) {
            data.add("data")
        }


        mListView.quickAdapter(data, android.R.layout.simple_list_item_1) {pos, data, itemView ->
            itemView?.findViewById<TextView>(android.R.id.text1)?.text = data
        }
    }
}