package com.example.virnandaelsa_3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessage : FirebaseMessagingService() {
    private val RC_INTENT = 100
    private val CHANNEL_ID = "virnandaelsa_3"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Kirim token baru ke server jika diperlukan
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Membuat Intent untuk membuka activity upload_dp
        val intent = Intent(this, upload_dp::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Memeriksa apakah ada data dalam pesan
        if (remoteMessage.data.isNotEmpty()) {
            val promoId = remoteMessage.data["promoId"].orEmpty()
            val promo = remoteMessage.data["promo"].orEmpty()
            val promoUntil = remoteMessage.data["promoUntil"].orEmpty()

            // Menyisipkan data ke Intent
            intent.putExtra("promoId", promoId)
            intent.putExtra("promo", promo)
            intent.putExtra("promoUntil", promoUntil)
            intent.putExtra("type", 0) // 0 = data

            // Mengirimkan notifikasi dengan data yang diambil
            sendNotif("Today Promo!!!", "$promo $promoUntil", intent)
        }

        // Memeriksa apakah ada notifikasi dalam pesan
        remoteMessage.notification?.let {
            val body = it.body.orEmpty()
            val title = it.title.orEmpty()

            // Menyisipkan judul dan body ke Intent
            intent.putExtra("title", title)
            intent.putExtra("body", body)
            intent.putExtra("type", 1) // 1 = notifikasi

            // Mengirimkan notifikasi dengan judul dan body
            sendNotif(title, body, intent)
        }
    }

    // Fungsi untuk mengirimkan notifikasi
    private fun sendNotif(title: String, body: String, intent: Intent) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            RC_INTENT,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Mengatur ringtone untuk notifikasi
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        // Membuat Notification Manager
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Implementasi Notification Channel untuk Android 8.0 (Oreo) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "App Notifications"
                setSound(ringtoneUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Membangun notifikasi
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.wisma) // Pastikan ikon ini ada di res/drawable
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.wisma)) // Pastikan ikon besar ini juga ada
            .setContentTitle(title)
            .setContentText(body)
            .setSound(ringtoneUri)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Notifikasi akan hilang setelah ditekan

        // Menampilkan notifikasi
        notificationManager.notify(RC_INTENT, notificationBuilder.build())
    }
}
