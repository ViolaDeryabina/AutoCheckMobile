package com.example.autocheckmobile.domain

import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.HealthResponse
import com.example.netlib.data.dto.ReportsStatsData
import com.example.netlib.data.result.NetworkResult
import com.example.netlib.domain.repository.ReportsRepository
import javax.inject.Inject

// Получение статистики
class GetStatsUseCase @Inject constructor(
    private val repository: ReportsRepository
) {
    suspend operator fun invoke(token: String): NetworkResult<ApiResponse<ReportsStatsData>> {
        return repository.getStats(token)
    }
}

// Проверка здоровья сервера
class HealthCheckUseCase @Inject constructor(
    private val repository: ReportsRepository
) {
    suspend operator fun invoke(): NetworkResult<HealthResponse> {
        return repository.healthCheck()
    }
}