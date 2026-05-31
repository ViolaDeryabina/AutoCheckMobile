package com.example.autocheckmobile.data.remote

import android.util.Log
import com.example.autocheckmobile.core.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Назначение: SSE-клиент для real-time обновления статуса проверки.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Singleton
class SubmissionEventClient @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Подписывается на SSE-поток до статуса done/error.
     * @return true если SSE отработал, false если нужен fallback polling.
     */
    suspend fun trackUntilDone(
        token: String,
        submissionId: Int,
        onStatus: suspend (String) -> Unit,
    ): Boolean = withContext(Dispatchers.IO) {
        val url = ApiConfig.submissionEventsUrl(submissionId)
        Log.i("[SubmissionEventClient]", "SSE подключение — submissionId=$submissionId")
        try {
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .header("Accept", "text/event-stream")
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("[SubmissionEventClient]", "SSE недоступен — code=${response.code}")
                    return@withContext false
                }
                val source = response.body?.source() ?: return@withContext false
                while (!source.exhausted()) {
                    val line = source.readUtf8Line() ?: break
                    if (!line.startsWith("data: ")) continue
                    val status = line.removePrefix("data: ").trim()
                    onStatus(status)
                    if (status == "done" || status == "error") {
                        Log.i("[SubmissionEventClient]", "SSE завершён — status=$status")
                        return@withContext true
                    }
                }
                true
            }
        } catch (error: Exception) {
            Log.e("[SubmissionEventClient]", "SSE ошибка — ${error.message}")
            false
        }
    }
}
