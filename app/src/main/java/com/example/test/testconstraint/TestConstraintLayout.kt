package com.example.test.testconstraint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bedrock.module_base.util.DensityUtil
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_constraint_layout.*

class TestConstraintLayout : Fragment() {

    init {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_constraint_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        constraint_layout.maxHeight = DensityUtil.dip2px(context!!, 300f)

    }
}