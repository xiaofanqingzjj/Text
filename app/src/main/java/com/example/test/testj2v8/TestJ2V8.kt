package com.example.test.testj2v8

import android.os.Bundle
import android.util.Log
import android.view.View
import com.bedrock.module_base.MenuFragment
import com.eclipsesource.v8.V8





class TestJ2V8 : MenuFragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMenu("Test JS hello world") {
            val runtime = V8.createV8Runtime()
            val result = runtime.executeIntegerScript("""
    var hello = 'hello, ';
    var world = 'world!';
    hello.concat(world).length;
    
    """.trimIndent())
            Log.e("Test", "JS result = $result")
            runtime.release()
        }
    }
}