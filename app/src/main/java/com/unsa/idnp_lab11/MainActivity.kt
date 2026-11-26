package com.unsa.idnp_lab11

import android.os.Bundle
import android.util.Log
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
        Log.i(TemporizadorService.TAG, "MainActivity onCreate(): Actividad creada")
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

    override fun onStart() {
        super.onStart()
        Log.i(TemporizadorService.TAG, "MainActivity onStart(): Actividad visible en pantalla")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TemporizadorService.TAG, "MainActivity onResume(): Actividad en primer plano interactiva")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TemporizadorService.TAG, "MainActivity onPause(): Actividad perdiendo el foco")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TemporizadorService.TAG, "MainActivity onStop(): [SEGUNDO PLANO] La actividad ya no es visible. El servicio debe continuar ejecutándose.")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TemporizadorService.TAG, "MainActivity onDestroy(): Actividad destruida")
    }
}
