package com.unsa.idnp_lab11.servicio

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log

/**
 * Punto 2: canal de notificacion del temporizador.
 * Desde Android 8.0 (API 26) toda notificacion debe pertenecer a un canal; en
 * versiones anteriores los canales no existen y no se crea nada.
 */
object CanalNotificaciones {

    const val ID_CANAL = "temporizador_estudio"
    private const val TAG = "Lab11FGS"

    fun crear(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val canal = NotificationChannel(
            ID_CANAL,
            "Temporizador de estudio",
            NotificationManager.IMPORTANCE_LOW   // sin sonido ni vibracion
        ).apply {
            description = "Muestra el tiempo de la sesión de estudio en curso"
            setShowBadge(false)
        }
        // createNotificationChannel es idempotente: si el canal ya existe no se duplica
        val gestor = context.getSystemService(NotificationManager::class.java)
        gestor.createNotificationChannel(canal)
        Log.i(TAG, "Canal '$ID_CANAL' creado/verificado (importancia LOW)")
    }
}
