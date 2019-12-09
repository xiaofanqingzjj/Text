package com.example.test.install

import android.content.Context
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider.getUriForFile
import android.os.Build.VERSION_CODES
import android.os.Build.VERSION
import android.os.Build.VERSION.SDK_INT
import androidx.core.content.FileProvider
import java.io.File


object ApkInstaller {


    fun installApk(context: Context, downloadApk: String) {


        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(downloadApk)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            val apkUri = FileProvider.getUriForFile(context, "com.example.test.fileprovider", file)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val uri = Uri.fromFile(file)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        }

        context.startActivity(intent)

    }
}