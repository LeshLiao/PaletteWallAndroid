package com.palettex.palettewall.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.palettex.palettewall.MainActivity
import com.palettex.palettewall.R

class MessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("GDT", "refresh token:$token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.run {
            var msg = StringBuffer()
            notification?.let {
                Log.d("GDT", "msg title:${it.title}, body:${it.body}")
                msg = msg.append("msg title:${it.title}, body:${it.body}\n")
            }
            data.forEach {
                val m = "key:${it.key}, value:${it.value}"
                Log.d("GDT", "m:$it")
                msg.append(m)
            }
            sendNotification(msg.toString())
        }
    }

    private fun sendNotification(msg: String) {
        Log.d("GDT", "sendNotification msg=$msg")
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, "")
            .setSmallIcon(R.drawable.ic_ai)
            .setContentTitle("FCM Message")
            .setContentText(msg)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}

