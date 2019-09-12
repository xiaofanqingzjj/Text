package com.example.testpermission

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Path
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import okhttp3.*
import java.io.IOException
import kotlin.random.Random


class MainActivity : FragmentActivity() {


//    private var iv1: View? = null
//    private var iv2: View? = null

    val handler: Handler = Handler()


    private fun playOne(org: Point, child: View, pathControll: AnimPathHelper.PathControll, delay: Long) {

        AnimatorSet().apply {

            interpolator = DecelerateInterpolator()
            duration = 600

            playTogether(*mutableListOf<ObjectAnimator>().apply {
                val path = Path().apply {
                    moveTo(org.x.toFloat(), org.y.toFloat())
                    quadTo(pathControll.controlX.toFloat(), pathControll.controlY.toFloat(), pathControll.destX.toFloat(), pathControll.destY.toFloat())
                }
                add(ObjectAnimator.ofFloat(child, View.X, View.Y, path))
                add(ObjectAnimator.ofFloat(child, View.ALPHA, 1f, 1f, 1f, 0f))

            }.toTypedArray())

            startDelay = delay
        }.start()
    }


    private fun testParseUri() {

        var url = "http://www.baidu.com?a=1&b=2&c=3&4="

        val uri = Uri.parse(url)
        val query = uri.query
        val queryNames = uri.queryParameterNames


        val values = queryNames?.map {
            uri.getQueryParameter(it)
        }


        Log.d(TAG, "uri:$uri, query:$query, names:$queryNames, values:$values")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        requestNeedPermissions()


        val container = findViewById<ViewGroup>(R.id.container);

        val image = findViewById<View>(R.id.iv_heart)

        findViewById<View>(R.id.btn1).setOnClickListener {


            testParseUri()

            val org = Point(container.width/ 2, container.height / 2)
            val paths = AnimPathHelper.buildPaths(this@MainActivity, org, container.childCount)

            for (i in 0 until container.childCount) {
                val child = container.getChildAt(i)

                playOne(child = child, org = org, pathControll = paths[i], delay = Random.nextLong(0, 200))
            }



//                }.toTypedArray())
//


//            val size = DensityUtil.dip2px(this@MainActivity, 30f).toFloat()
//
//
//
//
//            val duration = 2000L
//
//
//            handler.postDelayed({
//                iv1?.bringToFront()
//            }, duration / 2)
//
//
//            AnimatorSet().apply {
//
//                playTogether(*mutableListOf<ObjectAnimator>().apply {
//
//                    if (Build.VERSION.SDK_INT >= 21) {
//                        val path1 = Path()
//                        path1.rCubicTo(-size, -size, -size * 2, 0f, -size, size)
//                        path1.rCubicTo(size, size, size * 3f, size, size, -size)
//                        val path2 = Path()
//                        path2.rCubicTo(size, size, size * 2, 0f, size, -size)
//                        path2.rCubicTo(-size , -size, -size * 3f, size, -size, size)
//
//                        add(ObjectAnimator.ofFloat(iv1, View.TRANSLATION_X, View.TRANSLATION_Y, path1))
//                        add(ObjectAnimator.ofFloat(iv2, View.TRANSLATION_X, View.TRANSLATION_Y, path2))
//                    } else {
//                        add(ObjectAnimator.ofFloat(iv1, View.TRANSLATION_X, 0f, -size, -size *2, -size, 0f, size, 0f))
//
//                        add(ObjectAnimator.ofFloat(iv1, View.TRANSLATION_X, 0f, -size, -size *2, -size, 0f, size, 0f))
//                        add(ObjectAnimator.ofFloat(iv1, View.TRANSLATION_Y, 0f, -size, 0f, size, size, size, 0f))
//
//                        add(ObjectAnimator.ofFloat(iv2, View.TRANSLATION_X, 0f, size, size *2, size, -0f, -size,  0f))
//                        add(ObjectAnimator.ofFloat(iv2, View.TRANSLATION_Y, 0f, size, 0f, -size, -size, -size, 0f))
//                    }
//
//
//
//                }.toTypedArray())
//
//
//
//
//                this.duration = duration
//
//
//
//            }.start()

//        test()

        }

//        iv1 = findViewById(R.id.iv1)
//        iv2 = findViewById(R.id.iv2)


        //        // 添加Fragment
        //
        //        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //
        //        ft.add(R.id.fl_container, new SimpleFragment(), "tag1");
        //        ft.add(R.id.fl_container, new SimpleFragment(), "tag1");
        //
        ////        getSupportFragmentManager().findFragmentByTag()
        //
        //        ft.commit();
    }

    //
    //    @NeedsPermission(Manifest.permission.CAMERA)
    //    void onPermissonGranted() {
    //
    //    }
    //
    //    @NeedsPermission(Manifest.permission.CAMERA)
    //    void onPermissonDenied() {
    //
    //    }



