package com.unsa.idnp_lab11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unsa.idnp_lab11.servicio.TemporizadorService
import com.unsa.idnp_lab11.ui.TemporizadorScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                val inicio by TemporizadorService.inicio.collectAsStateWithLifecycle()
                Scaffold(modifier = Modifier.fillMaxSize()) { relleno ->
                    TemporizadorScreen(inicio = inicio, modifier = Modifier.padding(relleno))
                }
            }
        }
    }
}
