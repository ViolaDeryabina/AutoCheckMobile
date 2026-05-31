package com.example.autocheckmobile.data.remote

import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.sub.AiReviewResponse
import com.example.netlib.data.dto.sub.SubmissionResponse
import com.example.netlib.data.dto.sub.SubmissionResultsResponse
import com.example.netlib.data.dto.sub.SubmissionStatusResponse
import com.example.netlib.data.dto.sub.SubmissionsData
import com.example.netlib.data.dto.sub.VerdictUpdateRequest
import com.example.netlib.data.result.NetworkResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSubmissionsRepository @Inject constructor(
    private val api: AppApiService,
) {
    suspend fun listSubmissions(token: String): NetworkResult<ApiResponse<SubmissionsData>> =
        safeApiCall { api.listSubmissions(bearerToken(token)) }

    suspend fun createSubmissionWithGit(
        token: String,
        assignmentId: Int,
        gitUrl: String,
    ): NetworkResult<ApiResponse<SubmissionResponse>> = safeApiCall {
        api.createSubmission(
            token = bearerToken(token),
            assignmentId = assignmentId.toString().toRequestBody(textPlain()),
            gitUrl = gitUrl.toRequestBody(textPlain()),
            zipFile = null,
        )
    }

    suspend fun createSubmissionWithZip(
        token: String,
        assignmentId: Int,
        zipFile: File,
    ): NetworkResult<ApiResponse<SubmissionResponse>> = safeApiCall {
        val part = MultipartBody.Part.createFormData(
            "zip_file",
            zipFile.name,
            zipFile.asRequestBody("application/zip".toMediaTypeOrNull()),
        )
        api.createSubmission(
            token = bearerToken(token),
            assignmentId = assignmentId.toString().toRequestBody(textPlain()),
            gitUrl = null,
            zipFile = part,
        )
    }

    suspend fun getSubmission(
        token: String,
        submissionId: Int,
    ): NetworkResult<ApiResponse<SubmissionResponse>> =
        safeApiCall { api.getSubmission(bearerToken(token), submissionId) }

    suspend fun getSubmissionStatus(
        token: String,
        submissionId: Int,
    ): NetworkResult<ApiResponse<SubmissionStatusResponse>> =
        safeApiCall { api.getSubmissionStatus(bearerToken(token), submissionId) }

    suspend fun getSubmissionResults(
        token: String,
        submissionId: Int,
    ): NetworkResult<ApiResponse<SubmissionResultsResponse>> = safeApiCall {
        val response = api.getSubmissionResults(bearerToken(token), submissionId)
        ApiResponse(
            data = wrapResults(submissionId, response.data),
            error = response.error,
            meta = response.meta,
        )
    }

    suspend fun rerunSubmission(
        token: String,
        submissionId: Int,
    ): NetworkResult<ApiResponse<SubmissionResponse>> =
        safeApiCall { api.rerunSubmission(bearerToken(token), submissionId) }

    suspend fun updateVerdict(
        token: String,
        submissionId: Int,
        verdict: String,
    ): NetworkResult<ApiResponse<SubmissionResponse>> = safeApiCall {
        api.updateVerdict(
            bearerToken(token),
            submissionId,
            VerdictUpdateRequest(verdict),
        )
    }

    suspend fun downloadReport(
        token: String,
        submissionId: Int,
    ): NetworkResult<ResponseBody> =
        safeApiCall { api.downloadReport(bearerToken(token), submissionId) }

    suspend fun getAiReview(
        token: String,
        submissionId: Int,
    ): NetworkResult<ApiResponse<AiReviewResponse>> =
        safeApiCall { api.getAiReview(bearerToken(token), submissionId) }

    private fun textPlain() = "text/plain".toMediaTypeOrNull()
}
