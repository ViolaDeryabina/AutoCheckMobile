package com.example.autocheckmobile.presentation.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocheckmobile.domain.usecase.submission.CreateSubmissionWithGitUseCase
import com.example.autocheckmobile.domain.usecase.submission.CreateSubmissionWithZipUseCase
import com.example.netlib.data.result.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class UploadUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successSubmissionId: Int? = null,
)

/**
 * Назначение: загрузка решения кандидата (ZIP или Git URL) на сервер.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@HiltViewModel
class UploadViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val zipUseCase: CreateSubmissionWithZipUseCase,
    private val gitUseCase: CreateSubmissionWithGitUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UploadUiState())
    val state: StateFlow<UploadUiState> = _state.asStateFlow()

    /**
     * Отправляет ZIP-архив решения на проверку.
     */
    fun submitZip(token: String, assignmentId: Int, uri: Uri) {
        viewModelScope.launch {
            _state.value = UploadUiState(isLoading = true)
            Log.i("[UploadViewModel]", "Загрузка ZIP — assignmentId=$assignmentId")
            val file = copyUriToCache(uri) ?: run {
                _state.value = UploadUiState(errorMessage = "Не удалось прочитать файл")
                return@launch
            }
            if (file.length() > 50L * 1024 * 1024) {
                _state.value = UploadUiState(errorMessage = "Файл превышает 50 МБ")
                return@launch
            }
            when (val result = zipUseCase(token, assignmentId, file)) {
                is NetworkResult.Success -> {
                    val id = result.data.data?.id
                    _state.value = UploadUiState(successSubmissionId = id)
                    Log.i("[UploadViewModel]", "Успех — submissionId=$id")
                }
                is NetworkResult.Error -> _state.value = UploadUiState(errorMessage = result.message)
                is NetworkResult.Exception -> _state.value = UploadUiState(errorMessage = result.message)
            }
        }
    }

    /**
     * Отправляет Git URL решения на проверку.
     */
    fun submitGit(token: String, assignmentId: Int, gitUrl: String) {
        viewModelScope.launch {
            _state.value = UploadUiState(isLoading = true)
            when (val result = gitUseCase(token, assignmentId, gitUrl)) {
                is NetworkResult.Success -> {
                    _state.value = UploadUiState(successSubmissionId = result.data.data?.id)
                }
                is NetworkResult.Error -> _state.value = UploadUiState(errorMessage = result.message)
                is NetworkResult.Exception -> _state.value = UploadUiState(errorMessage = result.message)
            }
        }
    }

    fun resetState() {
        _state.value = UploadUiState()
    }

    private suspend fun copyUriToCache(uri: Uri): File? = withContext(Dispatchers.IO) {
        runCatching {
            val out = File(context.cacheDir, "submission_${System.currentTimeMillis()}.zip")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(out).use { output -> input.copyTo(output) }
            }
            out
        }.getOrNull()
    }
}
