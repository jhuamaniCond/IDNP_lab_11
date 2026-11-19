package com.unsa.idnp_lab11.servicio

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.unsa.idnp_lab11.MainActivity
import java.util.Locale

/**
 * Punto 3: notificacion persistente con titulo y tiempo transcurrido.
 */
object NotificacionTemporizador {

    const val ID_NOTIFICACION = 1101

    /** Convierte segundos en el formato hh:mm:ss. */
    fun formatear(segundos: Long): String = String.format(
        Locale.ROOT, "%02d:%02d:%02d",
        segundos / 3600, (segundos % 3600) / 60, segundos % 60
    )

    fun construir(context: Context, segundosTranscurridos: Long): Notification {
        // Tocar la notificacion vuelve a abrir la pantalla del temporizador
        val abrirApp = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, CanalNotificaciones.ID_CANAL)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Sesión de estudio en curso")
            .setContentText("Tiempo transcurrido: ${formatear(segundosTranscurridos)}")
            .setContentIntent(abrirApp)
            .setOngoing(true)              // persistente: no se descarta deslizando
            .setOnlyAlertOnce(true)        // las actualizaciones no vuelven a avisar
            .setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }
}
