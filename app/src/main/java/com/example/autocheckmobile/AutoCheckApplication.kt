package com.example.autocheckmobile

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

/**
 * Назначение: точка входа приложения, инициализация Hilt DI-контейнера.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@HiltAndroidApp
class AutoCheckApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("[AutoCheckApplication]", "Старт — приложение инициализировано")
    }
}
