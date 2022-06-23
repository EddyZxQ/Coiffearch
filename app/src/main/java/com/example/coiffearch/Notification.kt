package com.example.coiffearch

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.coiffearch.usuario.PanelUsuarioActivity


const val notificationID = 1
const val channelID = "all_notifications"
const val titleExtra = "titleExtra"
const val messageExtra = "messageextra"


class Notification: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {


        var irPanelNotificacionesUsuario = Intent(context, PanelUsuarioActivity::class.java)
        irPanelNotificacionesUsuario.putExtra("noti", "1")
        var pendingIntent =  PendingIntent.getActivity(context, 1, irPanelNotificacionesUsuario, PendingIntent.FLAG_ONE_SHOT)

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .setContentInfo(intent.getStringExtra(messageExtra))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.logo_app)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .build()


        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }
}