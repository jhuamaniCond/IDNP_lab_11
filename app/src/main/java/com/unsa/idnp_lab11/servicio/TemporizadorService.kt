package com.unsa.idnp_lab11.servicio

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Punto 1: servicio en primer plano que sostiene el temporizador de la sesion
 * de estudio. Se declara en AndroidManifest.xml con foregroundServiceType
 * "specialUse".
 */
class TemporizadorService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: servicio creado")
        CanalNotificaciones.crear(this)              // punto 2
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACCION_INICIAR -> iniciarEnPrimerPlano()
        }
        return START_NOT_STICKY
    }

    private fun iniciarEnPrimerPlano() {
        if (inicioMonotonico == 0L) {
            inicioMonotonico = SystemClock.elapsedRealtime()
            _inicio.value = System.currentTimeMillis()
        }
        val segundos = (SystemClock.elapsedRealtime() - inicioMonotonico) / 1000
        val notificacion = NotificacionTemporizador.construir(this, segundos)

        val tipo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
        // Debe llamarse pocos segundos despues de startForegroundService()
        ServiceCompat.startForeground(
            this, NotificacionTemporizador.ID_NOTIFICACION, notificacion, tipo
        )
        Log.i(TAG, "startForeground: notificación ${NotificacionTemporizador.ID_NOTIFICACION} " +
                "publicada, tiempo=${NotificacionTemporizador.formatear(segundos)}")
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy: servicio destruido")
        inicioMonotonico = 0L
        _inicio.value = null
        super.onDestroy()
    }

    // Servicio iniciado (no enlazado): no ofrece interfaz de enlace
    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val TAG = "Lab11FGS"
        const val ACCION_INICIAR = "com.unsa.idnp_lab11.INICIAR"

        private var inicioMonotonico = 0L
        private val _inicio = MutableStateFlow<Long?>(null)
        /** Hora (epoch ms) en que empezo la sesion, o null si no hay servicio. */
        val inicio: StateFlow<Long?> = _inicio.asStateFlow()

        /** Punto 4: lo invoca el boton de la interfaz. */
        fun iniciar(context: Context) {
            val intent = Intent(context, TemporizadorService::class.java)
                .setAction(ACCION_INICIAR)
            ContextCompat.startForegroundService(context, intent)
            Log.i(TAG, "startForegroundService solicitado desde la UI")
        }
    }
}
