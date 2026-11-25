package com.unsa.idnp_lab11.servicio

import android.app.NotificationManager
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Punto 1: servicio en primer plano que sostiene el temporizador de la sesion
 * de estudio. Se declara en AndroidManifest.xml con foregroundServiceType
 * "specialUse".
 */
class TemporizadorService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: servicio creado")
        CanalNotificaciones.crear(this)              // punto 2
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACCION_INICIAR -> iniciarEnPrimerPlano()
            ACCION_DETENER -> detenerServicio()
        }
        return START_NOT_STICKY
    }

    private fun iniciarEnPrimerPlano() {
        if (inicioMonotonico == 0L) {
            inicioMonotonico = SystemClock.elapsedRealtime()
            _inicio.value = System.currentTimeMillis()
            iniciarActualizacionNotificacion()
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

    private fun detenerServicio() {
        timerJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        Log.i(TAG, "Servicio detenido explícitamente y notificación removida")
    }

    private fun iniciarActualizacionNotificacion() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (inicioMonotonico != 0L) {
                delay(1000L)
                if (inicioMonotonico == 0L) break
                val segundos = (SystemClock.elapsedRealtime() - inicioMonotonico) / 1000
                val notificacion = NotificacionTemporizador.construir(this@TemporizadorService, segundos)
                try {
                    val gestor = getSystemService(NotificationManager::class.java)
                    gestor?.notify(NotificacionTemporizador.ID_NOTIFICACION, notificacion)
                } catch (e: Exception) {
                    Log.e(TAG, "Error al actualizar notificación", e)
                }
            }
        }
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy: servicio destruido")
        timerJob?.cancel()
        serviceJob.cancel()
        inicioMonotonico = 0L
        _inicio.value = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    // Servicio iniciado (no enlazado): no ofrece interfaz de enlace
    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val TAG = "Lab11FGS"
        const val ACCION_INICIAR = "com.unsa.idnp_lab11.INICIAR"
        const val ACCION_DETENER = "com.unsa.idnp_lab11.DETENER"

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

        /** Punto 6: detiene el servicio y remueve la notificación. */
        fun detener(context: Context) {
            val intent = Intent(context, TemporizadorService::class.java)
                .setAction(ACCION_DETENER)
            context.startService(intent)
            Log.i(TAG, "startService (detener) solicitado desde la UI")
        }
    }
}
