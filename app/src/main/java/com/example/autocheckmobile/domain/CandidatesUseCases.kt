package com.example.autocheckmobile.domain.usecase.candidate

import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.CandidateResponse
import com.example.netlib.data.dto.CandidatesData
import com.example.netlib.data.result.NetworkResult
import com.example.netlib.domain.repository.CandidatesRepository
import javax.inject.Inject

// Получение списка кандидатов
class GetCandidatesUseCase @Inject constructor(
    private val repository: CandidatesRepository
) {
    suspend operator fun invoke(token: String): NetworkResult<ApiResponse<CandidatesData>> {
        return repository.listCandidates(token)
    }
}

// Получение кандидата по ID
class GetCandidateUseCase @Inject constructor(
    private val repository: CandidatesRepository
) {
    suspend operator fun invoke(
        token: String,
        candidateId: Int
    ): NetworkResult<ApiResponse<CandidateResponse>> {
        return repository.getCandidate(token, candidateId)
    }
}