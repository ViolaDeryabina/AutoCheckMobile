package com.example.autocheckmobile.domain.usecase.submission

import com.example.netlib.data.dto.*
import com.example.netlib.data.dto.sub.AiReviewResponse
import com.example.netlib.data.dto.sub.SubmissionResponse
import com.example.netlib.data.dto.sub.SubmissionResultsResponse
import com.example.netlib.data.dto.sub.SubmissionStatusResponse
import com.example.netlib.data.dto.sub.SubmissionsData
import com.example.netlib.data.result.NetworkResult
import com.example.netlib.domain.repository.SubmissionsRepository
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject

// Получение списка проверок
class GetSubmissionsUseCase @Inject constructor(
    private val repository: SubmissionsRepository
) {
    suspend operator fun invoke(token: String): NetworkResult<ApiResponse<SubmissionsData>> {
        return repository.listSubmissions(token)
    }
}

// Создание проверки через Git URL
class CreateSubmissionWithGitUseCase @Inject constructor(
    private val repository: SubmissionsRepository
) {
    suspend operator fun invoke(
        token: String,
        assignmentId: Int,
        gitUrl: String
    ): NetworkResult<ApiResponse<SubmissionResponse>> {
        return repository.createSubmissionWithGit(token, assignmentId, gitUrl)
    }
}

// Создание проверки через ZIP файл
class CreateSubmissionWithZipUseCase @Inject constructor(
    private val repository: SubmissionsRepository
) {
    suspend operator fun invoke(
        token: String,
        assignmentId: Int,
        zipFile: File
    ): NetworkResult<ApiResponse<SubmissionResponse>> {
        return repository.createSubmissionWithZip(token, assignmentId, zipFile)
    }
}

// Получение проверки по ID
class GetSubmissionUseCase @Inject constructor(
    private val repository: SubmissionsRepository
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int
    ): NetworkResult<ApiResponse<SubmissionResponse>> {
        return repository.getSubmission(token, submissionId)
    }
}

// Получение статуса проверки
class GetSubmissionStatusUseCase @Inject constructor(
    private val repository: SubmissionsRepository
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int
    ): NetworkResult<ApiResponse<SubmissionStatusResponse>> {
        return repository.getSubmissionStatus(token, submissionId)
    }
}

// Получение результатов проверки
class GetSubmissionResultsUseCase @Inject constructor(
    private val repository: SubmissionsRepository
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int
    ): NetworkResult<ApiResponse<SubmissionResultsResponse>> {
        return repository.getSubmissionResults(token, submissionId)
    }
}

// Повторный запуск проверки
class RerunSubmissionUseCase @Inject constructor(
    private val repository: SubmissionsRepository
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int
    ): NetworkResult<ApiResponse<SubmissionResponse>> {
        return repository.rerunSubmission(token, submissionId)
    }
}

// Обновление вердикта
class UpdateVerdictUseCase @Inject constructor(
    private val repository: SubmissionsRepository
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int,
        verdict: String
    ): NetworkResult<ApiResponse<SubmissionResponse>> {
        return repository.updateVerdict(token, submissionId, verdict)
    }
}

// Скачивание отчета
class DownloadReportUseCase @Inject constructor(
    private val repository: SubmissionsRepository
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int
    ): NetworkResult<ResponseBody> {
        return repository.downloadReport(token, submissionId)
    }
}

// Получение AI ревью
class GetAiReviewUseCase @Inject constructor(
    private val repository: SubmissionsRepository
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int
    ): NetworkResult<ApiResponse<AiReviewResponse>> {
        return repository.getAiReview(token, submissionId)
    }
}