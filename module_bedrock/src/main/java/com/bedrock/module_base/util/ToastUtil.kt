package com.bedrock.module_base.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

private val toastTask = Runnable {

}

private val handler = Handler(Looper.getMainLooper())

fun quikToast(context: Context, text: String) {


    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}