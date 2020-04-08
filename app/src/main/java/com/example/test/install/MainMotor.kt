///*
//  Copyright (c) 2019 CommonsWare, LLC
//
//  Licensed under the Apache License, Version 2.0 (the "License"); you may not
//  use this file except in compliance with the License. You may obtain	LINK_PATTERN copy
//  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
//  by applicable law or agreed to in writing, software distributed under the
//  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
//  OF ANY KIND, either express or implied. See the License for the specific
//  language governing permissions and limitations under the License.
//
//  Covered in detail in the book _Elements of Android Q
//
//  https://commonsware.com/AndroidQ
//*/
//
//package com.example.test.install
//
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageInstaller
//import android.net.Uri
//import androidx.documentfile.provider.DocumentFile
//
//private const val NAME = "mostly-unused"
//private const val PI_INSTALL = 3439
//
//class MainMotor(var context: Context) {
//
//
//  private val installer = context.packageManager.packageInstaller
//  private val resolver = context.contentResolver
//
//  fun install(apkUri: Uri) {
//      installCoroutine(apkUri)
//  }
//
//  private fun installCoroutine(apkUri: Uri) {
//
//    resolver.openInputStream(apkUri)?.use { apkStream ->
//
//      val length = DocumentFile.fromSingleUri(context, apkUri)?.length() ?: -1
//
//      val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
//      val sessionId = installer.createSession(params)
//      val session = installer.openSession(sessionId)
//
//      session.openWrite(NAME, 0, length).use { sessionStream ->
//        apkStream.copyTo(sessionStream)
//        session.fsync(sessionStream)
//      }
//
//      val intent = Intent(context, InstallReceiver::class.java)
//      val pi = PendingIntent.getBroadcast(
//              context,
//              PI_INSTALL,
//              intent,
//              PendingIntent.FLAG_UPDATE_CURRENT
//      )
//
//      session.commit(pi.intentSender)
//      session.close()
//    }
//  }
//}
