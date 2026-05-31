package com.example.autocheckmobile.presentation.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocheckmobile.data.remote.SubmissionEventClient
import com.example.autocheckmobile.domain.usecase.submission.DownloadReportUseCase
import com.example.autocheckmobile.domain.usecase.submission.GetAiReviewUseCase
import com.example.autocheckmobile.domain.usecase.submission.GetSubmissionResultsUseCase
import com.example.autocheckmobile.domain.usecase.submission.GetSubmissionStatusUseCase
import com.example.autocheckmobile.domain.usecase.submission.GetSubmissionUseCase
import com.example.autocheckmobile.domain.usecase.submission.RerunSubmissionUseCase
import com.example.autocheckmobile.domain.usecase.submission.UpdateVerdictUseCase
import com.example.netlib.data.dto.sub.AiReviewResponse
import com.example.netlib.data.dto.sub.CheckerResult
import com.example.netlib.data.dto.sub.SubmissionResponse
import com.example.netlib.data.result.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class SubmissionDetailsState(
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val submission: SubmissionResponse? = null,
    val results: List<CheckerResult> = emptyList(),
    val aiReview: AiReviewResponse? = null,
    val aiUnavailable: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val reportSavedPath: String? = null,
)

/**
 * Назначение: детали проверки — SSE/polling, rerun, вердикт, отчёт, AI-анализ.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@HiltViewModel
class SubmissionDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSubmissionUseCase: GetSubmissionUseCase,
    private val getResultsUseCase: GetSubmissionResultsUseCase,
    private val getStatusUseCase: GetSubmissionStatusUseCase,
    private val getAiReviewUseCase: GetAiReviewUseCase,
    private val rerunUseCase: RerunSubmissionUseCase,
    private val verdictUseCase: UpdateVerdictUseCase,
    private val downloadReportUseCase: DownloadReportUseCase,
    private val eventClient: SubmissionEventClient,
) : ViewModel() {

    private val _state = MutableStateFlow(SubmissionDetailsState())
    val state: StateFlow<SubmissionDetailsState> = _state.asStateFlow()

    fun load(token: String, submissionId: Int) {
        viewModelScope.launch {
            _state.value = SubmissionDetailsState(isLoading = true)
            fetchDetails(token, submissionId)
            trackStatus(token, submissionId)
        }
    }

    fun loadAiReview(token: String, submissionId: Int) {
        viewModelScope.launch {
            when (val result = getAiReviewUseCase(token, submissionId)) {
                is NetworkResult.Success -> {
                    val review = result.data.data
                    _state.value = _state.value.copy(aiReview = review, aiUnavailable = review == null)
                }
                else -> _state.value = _state.value.copy(aiUnavailable = true)
            }
        }
    }

    fun rerun(token: String, submissionId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isActionLoading = true)
            when (val result = rerunUseCase(token, submissionId)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(isActionLoading = false, successMessage = "Проверка перезапущена")
                    fetchDetails(token, submissionId)
                    trackStatus(token, submissionId)
                }
                is NetworkResult.Error -> _state.value = _state.value.copy(isActionLoading = false, errorMessage = result.message)
                is NetworkResult.Exception -> _state.value = _state.value.copy(isActionLoading = false, errorMessage = result.message)
            }
        }
    }

    fun setVerdict(token: String, submissionId: Int, verdict: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isActionLoading = true)
            when (val result = verdictUseCase(token, submissionId, verdict)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isActionLoading = false,
                        submission = result.data.data,
                        successMessage = "Вердикт сохранён",
                    )
                }
                is NetworkResult.Error -> _state.value = _state.value.copy(isActionLoading = false, errorMessage = result.message)
                is NetworkResult.Exception -> _state.value = _state.value.copy(isActionLoading = false, errorMessage = result.message)
            }
        }
    }

    fun downloadReport(token: String, submissionId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isActionLoading = true, errorMessage = null)
            when (val result = downloadReportUseCase(token, submissionId)) {
                is NetworkResult.Success -> {
                    try {
                        val savedPath = withContext(Dispatchers.IO) {
                            val body = result.data
                            val file = File(context.cacheDir, "submission-$submissionId-report.json")
                            body.byteStream().use { input ->
                                file.outputStream().use { output -> input.copyTo(output) }
                            }
                            file.absolutePath
                        }
                        _state.value = _state.value.copy(
                            isActionLoading = false,
                            reportSavedPath = savedPath,
                            successMessage = "Отчёт сохранён",
                        )
                        Log.i("[SubmissionDetailsViewModel]", "Отчёт сохранён — path=$savedPath")
                    } catch (error: Exception) {
                        Log.e("[SubmissionDetailsViewModel]", "Ошибка сохранения отчёта — ${error.message}")
                        _state.value = _state.value.copy(
                            isActionLoading = false,
                            errorMessage = error.message ?: "Не удалось сохранить отчёт",
                        )
                    }
                }
                is NetworkResult.Error -> _state.value = _state.value.copy(
                    isActionLoading = false,
                    errorMessage = result.message ?: "Ошибка загрузки отчёта",
                )
                is NetworkResult.Exception -> _state.value = _state.value.copy(
                    isActionLoading = false,
                    errorMessage = result.message ?: "Ошибка сети",
                )
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null, reportSavedPath = null)
    }

    private suspend fun trackStatus(token: String, submissionId: Int) {
        val sseOk = eventClient.trackUntilDone(token, submissionId) { status ->
            fetchDetails(token, submissionId)
        }
        if (!sseOk) pollUntilDone(token, submissionId)
        fetchDetails(token, submissionId)
        loadAiReview(token, submissionId)
    }

    private suspend fun fetchDetails(token: String, submissionId: Int) {
        val submission = when (val r = getSubmissionUseCase(token, submissionId)) {
            is NetworkResult.Success -> r.data.data
            is NetworkResult.Error -> {
                if (r.status == 401) _state.value = _state.value.copy(errorMessage = "Сессия истекла")
                null
            }
            else -> null
        }
        val results = when (val r = getResultsUseCase(token, submissionId)) {
            is NetworkResult.Success -> r.data.data.results
            else -> emptyList()
        }
        _state.value = _state.value.copy(isLoading = false, submission = submission, results = results)
    }

    private suspend fun pollUntilDone(token: String, submissionId: Int) {
        repeat(75) {
            val status = when (val r = getStatusUseCase(token, submissionId)) {
                is NetworkResult.Success -> r.data.data.status
                else -> null
            }
            if (status == "done" || status == "error") return
            delay(2000)
            fetchDetails(token, submissionId)
        }
    }
}
