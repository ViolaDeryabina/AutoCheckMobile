package com.example.autocheckmobile.data.remote

import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.CandidateResponse
import com.example.netlib.data.dto.CandidatesData
import com.example.netlib.data.result.NetworkResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCandidatesRepository @Inject constructor(
    private val api: AppApiService,
) {
    suspend fun listCandidates(token: String): NetworkResult<ApiResponse<CandidatesData>> =
        safeApiCall { api.listCandidates(bearerToken(token)) }

    suspend fun getCandidate(
        token: String,
        candidateId: Int,
    ): NetworkResult<ApiResponse<CandidateResponse>> =
        safeApiCall { api.getCandidate(bearerToken(token), candidateId) }
}
