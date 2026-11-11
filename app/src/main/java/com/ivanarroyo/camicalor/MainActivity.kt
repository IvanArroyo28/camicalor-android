package com.ivanarroyo.camicalor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ivanarroyo.camicalor.presentation.navigation.CamiCalorNavHost
import com.ivanarroyo.camicalor.ui.theme.CamicalorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CamicalorTheme {
                // Cada pantalla tiene su propio Scaffold y barra superior.
                CamiCalorNavHost()
            }
        }
    }
}
