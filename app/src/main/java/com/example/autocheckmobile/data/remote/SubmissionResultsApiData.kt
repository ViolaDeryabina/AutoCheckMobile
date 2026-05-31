package com.example.autocheckmobile.data.remote

data class SubmissionResultsApiData(
    val items: List<CheckerResultApiItem>? = null,
    val total: Int = 0,
)

data class CheckerResultApiItem(
    val checker: String? = null,
    val status: String? = null,
    val score: Int? = null,
    val message: String? = null,
    val details: String? = null,
)
