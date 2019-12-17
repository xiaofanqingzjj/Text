package com.example.test.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.bedrock.module_base.util.TimeUtil
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_test_alarm.*
import java.util.*


object TipReadAlarmManager {

    const val TAG = "TipReadAlarmManager"


    /**
     * 添加一个闹钟
     *
     * @param context
     * @param time 闹钟触发的时间
     * @param interval 时间间隔
     */
    fun addAlarm(context: Context, time: Long, interval: Long) {

        val pendingIntent = alarmPendingIntent(context)
        Log.d(TAG, "addAlarm:${TimeUtil.format(time)}, packageName:${context.packageName},  interval:${interval}")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC, time, interval, pendingIntent)

    }

    private fun alarmPendingIntent(context: Context): PendingIntent {
        val intent =  Intent(TipReadAlarmBroadcastReceiver.ACTION).apply {
            // 必须显示指定接受者，要不然
            component = ComponentName(context, TipReadAlarmBroadcastReceiver::class.java)
            setPackage(context.packageName)
        }
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }


    /**
     * 关闭闹钟
     */
    fun cancelAlarm(context: Context) {
        Log.d(TAG, "cancelAlarm")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.cancel(alarmPendingIntent(context))
    }

}