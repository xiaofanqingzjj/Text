package com.example.opengl

import android.os.Bundle
import com.bedrock.module_base.MenuActivity

class MainActivity : MenuActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenu("Demo1", GLDemo1::class.java)

        addMenu("Demo2", GLDemo2::class.java)

        addMenu("Demo3", GLDemo3::class.java)
    }

}
