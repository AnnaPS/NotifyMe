package com.novadev.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver(){

    lateinit var mainActivity: MainActivity
    override fun onReceive(context: Context?, intent: Intent?) {
        mainActivity = MainActivity()
        mainActivity.updateNotification()
    }

}