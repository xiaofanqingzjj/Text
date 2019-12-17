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


class TestAlarm : Fragment() {

    companion object {
        const val TAG = "TestAlarm"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance();

        btn_set_alarm.setOnClickListener {

            // TODO Auto-generated method stub
            calendar.setTimeInMillis(System.currentTimeMillis());
            val mHour = calendar.get(Calendar.HOUR_OF_DAY);
            val mMinute = calendar.get(Calendar.MINUTE);


            TimePickerDialog(context!!,  object : TimePickerDialog.OnTimeSetListener {


                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

                    // TODO Auto-generated method stub
                    calendar.timeInMillis = System.currentTimeMillis()

                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    val interval: Long = 10000 //(24 * 60 * 60 * 1000)

                    TipReadAlarmManager.addAlarm(context!!.applicationContext, calendar.timeInMillis, interval)
//
                    tv_label.text = TimeUtil.format(calendar.timeInMillis)
//
//
//                    // 建立Intent和PendingIntent来调用目标组件
////                    val intent = new Intent(MainActivity.this, AlarmReceiver.class);
//
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//                    // 获取闹钟管理的实例
//                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//                    // 设置闹钟
//                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                    // 设置周期闹钟
//                    am.setRepeating(AlarmManager.RTC_WAKEUP,
//                            System.currentTimeMillis() + (10 * 1000),
//                            (24 * 60 * 60 * 1000), pendingIntent);
//                    String tmpS = "设置闹钟时间为" + format(hourOfDay)
//                    + ":" + format(minute);
//                    info.setText(tmpS);

                }
            }, mHour, mMinute, true)
                    .show();
        }


        btn_cancel.setOnClickListener {
            TipReadAlarmManager.cancelAlarm(context!!)
        }
    }



}