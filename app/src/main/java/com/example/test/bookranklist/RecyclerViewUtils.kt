package com.example.test.bookranklist

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


fun RecyclerView.vertical() {
    layoutManager = object : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
        override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
            return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}

fun RecyclerView.horizontal() {
    layoutManager = object : LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {

        override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
            return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}