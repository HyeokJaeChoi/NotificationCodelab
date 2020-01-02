package com.hyeok.notificationcodelab

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    private val NOTIFICATION_ID = 0
    private val notifyManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val ACTION_UPDATE_NOTIFICATION = "com.example.android.notifyme.ACTION_UPDATE_NOTIFICATION"
    private val notificationReceiver by lazy { NotificationReceiver() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notify.setOnClickListener { sendNotification() }
        update.setOnClickListener { updateNotification() }
        cancel.setOnClickListener { cancelNotification() }

        createNotificationChannel()
        setNotificationButtonState(true, false, false)

        registerReceiver(notificationReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))
    }

    override fun onDestroy() {
        unregisterReceiver(notificationReceiver)
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Mascot Notification",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = "Notification from Mascot"
            }

            notifyManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun buildNotificationBuilder(): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("You've been notified!")
            .setContentText("This is your notification text.")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_android)
    }

    private fun sendNotification() {
        notifyManager.notify(NOTIFICATION_ID, buildNotificationBuilder().build())
        setNotificationButtonState(false, true, true)
    }

    private fun updateNotification() {
        val androidImage = BitmapFactory.decodeResource(resources, R.drawable.mascot_1)

        val updatedNotificationBuilder = buildNotificationBuilder().setStyle(NotificationCompat.BigPictureStyle()
            .bigPicture(androidImage)
            .setBigContentTitle("Notification Updated!"))

        notifyManager.notify(NOTIFICATION_ID, updatedNotificationBuilder.build())

        setNotificationButtonState(false, false, true)
    }

    private fun cancelNotification() {
        notifyManager.cancel(NOTIFICATION_ID)
        setNotificationButtonState(true, false, false)
    }

    private fun setNotificationButtonState(isNotifyEnabled: Boolean, isUpdateEnabled: Boolean, isCancelEnabled: Boolean) {
        notify.isEnabled = isNotifyEnabled
        update.isEnabled = isUpdateEnabled
        cancel.isEnabled = isCancelEnabled
    }

    inner class NotificationReceiver: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            updateNotification()
        }
    }
}
