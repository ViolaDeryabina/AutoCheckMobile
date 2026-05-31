package com.example.autocheckmobile.domain.usecase.candidate

import com.example.autocheckmobile.data.remote.AppCandidatesRepository
import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.CandidateResponse
import com.example.netlib.data.dto.CandidatesData
import com.example.netlib.data.result.NetworkResult
import javax.inject.Inject

class GetCandidatesUseCase @Inject constructor(
    private val repository: AppCandidatesRepository,
) {
    suspend operator fun invoke(token: String): NetworkResult<ApiResponse<CandidatesData>> =
        repository.listCandidates(token)
}

class GetCandidateUseCase @Inject constructor(
    private val repository: AppCandidatesRepository,
) {
    suspend operator fun invoke(
        token: String,
        candidateId: Int,
    ): NetworkResult<ApiResponse<CandidateResponse>> =
        repository.getCandidate(token, candidateId)
}
