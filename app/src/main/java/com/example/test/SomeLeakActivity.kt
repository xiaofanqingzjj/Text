package com.example.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SomeLeakActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread {
            TestLeaks.test(this)
        }.start()
    }
}
