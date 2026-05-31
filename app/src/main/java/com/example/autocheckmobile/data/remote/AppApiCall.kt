package com.example.autocheckmobile.data.remote

import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.sub.CheckerResult
import com.example.netlib.data.dto.sub.SubmissionResultsResponse
import com.example.netlib.data.result.NetworkResult

internal fun mapCheckerResults(data: SubmissionResultsApiData?): List<CheckerResult> =
    data?.items.orEmpty().map { item ->
        val detailsText = item.details.toDetailText().ifBlank { item.message.orEmpty() }
        CheckerResult(
            name = item.checker.orEmpty().ifBlank { "Checker" },
            score = item.score ?: 0,
            maxScore = 100,
            details = detailsText,
        )
    }

internal fun wrapResults(
    submissionId: Int,
    data: SubmissionResultsApiData?,
): SubmissionResultsResponse = SubmissionResultsResponse(
    submissionId = submissionId,
    results = mapCheckerResults(data),
)

internal suspend fun <T> safeApiCall(block: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(block())
    } catch (error: retrofit2.HttpException) {
        val message = parseErrorMessage(error)
        android.util.Log.e("[AppApiCall]", "HTTP ${error.code()} — $message")
        NetworkResult.Error(error.code(), message)
    } catch (error: Exception) {
        android.util.Log.e("[AppApiCall]", "Exception — ${error.message}")
        NetworkResult.Exception(error.message ?: "Ошибка сети")
    }
}

private fun parseErrorMessage(error: retrofit2.HttpException): String {
    val raw = error.response()?.errorBody()?.string().orEmpty()
    if (raw.isBlank()) return error.message().orEmpty().ifBlank { "HTTP ${error.code()}" }
    return runCatching {
        val root = com.google.gson.Gson().fromJson(raw, com.google.gson.JsonObject::class.java)
        root.getAsJsonObject("error")?.get("message")?.asString
            ?: root.get("detail")?.asString
            ?: root.get("message")?.asString
    }.getOrNull()?.takeIf { it.isNotBlank() } ?: "HTTP ${error.code()}"
}

internal fun bearerToken(token: String): String =
    if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"
