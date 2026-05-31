package com.example.autocheckmobile.data.remote

import android.util.Log
import com.example.netlib.data.result.NetworkResult
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException

private val gson = Gson()

internal suspend fun <T> safeApiCall(block: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(block())
    } catch (error: HttpException) {
        val message = parseErrorMessage(error)
        Log.e("[AppApiCall]", "HTTP ${error.code()} — $message")
        NetworkResult.Error(error.code(), message)
    } catch (error: Exception) {
        Log.e("[AppApiCall]", "Exception — ${error.message}")
        NetworkResult.Exception(error.message ?: "Ошибка сети")
    }
}

private fun parseErrorMessage(error: HttpException): String {
    val raw = error.response()?.errorBody()?.string().orEmpty()
    if (raw.isBlank()) return error.message().orEmpty().ifBlank { "HTTP ${error.code()}" }
    return runCatching {
        val root = gson.fromJson(raw, JsonObject::class.java)
        root.getAsJsonObject("error")?.get("message")?.asString
            ?: root.get("detail")?.asString
            ?: root.get("message")?.asString
    }.getOrNull()?.takeIf { it.isNotBlank() } ?: "HTTP ${error.code()}"
}

internal fun bearerToken(token: String): String =
    if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"
