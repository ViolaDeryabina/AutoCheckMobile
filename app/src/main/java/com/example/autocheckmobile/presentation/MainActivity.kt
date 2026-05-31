package com.example.autocheckmobile.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.autocheckmobile.presentation.navigation.AppNavHost
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import androidx.compose.material3.Surface
import dagger.hilt.android.AndroidEntryPoint

/**
 * Назначение: главная Activity приложения AutoCheckMobile.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.i("[MainActivity]", "Старт — onCreate")
        setContent {
            CustomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DesignTokens.Background,
                ) {
                    AppNavHost()
                }
            }
        }
    }
}
