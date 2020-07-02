package com.example.test.filemanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class TestFileManager : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filesDir
        getDir("aa", 0)

        externalCacheDir
    }

}