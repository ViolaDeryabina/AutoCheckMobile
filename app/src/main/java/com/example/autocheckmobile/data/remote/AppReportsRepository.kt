package com.example.autocheckmobile.data.remote

import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.ReportsStatsData
import com.example.netlib.data.result.NetworkResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppReportsRepository @Inject constructor(
    private val api: AppApiService,
) {
    suspend fun getStats(token: String): NetworkResult<ApiResponse<ReportsStatsData>> =
        safeApiCall { api.getStats(bearerToken(token)) }
}
