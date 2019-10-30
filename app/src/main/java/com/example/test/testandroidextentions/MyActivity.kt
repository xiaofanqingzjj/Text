package com.example.test.testandroidextentions

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.test_bind_views.*
import kotlinx.android.synthetic.main.test_bind_views.view.*


class MyActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        text?.text = "text"
    }
}

class MyFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        text?.text = "text"
    }
}

class MyView(context: Context, attributeSet: AttributeSet? = null): View(context, attributeSet){

    init {
        text?.text = "text"
    }

}