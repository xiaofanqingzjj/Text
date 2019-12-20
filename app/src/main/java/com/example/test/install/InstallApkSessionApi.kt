package com.example.test.install


import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast

import androidx.fragment.app.FragmentActivity

import com.bedrock.module_base.util.FileUtils
import com.bedrock.permissionrequestor.PermissionsRequestor
import com.example.test.R
import com.fortunexiao.tktx.async

import java.io.File
import java.io.IOException

class InstallApkSessionApi : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        PermissionsRequestor(this).request(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INSTALL_PACKAGES))

        setContentView(R.layout.install_apk_session_api)


        // Watch for button clicks.
        val button = findViewById<View>(R.id.install) as Button


        button.setOnClickListener {
            var session: PackageInstaller.Session? = null
            try {
                val packageInstaller = packageManager.packageInstaller
                val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)

                val sessionId = packageInstaller.createSession(params)
                session = packageInstaller.openSession(sessionId)

                addApkToInstallSession("test.apk", session)

                // Create an install status receiver.
                val context = this@InstallApkSessionApi

                val intent = Intent(context, InstallApkSessionApi::class.java)
                intent.action = PACKAGE_INSTALLED_ACTION

                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
                val statusReceiver = pendingIntent.intentSender
                // Commit the session (this will start the installation workflow).
                session.commit(statusReceiver)


            } catch (e: IOException) {
                throw RuntimeException("Couldn't install package", e)
            } catch (e: RuntimeException) {
                session?.abandon()
                throw e
            }
        }


        findViewById<View>(R.id.install2).setOnClickListener {
            ApkInstaller.installApk(this@InstallApkSessionApi, "/sdcard/test.apk")
            //                installApk(InstallApkSessionApi.this, "/sdcard/test.apk");
        }


        findViewById<View>(R.id.copy_apk).setOnClickListener {


            async(
                    doBackground = {
                        val catcheDir = File((externalCacheDir?.absolutePath
                                ?: "") + "/update_files")

                        if (!catcheDir.exists()) {
                            catcheDir.mkdirs()
                        }


                        val targetFile = File(catcheDir, "test.apk")
                        FileUtils.copyFile("/sdcard/test.apk", targetFile.absolutePath)
                        targetFile
                    },


                    postExecute = {
                        Toast.makeText(this@InstallApkSessionApi, "target:$it", Toast.LENGTH_SHORT).show()

                        installApk(this@InstallApkSessionApi, it?.absolutePath ?: "")
                    }
            )

        }


    }

    @Throws(IOException::class)
    private fun addApkToInstallSession(assetName: String, session: PackageInstaller.Session?) {


        // It's recommended to pass the file size to openWrite(). Otherwise installation may fail
        // if the disk is almost full.

        //        final OutputStream packageInSession = session.openWrite("package", 0, -1);
        //        final InputStream is = new FileInputStream(assetName);
        //        byte[] buffer = new byte[16384];
        //        int n;
        //        while ((n = is.read(buffer)) >= 0) {
        //            packageInSession.write(buffer, 0, n);
        //        }
        //
        //        is.close();


        session!!.openWrite("package", 0, -1).use { packageInSession ->
            assets.open(assetName).use { inputStream ->

                val buffer = ByteArray(16384)
                var n: Int = 0


                while ({n = inputStream.read(buffer); n}() >= 0) {
                    packageInSession.write(buffer, 0, n)
                }
            }
        }
    }


    // Note: this Activity must run in singleTop launchMode for it to be able to receive the intent
    // in onNewIntent().
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val extras = intent.extras
        if (PACKAGE_INSTALLED_ACTION == intent.action) {
            val status = extras!!.getInt(PackageInstaller.EXTRA_STATUS)
            val message = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE)
            when (status) {
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    // This test app isn't privileged, so the user has to confirm the install.
                    val confirmIntent = extras.get(Intent.EXTRA_INTENT) as Intent?
                    startActivity(confirmIntent)
                }
                PackageInstaller.STATUS_SUCCESS -> Toast.makeText(this, "Install succeeded!", Toast.LENGTH_SHORT).show()
                PackageInstaller.STATUS_FAILURE, PackageInstaller.STATUS_FAILURE_ABORTED, PackageInstaller.STATUS_FAILURE_BLOCKED, PackageInstaller.STATUS_FAILURE_CONFLICT, PackageInstaller.STATUS_FAILURE_INCOMPATIBLE, PackageInstaller.STATUS_FAILURE_INVALID, PackageInstaller.STATUS_FAILURE_STORAGE -> Toast.makeText(this, "Install failed! $status, $message", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "Unrecognized status received from installer: $status", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private val PACKAGE_INSTALLED_ACTION = "com.example.android.apis.content.SESSION_API_PACKAGE_INSTALLED"


        private fun installApk(context: Context, apkPath: String) {
            val intent = Intent(Intent.ACTION_VIEW)
            val file = File(apkPath)

//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//
//                val apkUri = FileProvider.getUriForFile(context, "com.example.test.fileprovider", file);
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//            } else {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val uri = Uri.fromFile(file)
                intent.setDataAndType(uri, "application/vnd.android.package-archive")
//            }

            //                    Intent intent = new Intent(Intent.ACTION_VIEW);
            //                    intent.setDataAndType(Uri.parse("file://" + DownloadTools.generateAbsolutePath(url)),
            //                            "application/vnd.android.package-archive");
            //                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //                    mContext.startActivity(intent);
            //        }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("UpdateManager", "installApk error", e)
            }

        }
    }
}