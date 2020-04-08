package com.example.test

import android.os.Bundle
import android.util.Log
import android.view.View
import com.bedrock.module_base.SimpleFragment
import kotlinx.android.synthetic.main.fragment_test_focustable.*

class TestFocusableInput : SimpleFragment() {

    companion object {
        const val TAG = "TestFocusableInput"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.fragment_test_focustable)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setOnFocusChangeListener { v, hasFocus ->

            Log.d(TAG, "onFocusChange:$v, hasFocus:$hasFocus")
        }

        et1.setOnFocusChangeListener { v, hasFocus ->
            Log.d(TAG, "et1 onFocusChange:$v, hasFocus:$hasFocus")
        }
    }
}