    fun test() {
        val url = "https://api.story.qq.com/user/login"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build()
        val call = okHttpClient.newCall(request)


        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "ok http  onFailure: ", e)
            }

            override fun onResponse(call: Call, response: Response) {

                Log.d(TAG, "ok http onResponse: " + response.body()?.string())
            }

        })


        Thread(Runnable {
            try {
                val ret = HttpUtil.doGet("https://api.story.qq.com/user/login")
                Log.d(TAG, "zhenngshi:$ret")
            } catch (e: Exception) {
                Log.e(TAG, "zhenngshi error :", e)
            }

            Log.d(TAG, "-----------------------------------------")

            try {
                val ret = HttpUtil.doGet(" https://betaapi.story.qq.com/user/login")
                Log.d(TAG, "yufabu:$ret")
            } catch (e: Exception) {
                Log.e(TAG, "yufabu error: ", e)
            }




        }).start()




//        call.enqueue(object : Callback {
//            fun onFailure(call: Call, e: IOException) {
//                Log.d(TAG, "onFailure: ")
//            }
//
//            @Throws(IOException::class)
//            fun onResponse(call: Call, response: Response) {
//                Log.d(TAG, "onResponse: " + response.body().string())
//            }
//        })
    }



    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        Log.w(TAG, "onSaveInstanceState")
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.w(TAG, "onSaveInstanceState----------")
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        Log.w(TAG, "onRestoreInstanceState")
    }

    private fun requestNeedPermissions() {

        ActivityCompat.requestPermissions(this, arrayOf(

                //                // 1.日历
                //                android.Manifest.permission.READ_CALENDAR              // to access your calendar
                //
                //                // 2.相机
                //                , android.Manifest.permission.CAMERA                    // to take pictures and record video
                //
                //                // 3.联系人
                //                , android.Manifest.permission.READ_CONTACTS             // read your contacts
                //                , android.Manifest.permission.WRITE_CONTACTS            // modify and delete your contacts
                //                , android.Manifest.permission.GET_ACCOUNTS              // access all accounts stored on device
                //
                //
                //                // 4.位置
                //                , android.Manifest.permission.ACCESS_COARSE_LOCATION    // to get precise location (GPS and network-base)
                //                , android.Manifest.permission.ACCESS_FINE_LOCATION      // to get precise location (GPS and network-base)
                //
                //                // 5.麦克风
                //                , android.Manifest.permission.RECORD_AUDIO              // to record audio?

                // 6.电话
                android.Manifest.permission.READ_PHONE_STATE           // access phone number, IMEI, and IMSI
        )//                , android.Manifest.permission.CALL_PHONE                 // directly call phone numbers
                //                , android.Manifest.permission.READ_CALL_LOG              // read call history
                //                , android.Manifest.permission.WRITE_CALL_LOG             // write and delete call history
                //                , android.Manifest.permission.ADD_VOICEMAIL              //
                //                , android.Manifest.permission.USE_SIP                    // use session initiation protocol to stream video
                //                , android.Manifest.permission.PROCESS_OUTGOING_CALLS     // answer, monitor, and end calls
                // 7.传感器
                //                , android.Manifest.permission.BODY_SENSORS               //
                //
                //                // 8.短信
                //                , android.Manifest.permission.SEND_SMS                   //
                //                , android.Manifest.permission.RECEIVE_SMS                //
                //                , android.Manifest.permission.READ_SMS                   //
                //                , android.Manifest.permission.RECEIVE_WAP_PUSH           //
                //                , android.Manifest.permission.RECEIVE_MMS                //
                //                // 9. 存储
                //                ,android.Manifest.permission.WRITE_EXTERNAL_STORAGE      // to access photos, media, and files on your devices
                //                ,android.Manifest.permission.READ_EXTERNAL_STORAGE       // to access photos, media, and files on your devices
                //
                , 0)


    }


    //    /**
    //     * 申请特殊权限：系统覆窗
    //     */
    //    private fun requestAlertWindowPermission() {
    //        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    //        intent.data = Uri.parse("package:$packageName")
    //        startActivityForResult(intent, REQUEST_CODE)
    //    }
    //
    //
    //    /**
    //     * 申请特殊权限：写设置
    //     */
    //    private fun requestWriteSettings() {
    //        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
    //        intent.data = Uri.parse("package:$packageName")
    //        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS)
    //    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                Log.i(TAG, "onActivityResult write settings granted")
            }
        } else if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Log.i(TAG, "onActivityResult granted")
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d(TAG, "onRequestPermissionsResult requestCode:$requestCode, permissions:$permissions, grantResults:$grantResults")
    }

    companion object {


        val TAG = "MainActivity"

        private val REQUEST_CODE = 1
        private val REQUEST_CODE_WRITE_SETTINGS = 2
    }
}
