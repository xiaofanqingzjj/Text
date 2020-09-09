package com.example.test

import android.os.Bundle
import android.view.View
import com.bedrock.module_base.SimpleFragment
import com.tencent.mars.xlog.Log
import kotlinx.android.synthetic.main.fragment_test_xlog.*


class TestXLog : SimpleFragment() {

    companion object {
        const val TAG = "TestXLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_test_xlog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_log.setOnClickListener {
            Log.e(TAG, "TESTlOG")
        }

    }

}