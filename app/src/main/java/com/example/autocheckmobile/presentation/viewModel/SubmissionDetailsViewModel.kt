package com.example.autocheckmobile.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocheckmobile.domain.usecase.submission.GetAiReviewUseCase
import com.example.autocheckmobile.domain.usecase.submission.GetSubmissionResultsUseCase
import com.example.autocheckmobile.domain.usecase.submission.GetSubmissionStatusUseCase
import com.example.autocheckmobile.domain.usecase.submission.GetSubmissionUseCase
import com.example.netlib.data.dto.sub.AiReviewResponse
import com.example.netlib.data.dto.sub.CheckerResult
import com.example.netlib.data.dto.sub.SubmissionResponse
import com.example.netlib.data.result.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubmissionDetailsState(
    val isLoading: Boolean = false,
    val submission: SubmissionResponse? = null,
    val results: List<CheckerResult> = emptyList(),
    val aiReview: AiReviewResponse? = null,
    val aiUnavailable: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * Назначение: загрузка деталей проверки, результатов чекеров и AI-анализа с polling статуса.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@HiltViewModel
class SubmissionDetailsViewModel @Inject constructor(
    private val getSubmissionUseCase: GetSubmissionUseCase,
    private val getResultsUseCase: GetSubmissionResultsUseCase,
    private val getStatusUseCase: GetSubmissionStatusUseCase,
    private val getAiReviewUseCase: GetAiReviewUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SubmissionDetailsState())
    val state: StateFlow<SubmissionDetailsState> = _state.asStateFlow()

    /**
     * Загружает проверку и запускает отслеживание статуса в реальном времени (polling).
     */
    fun load(token: String, submissionId: Int) {
        viewModelScope.launch {
            _state.value = SubmissionDetailsState(isLoading = true)
            fetchDetails(token, submissionId)
            pollUntilDone(token, submissionId)
        }
    }

    /**
     * Загружает AI-анализ для текущей проверки.
     */
    fun loadAiReview(token: String, submissionId: Int) {
        viewModelScope.launch {
            when (val result = getAiReviewUseCase(token, submissionId)) {
                is NetworkResult.Success -> {
                    val review = result.data.data
                    _state.value = _state.value.copy(
                        aiReview = review,
                        aiUnavailable = review == null,
                    )
                }
                else -> _state.value = _state.value.copy(aiUnavailable = true)
            }
        }
    }

    private suspend fun fetchDetails(token: String, submissionId: Int) {
        val submission = when (val r = getSubmissionUseCase(token, submissionId)) {
            is NetworkResult.Success -> r.data.data
            else -> null
        }
        val results = when (val r = getResultsUseCase(token, submissionId)) {
            is NetworkResult.Success -> r.data.data?.results.orEmpty()
            else -> emptyList()
        }
        _state.value = _state.value.copy(
            isLoading = false,
            submission = submission,
            results = results,
        )
        Log.i("[SubmissionDetailsViewModel]", "Загружено — submissionId=$submissionId results=${results.size}")
    }

    private suspend fun pollUntilDone(token: String, submissionId: Int) {
        repeat(75) {
            val status = when (val r = getStatusUseCase(token, submissionId)) {
                is NetworkResult.Success -> r.data.data?.status
                else -> null
            }
            if (status == "done" || status == "error") {
                fetchDetails(token, submissionId)
                loadAiReview(token, submissionId)
                return
            }
            delay(2000)
            fetchDetails(token, submissionId)
        }
    }
}
