package com.example.autocheckmobile.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocheckmobile.data.local.SessionStorage
import com.example.autocheckmobile.data.remote.SubmissionEventClient
import com.example.autocheckmobile.domain.usecase.assignment.GetAssignmentsUseCase
import com.example.autocheckmobile.domain.usecase.auth.GetProfileUseCase
import com.example.autocheckmobile.domain.usecase.auth.LoginUseCase
import com.example.autocheckmobile.domain.usecase.auth.LogoutUseCase
import com.example.autocheckmobile.domain.usecase.auth.RegisterUseCase
import com.example.autocheckmobile.domain.usecase.candidate.GetCandidatesUseCase
import com.example.autocheckmobile.domain.usecase.submission.GetSubmissionsUseCase
import com.example.autocheckmobile.domain.GetStatsUseCase
import com.example.netlib.data.dto.AssignmentItem
import com.example.netlib.data.dto.ReportsStatsData
import com.example.netlib.data.dto.RegisterRequest
import com.example.netlib.data.dto.sub.SubmissionItem
import com.example.netlib.data.result.NetworkResult
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
    val toast: ToastEvent? = null,
    val assignments: List<AssignmentItem> = emptyList(),
    val submissions: List<SubmissionItem> = emptyList(),
    val stats: ReportsStatsData? = null,
    val candidateNames: Map<Int, String> = emptyMap(),
    val sessionExpired: Boolean = false,
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
    private val getCandidatesUseCase: GetCandidatesUseCase,
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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = loginUseCase(email, password)) {
                is NetworkResult.Success -> {
                    val auth = result.data.data
                    persistAuth(auth.accessToken, auth.user.id, auth.user.email, auth.user.fullName, auth.user.role)
                    Log.i("[SessionViewModel]", "Вход успешен — userId=${auth.user.id}")
                    loadCollections(auth.accessToken)
                    showToast("Успешный вход")
                }
                is NetworkResult.Error -> handleError(result.status, result.message.orEmpty())
                is NetworkResult.Exception -> handleError(null, result.message.orEmpty().ifBlank { "Ошибка сети" })
            }
        }
    }

    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = registerUseCase(RegisterRequest(fullName, email, password, "candidate"))) {
                is NetworkResult.Success -> {
                    val auth = result.data.data
                    persistAuth(auth.accessToken, auth.user.id, auth.user.email, auth.user.fullName, auth.user.role)
                    Log.i("[SessionViewModel]", "Регистрация успешна — userId=${auth.user.id}")
                    loadCollections(auth.accessToken)
                    showToast("Регистрация успешна")
                }
                is NetworkResult.Error -> handleError(result.status, result.message.orEmpty())
                is NetworkResult.Exception -> handleError(null, result.message.orEmpty().ifBlank { "Ошибка сети" })
            }
        }
    }

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

    fun refreshData() {
        val token = _session.value?.token ?: return
        loadCollections(token)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null, toast = null)
    }

    fun consumeToast() {
        _uiState.value = _uiState.value.copy(toast = null)
    }

    fun clearSessionExpired() {
        _uiState.value = _uiState.value.copy(sessionExpired = false)
    }

    fun candidateName(id: Int): String = _uiState.value.candidateNames[id] ?: "Кандидат #$id"

    fun assignmentTitle(id: Int): String =
        _uiState.value.assignments.find { it.id == id }?.title ?: "Задание #$id"

    private suspend fun restoreSession(token: String) {
        when (val result = getProfileUseCase(token)) {
            is NetworkResult.Success -> {
                val user = result.data.data
                _session.value = UserSession(token, user.id, user.email, user.fullName, user.role)
                loadCollections(token)
            }
            is NetworkResult.Error -> {
                if (result.status == 401) {
                    val hadActiveSession = _session.value != null
                    sessionStorage.clearSession()
                    _session.value = null
                    if (hadActiveSession) {
                        _uiState.value = _uiState.value.copy(sessionExpired = true)
                        showToast("Сессия истекла, войдите снова", isError = true)
                    }
                }
            }
            is NetworkResult.Exception -> Unit
        }
    }

    private suspend fun persistAuth(token: String, userId: Int, email: String, fullName: String, role: String) {
        _session.value = UserSession(token, userId, email, fullName, role)
        sessionStorage.saveSession(token, userId, email, fullName, role)
    }

    private fun loadCollections(token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            var unauthorized = false
            val isExpert = _session.value?.role in setOf("expert", "admin")

            val assignments = when (val r = getAssignmentsUseCase(token)) {
                is NetworkResult.Success -> r.data.data.items
                is NetworkResult.Error -> { if (r.status == 401) unauthorized = true; emptyList() }
                else -> emptyList()
            }
            val submissions = when (val r = getSubmissionsUseCase(token)) {
                is NetworkResult.Success -> r.data.data.items
                is NetworkResult.Error -> { if (r.status == 401) unauthorized = true; emptyList() }
                else -> emptyList()
            }
            val stats = if (isExpert) {
                when (val r = getStatsUseCase(token)) {
                    is NetworkResult.Success -> r.data.data
                    is NetworkResult.Error -> { if (r.status == 401) unauthorized = true; null }
                    else -> null
                }
            } else {
                null
            }
            val candidateNames = if (isExpert) {
                when (val r = getCandidatesUseCase(token)) {
                    is NetworkResult.Success -> r.data.data.items.associate { it.id to it.fullName }
                    else -> emptyMap()
                }
            } else {
                emptyMap()
            }

            if (unauthorized) {
                sessionStorage.clearSession()
                _session.value = null
                _uiState.value = AppUiState(sessionExpired = true, toast = ToastEvent("Сессия истекла", isError = true))
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                assignments = assignments,
                submissions = submissions,
                stats = stats,
                candidateNames = candidateNames,
            )
        }
    }

    private fun showToast(message: String, isError: Boolean = false) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            toast = ToastEvent(message, isError),
            errorMessage = if (isError) message else null,
        )
    }

    private fun handleError(status: Int?, message: String) {
        val mapped = when (status) {
            401 -> "Неверный email или пароль"
            403 -> "Недостаточно прав"
            404 -> "Сервис недоступен (404). Проверьте адрес API"
            422 -> "Ошибка валидации данных"
            in 500..599 -> "Серверная ошибка"
            else -> message.ifBlank { "Ошибка запроса" }
        }
        Log.e("[SessionViewModel]", "Ошибка — $mapped")
        showToast(mapped, isError = true)
    }
}
