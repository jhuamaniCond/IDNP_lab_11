package com.unsa.idnp_lab11.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.unsa.idnp_lab11.servicio.TemporizadorService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Punto 4: accion de la interfaz que inicia el temporizador. Antes de arrancar
 * el servicio se pide POST_NOTIFICATIONS (Android 13+), sin el cual la
 * notificacion persistente no se mostraria en el panel.
 */
@Composable
fun TemporizadorScreen(inicio: Long?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var permisoDenegado by remember { mutableStateOf(false) }

    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoDenegado = !concedido
        // El servicio se inicia igual: sin permiso solo se oculta la notificacion
        TemporizadorService.iniciar(context)
    }

    fun alPulsarIniciar() {
        val necesitaPermiso = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        if (necesitaPermiso) pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        else TemporizadorService.iniciar(context)
    }

    val activo = inicio != null
    Column(
        modifier = modifier.padding(24.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("CampusDocs", style = MaterialTheme.typography.labelLarge)
        Text("Sesión de estudio", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
        Text("Inicia el temporizador para medir cuánto tiempo dedicas a tus apuntes. " +
            "El tiempo se muestra en una notificación persistente mientras el " +
            "servicio en primer plano está activo.")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Estado del servicio", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                Text(
                    if (activo) "EN PRIMER PLANO" else "DETENIDO",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (activo) Color(0xFF2E7D32) else Color(0xFF757575)
                )
                if (inicio != null) {
                    val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(inicio))
                    Text("Sesión iniciada a las $hora")
                }
            }
        }

        Button(onClick = ::alPulsarIniciar, enabled = !activo,
            modifier = Modifier.fillMaxWidth()) {
            Text(if (activo) "Temporizador en curso" else "Iniciar temporizador")
        }

        if (permisoDenegado) {
            Text("Permiso de notificaciones denegado: el servicio corre, pero la " +
                "notificación no se mostrará en el panel.",
                color = MaterialTheme.colorScheme.error)
        }
    }
}
