package com.example.virnandaelsa_3.service

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.virnandaelsa_3.R
import com.example.virnandaelsa_3.upload_dp
import com.example.virnandaelsa_3.upload_dp.Companion.CHANNEL_ID
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "dummy_channel"
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (message.notification != null){
            startNotification()
        }
    }
    private fun startNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.wisma)
            .setContentTitle("Pesanan Telah Berhasil \uD83C\uDF89")
            .setContentText("Terima kasih telah melakukan pemesanan. \nSemoga harimu menyenangkan!!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }
}