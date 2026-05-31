package com.example.autocheckmobile.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "autocheck_session")

/**
 * Назначение: локальное хранение JWT-токена и профиля пользователя между сессиями.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Singleton
class SessionStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val tokenKey = stringPreferencesKey("access_token")
    private val emailKey = stringPreferencesKey("user_email")
    private val nameKey = stringPreferencesKey("user_name")
    private val roleKey = stringPreferencesKey("user_role")
    private val userIdKey = stringPreferencesKey("user_id")

    val tokenFlow: Flow<String?> = context.sessionDataStore.data.map { prefs ->
        prefs[tokenKey]
    }

    val userEmailFlow: Flow<String?> = context.sessionDataStore.data.map { prefs ->
        prefs[emailKey]
    }

    val userNameFlow: Flow<String?> = context.sessionDataStore.data.map { prefs ->
        prefs[nameKey]
    }

    val userRoleFlow: Flow<String?> = context.sessionDataStore.data.map { prefs ->
        prefs[roleKey]
    }

    val userIdFlow: Flow<String?> = context.sessionDataStore.data.map { prefs ->
        prefs[userIdKey]
    }

    /**
     * Сохраняет токен и данные пользователя после успешной авторизации.
     */
    suspend fun saveSession(
        token: String,
        userId: Int,
        email: String,
        fullName: String,
        role: String,
    ) {
        Log.i("[SessionStorage]", "Сохранение сессии — userId=$userId")
        context.sessionDataStore.edit { prefs ->
            prefs[tokenKey] = token
            prefs[userIdKey] = userId.toString()
            prefs[emailKey] = email
            prefs[nameKey] = fullName
            prefs[roleKey] = role
        }
    }

    /**
     * Очищает сохранённую сессию при выходе из системы.
     */
    suspend fun clearSession() {
        Log.i("[SessionStorage]", "Очистка сессии")
        context.sessionDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
