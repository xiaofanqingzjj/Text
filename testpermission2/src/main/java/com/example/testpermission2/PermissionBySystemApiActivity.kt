package com.example.testpermission2

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.util.Log


/**
 * 使用系统API申请敏感权限
 *
 */
class PermissionBySystemApiActivity : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_system)

        requestNeedPermissions()
    }


    // 正常流程，我们希望用户授权之后才能走
    private fun normalFlow() {

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
