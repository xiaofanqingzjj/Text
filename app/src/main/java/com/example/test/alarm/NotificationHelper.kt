package com.example.test.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

object NotificationHelper {


    private var currentChannel: String? = null

    /**
     * 通知的chanel Id
     */
    val NOTIFICATION_CUSTOM_CHANNEL_ID = "story_default"
    val NOTIFICATION_CUSTOM_CHANNEL_NAME = "一零零一"


    /**
     * 8.0之后弹通知必须要设置chanel才能弹
     *
     * 设置NotificationManager默认的chanel
     *
     * @param context
     */
    fun initNotificationChannel(context: Context) { // 8。
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CUSTOM_CHANNEL_ID, NOTIFICATION_CUSTOM_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            nm.createNotificationChannel(channel)
        }

        currentChannel = NOTIFICATION_CUSTOM_CHANNEL_ID
    }


    fun notify(context: Context,
               id: Int,
               title: String?,
               content: String?,
               ticker: String? = null,
               smallIcon: Int,
               largeIcon: Bitmap? = null,

               pendingIntent: PendingIntent?,
               isSoundOn: Boolean = false,
               remoteView: RemoteViews? = null
               ) {

        if (currentChannel == null) {
            initNotificationChannel(context)
        }

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, currentChannel!!)

                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)

                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())

                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)


        builder.setTicker(if (!ticker.isNullOrEmpty()) ticker else title)

        if (isSoundOn) {
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE or Notification.FLAG_SHOW_LIGHTS)
        }

        // 自定义View通知
        if (remoteView != null) {
            builder.setContent(remoteView)
        }

        val notification: Notification = builder.build()

        nm.notify(id, notification)
    }

}