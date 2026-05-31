package com.example.autocheckmobile.domain.usecase.submission

import com.example.autocheckmobile.data.remote.AppSubmissionsRepository
import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.sub.AiReviewResponse
import com.example.netlib.data.dto.sub.SubmissionResponse
import com.example.netlib.data.dto.sub.SubmissionResultsResponse
import com.example.netlib.data.dto.sub.SubmissionStatusResponse
import com.example.netlib.data.dto.sub.SubmissionsData
import com.example.netlib.data.result.NetworkResult
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject

class GetSubmissionsUseCase @Inject constructor(
    private val repository: AppSubmissionsRepository,
) {
    suspend operator fun invoke(token: String): NetworkResult<ApiResponse<SubmissionsData>> =
        repository.listSubmissions(token)
}

class CreateSubmissionWithGitUseCase @Inject constructor(
    private val repository: AppSubmissionsRepository,
) {
    suspend operator fun invoke(
        token: String,
        assignmentId: Int,
        gitUrl: String,
    ): NetworkResult<ApiResponse<SubmissionResponse>> =
        repository.createSubmissionWithGit(token, assignmentId, gitUrl)
}

class CreateSubmissionWithZipUseCase @Inject constructor(
    private val repository: AppSubmissionsRepository,
) {
    suspend operator fun invoke(
        token: String,
        assignmentId: Int,
        zipFile: File,
    ): NetworkResult<ApiResponse<SubmissionResponse>> =
        repository.createSubmissionWithZip(token, assignmentId, zipFile)
}

class GetSubmissionUseCase @Inject constructor(
    private val repository: AppSubmissionsRepository,
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int,
    ): NetworkResult<ApiResponse<SubmissionResponse>> =
        repository.getSubmission(token, submissionId)
}

class GetSubmissionStatusUseCase @Inject constructor(
    private val repository: AppSubmissionsRepository,
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int,
    ): NetworkResult<ApiResponse<SubmissionStatusResponse>> =
        repository.getSubmissionStatus(token, submissionId)
}

class GetSubmissionResultsUseCase @Inject constructor(
    private val repository: AppSubmissionsRepository,
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int,
    ): NetworkResult<ApiResponse<SubmissionResultsResponse>> =
        repository.getSubmissionResults(token, submissionId)
}

class RerunSubmissionUseCase @Inject constructor(
    private val repository: AppSubmissionsRepository,
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int,
    ): NetworkResult<ApiResponse<SubmissionResponse>> =
        repository.rerunSubmission(token, submissionId)
}

class UpdateVerdictUseCase @Inject constructor(
    private val repository: AppSubmissionsRepository,
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int,
        verdict: String,
    ): NetworkResult<ApiResponse<SubmissionResponse>> =
        repository.updateVerdict(token, submissionId, verdict)
}

class DownloadReportUseCase @Inject constructor(
    private val repository: AppSubmissionsRepository,
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int,
    ): NetworkResult<ResponseBody> =
        repository.downloadReport(token, submissionId)
}

class GetAiReviewUseCase @Inject constructor(
    private val repository: AppSubmissionsRepository,
) {
    suspend operator fun invoke(
        token: String,
        submissionId: Int,
    ): NetworkResult<ApiResponse<AiReviewResponse>> =
        repository.getAiReview(token, submissionId)
}
