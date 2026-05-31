package com.example.autocheckmobile.data.remote

import com.google.gson.JsonElement
import com.google.gson.Gson

data class SubmissionResultsApiData(
    val items: List<CheckerResultApiItem>? = null,
    val total: Int = 0,
)

data class CheckerResultApiItem(
    val checker: String? = null,
    val status: String? = null,
    val score: Int? = null,
    val message: String? = null,
    val details: JsonElement? = null,
)

data class AiReviewApiData(
    val available: Boolean? = null,
    val summary: String? = null,
    val strengths: List<String>? = null,
    val improvements: List<String>? = null,
)

private val gson = Gson()

fun JsonElement?.toDetailText(): String {
    if (this == null || isJsonNull) return ""
    return when {
        isJsonPrimitive -> asString
        isJsonObject -> gson.toJson(this)
        isJsonArray -> gson.toJson(this)
        else -> toString()
    }
}
