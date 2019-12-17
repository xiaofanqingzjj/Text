package com.example.test.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.test.R


/**
 *
 */
class TipReadAlarmBroadcastReceiver : BroadcastReceiver() {

    companion object {

        const val TAG = "TipReadAlarmBroadcastReceiver"

        const val ACTION = "com.tencent.story.alarm"

        const val NOTIFY_ID = 1001
    }


    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive：${intent.action}")
        val action = intent.action
        if (ACTION == action) {
            notifyUpdate(context)
        }
    }

    private fun notifyUpdate(context: Context) {

        val pendingIntent = PendingIntent.getBroadcast(context, 0, Intent(ACTION), 0)

        NotificationHelper.notify(
                context = context,
                id = NOTIFY_ID,
                title = "一零零一",
                content = "今日作品更新，快来看看吧~",
                smallIcon = R.mipmap.ic_launcher,
                pendingIntent =  pendingIntent
        )
    }


}