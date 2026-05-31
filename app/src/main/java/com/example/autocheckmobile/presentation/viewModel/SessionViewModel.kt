package com.example.autocheckmobile.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocheckmobile.data.local.SessionStorage
import com.example.autocheckmobile.domain.usecase.assignment.GetAssignmentsUseCase
import com.example.autocheckmobile.domain.usecase.auth.GetProfileUseCase
import com.example.autocheckmobile.domain.usecase.auth.LoginUseCase
import com.example.autocheckmobile.domain.usecase.auth.LogoutUseCase
import com.example.autocheckmobile.domain.usecase.auth.RegisterUseCase
import com.example.autocheckmobile.domain.usecase.submission.GetSubmissionsUseCase
import com.example.netlib.data.dto.AssignmentItem
import com.example.netlib.data.dto.ReportsStatsData
import com.example.netlib.data.dto.sub.SubmissionItem
import com.example.netlib.data.dto.RegisterRequest
import com.example.netlib.data.result.NetworkResult
import com.example.autocheckmobile.domain.GetStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserSession(
    val token: String,
    val userId: Int,
    val email: String,
    val fullName: String,
    val role: String,
)

data class AppUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val assignments: List<AssignmentItem> = emptyList(),
    val submissions: List<SubmissionItem> = emptyList(),
    val stats: ReportsStatsData? = null,
)

/**
 * Назначение: глобальное состояние сессии, авторизации и коллекций данных приложения.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getAssignmentsUseCase: GetAssignmentsUseCase,
    private val getSubmissionsUseCase: GetSubmissionsUseCase,
    private val getStatsUseCase: GetStatsUseCase,
) : ViewModel() {

    private val _session = MutableStateFlow<UserSession?>(null)
    val session: StateFlow<UserSession?> = _session.asStateFlow()

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    val storedToken = sessionStorage.tokenFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null,
    )

    init {
        viewModelScope.launch {
            sessionStorage.tokenFlow.collect { token ->
                if (token != null && _session.value?.token != token) {
                    restoreSession(token)
                }
            }
        }
    }

    /**
     * Выполняет вход по email и паролю, сохраняет JWT-токен.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = loginUseCase(email, password)) {
                is NetworkResult.Success -> {
                    val auth = result.data.data ?: run {
                        setError("Пустой ответ сервера")
                        return@launch
                    }
                    persistAuth(auth.accessToken, auth.user.id, auth.user.email, auth.user.fullName, auth.user.role)
                    Log.i("[SessionViewModel]", "Вход успешен — userId=${auth.user.id}")
                    loadCollections(auth.accessToken)
                    _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Успешный вход")
                }
                is NetworkResult.Error -> setError(mapError(result.status, result.message.orEmpty()))
                is NetworkResult.Exception -> setError(result.message.orEmpty().ifBlank { "Ошибка сети" })
            }
        }
    }

    /**
     * Регистрирует нового кандидата и выполняет автоматический вход.
     */
    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = registerUseCase(RegisterRequest(fullName, email, password, "candidate"))) {
                is NetworkResult.Success -> {
                    login(email, password)
                }
                is NetworkResult.Error -> setError(mapError(result.status, result.message.orEmpty()))
                is NetworkResult.Exception -> setError(result.message.orEmpty().ifBlank { "Ошибка сети" })
            }
        }
    }

    /**
     * Выход из системы с очисткой локальной сессии.
     */
    fun logout() {
        val token = _session.value?.token ?: return
        viewModelScope.launch {
            logoutUseCase(token)
            sessionStorage.clearSession()
            _session.value = null
            _uiState.value = AppUiState()
            Log.i("[SessionViewModel]", "Выход — сессия очищена")
        }
    }

    /**
     * Загружает задания, проверки и статистику для текущего пользователя.
     */
    fun refreshData() {
        val token = _session.value?.token ?: return
        loadCollections(token)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun assignmentTitle(id: Int): String =
        _uiState.value.assignments.find { it.id == id }?.title ?: "Задание #$id"

    private suspend fun restoreSession(token: String) {
        when (val result = getProfileUseCase(token)) {
            is NetworkResult.Success -> {
                val user = result.data.data ?: return
                _session.value = UserSession(token, user.id, user.email, user.fullName, user.role)
                loadCollections(token)
            }
            is NetworkResult.Error -> {
                if (result.status == 401) sessionStorage.clearSession()
            }
            is NetworkResult.Exception -> Unit
        }
    }

    private suspend fun persistAuth(token: String, userId: Int, email: String, fullName: String, role: String) {
        sessionStorage.saveSession(token, userId, email, fullName, role)
        _session.value = UserSession(token, userId, email, fullName, role)
    }

    private fun loadCollections(token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val assignments = when (val r = getAssignmentsUseCase(token)) {
                is NetworkResult.Success -> r.data.data?.items.orEmpty()
                else -> emptyList()
            }
            val submissions = when (val r = getSubmissionsUseCase(token)) {
                is NetworkResult.Success -> r.data.data?.items.orEmpty()
                else -> emptyList()
            }
            val stats = when (val r = getStatsUseCase(token)) {
                is NetworkResult.Success -> r.data.data
                else -> null
            }
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                assignments = assignments,
                submissions = submissions,
                stats = stats,
            )
        }
    }

    private fun setError(message: String) {
        Log.e("[SessionViewModel]", "Ошибка — $message")
        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
    }

    private fun mapError(status: Int, message: String): String = when (status) {
        401 -> "Неверный email или пароль"
        403 -> "Недостаточно прав"
        422 -> "Ошибка валидации данных"
        in 500..599 -> "Серверная ошибка"
        else -> message
    }
}
