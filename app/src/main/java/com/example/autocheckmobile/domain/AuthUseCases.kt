package com.example.autocheckmobile.domain.usecase.auth

import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.AuthData
import com.example.netlib.data.dto.AuthRequest
import com.example.netlib.data.dto.LogoutData
import com.example.netlib.data.dto.RegisterRequest
import com.example.netlib.data.dto.User
import com.example.netlib.data.result.NetworkResult
import com.example.netlib.domain.repository.AuthenticationRepository
import javax.inject.Inject

// Регистрация
class RegisterUseCase @Inject constructor(
    private val repository: AuthenticationRepository
) {
    suspend operator fun invoke(request: RegisterRequest): NetworkResult<ApiResponse<AuthData>> {
        return repository.register(request)
    }
}

// Авторизация
class LoginUseCase @Inject constructor(
    private val repository: AuthenticationRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): NetworkResult<ApiResponse<AuthData>> {
        val request = AuthRequest(email = email, password = password)
        return repository.authorization(request)
    }
}

// Выход из системы
class LogoutUseCase @Inject constructor(
    private val repository: AuthenticationRepository
) {
    suspend operator fun invoke(token: String): NetworkResult<ApiResponse<LogoutData>> {
        return repository.logOut(token)
    }
}

// Получение профиля
class GetProfileUseCase @Inject constructor(
    private val repository: AuthenticationRepository
) {
    suspend operator fun invoke(token: String): NetworkResult<ApiResponse<User>> {
        return repository.getProfile(token)
    }
}