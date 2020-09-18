package com.novadev.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mReceiver: NotificationReceiver

    companion object {
        const val NOTIFICATION_ID = 1
        const val ACTION_UPDATE_NOTIFICATION =
            "com.android.example.notifyme.ACTION_UPDATE_NOTIFICATION"
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initListeners()
        registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))
    }

    private fun initView() {
        // Create the notification channel.
        createNotificationChannel()
        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )
        mReceiver = NotificationReceiver()
    }

    private fun createNotificationChannel() {
        mNotificationManager =
            getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_description)

            mNotificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun initListeners() {
        btnNotify.setOnClickListener {
            sendNotification()
        }
        btnUpdate.setOnClickListener {
            updateNotification()
        }
        btnCancel.setOnClickListener {
            cancelNotification()
        }
    }

    private fun sendNotification() {
        // Sets up the pending intent to update the notification.
        // Corresponds to a press of the Update Me! button.
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(
            this, NOTIFICATION_ID,
            updateIntent, PendingIntent.FLAG_ONE_SHOT
        )

        val notifyBuilder: NotificationCompat.Builder = getNotificationBuilder()

        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build())

        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = true,
            isCancelEnabled = true
        )
    }

    private fun setNotificationButtonState(
        isNotifyEnabled: Boolean,
        isUpdateEnabled: Boolean,
        isCancelEnabled: Boolean
    ) {
        btnNotify.isEnabled = isNotifyEnabled
        btnUpdate.isEnabled = isUpdateEnabled
        btnCancel.isEnabled = isCancelEnabled
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            this, NOTIFICATION_ID, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setContentIntent(notificationPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

    }

    fun updateNotification() {
        val androidImage = BitmapFactory.decodeResource(resources, R.drawable.mascot_1)
        val notifyBuilder: NotificationCompat.Builder = getNotificationBuilder()
        notifyBuilder.setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!")
        )

        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build())

        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = false,
            isCancelEnabled = true
        )

    }

    private fun cancelNotification() {
        mNotificationManager.cancel(1)
        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )

    }


    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }
}