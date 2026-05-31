package com.example.autocheckmobile.data.remote

import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.CandidateResponse
import com.example.netlib.data.dto.CandidatesData
import com.example.netlib.data.dto.ReportsStatsData
import com.example.netlib.data.dto.sub.AiReviewResponse
import com.example.netlib.data.dto.sub.SubmissionResponse
import com.example.netlib.data.dto.sub.SubmissionResultsResponse
import com.example.netlib.data.dto.sub.SubmissionStatusResponse
import com.example.netlib.data.dto.sub.SubmissionsData
import com.example.netlib.data.dto.sub.VerdictUpdateRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

/**
 * Retrofit-интерфейс с корректными путями относительно baseUrl `/api/`.
 * В net-lib часть эндпоинтов ошибочно начинается с `api/v1/...`, что даёт `/api/api/v1/...`.
 */
interface AppApiService {

    @GET("v1/submissions")
    suspend fun listSubmissions(@Header("Authorization") token: String): ApiResponse<SubmissionsData>

    @Multipart
    @POST("v1/submissions")
    suspend fun createSubmission(
        @Header("Authorization") token: String,
        @Part("assignment_id") assignmentId: RequestBody,
        @Part("git_url") gitUrl: RequestBody?,
        @Part zipFile: MultipartBody.Part?,
    ): ApiResponse<SubmissionResponse>

    @GET("v1/submissions/{submission_id}")
    suspend fun getSubmission(
        @Header("Authorization") token: String,
        @Path("submission_id") submissionId: Int,
    ): ApiResponse<SubmissionResponse>

    @GET("v1/submissions/{submission_id}/status")
    suspend fun getSubmissionStatus(
        @Header("Authorization") token: String,
        @Path("submission_id") submissionId: Int,
    ): ApiResponse<SubmissionStatusResponse>

    @GET("v1/submissions/{submission_id}/results")
    suspend fun getSubmissionResults(
        @Header("Authorization") token: String,
        @Path("submission_id") submissionId: Int,
    ): ApiResponse<SubmissionResultsResponse>

    @POST("v1/submissions/{submission_id}/rerun")
    suspend fun rerunSubmission(
        @Header("Authorization") token: String,
        @Path("submission_id") submissionId: Int,
    ): ApiResponse<SubmissionResponse>

    @PUT("v1/submissions/{submission_id}/verdict")
    suspend fun updateVerdict(
        @Header("Authorization") token: String,
        @Path("submission_id") submissionId: Int,
        @Body verdictUpdate: VerdictUpdateRequest,
    ): ApiResponse<SubmissionResponse>

    @Streaming
    @GET("v1/submissions/{submission_id}/report")
    suspend fun downloadReport(
        @Header("Authorization") token: String,
        @Path("submission_id") submissionId: Int,
    ): ResponseBody

    @GET("v1/submissions/{submission_id}/ai-review")
    suspend fun getAiReview(
        @Header("Authorization") token: String,
        @Path("submission_id") submissionId: Int,
    ): ApiResponse<AiReviewResponse>

    @GET("v1/candidates")
    suspend fun listCandidates(@Header("Authorization") token: String): ApiResponse<CandidatesData>

    @GET("v1/candidates/{candidate_id}")
    suspend fun getCandidate(
        @Header("Authorization") token: String,
        @Path("candidate_id") candidateId: Int,
    ): ApiResponse<CandidateResponse>

    @GET("v1/reports/stats")
    suspend fun getStats(@Header("Authorization") token: String): ApiResponse<ReportsStatsData>
}
