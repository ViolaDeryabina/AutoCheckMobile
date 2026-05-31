package com.example.autocheckmobile.domain

import com.example.autocheckmobile.data.remote.AppReportsRepository
import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.ReportsStatsData
import com.example.netlib.data.result.NetworkResult
import javax.inject.Inject

class GetStatsUseCase @Inject constructor(
    private val repository: AppReportsRepository,
) {
    suspend operator fun invoke(token: String): NetworkResult<ApiResponse<ReportsStatsData>> =
        repository.getStats(token)
}

// Оставлен для совместимости с HealthViewModel (не используется в навигации).
class HealthCheckUseCase @Inject constructor() {
    suspend operator fun invoke(): NetworkResult<com.example.netlib.data.dto.HealthResponse> =
        NetworkResult.Exception("Health check не настроен")
}
