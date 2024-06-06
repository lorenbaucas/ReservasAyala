package com.reservasayala.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.reservasayala.R
import com.reservasayala.controllers.MainActivity

// ID de la notificación y de su canal
const val notificationID = 1
const val channelID = "channel"

// Variables que se tienen que usar para cambiar el título y el mensaje de las notificaciones
object NotificationData {
    var title = "Title"
    var message = "Message"
}

class Notification: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Al hacer click en la notificación se abrirá la aplicación
        val activityIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, activityIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // La notificación tendrá el icono de la aplicación, el título y mensaje que le hayamos
        // pasado, abrirá la aplicación y también hará una vibración simple
        val notification = NotificationCompat.Builder(context, channelID)
        .setSmallIcon(R.drawable.icon)
        .setContentTitle(NotificationData.title)
        .setContentText(NotificationData.message)
        .setContentIntent(pendingIntent)
        .setVibrate(longArrayOf(1000))
        .build()

        // Gestor de las notificaciones
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }
}