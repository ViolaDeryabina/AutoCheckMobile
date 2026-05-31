package com.example.autocheckmobile.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocheckmobile.domain.usecase.auth.RegisterUseCase
import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.AuthData
import com.example.netlib.data.dto.RegisterRequest
import com.example.netlib.data.result.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(private val useCase: RegisterUseCase) : ViewModel() {
    private val _data = MutableStateFlow<NetworkResult<ApiResponse<AuthData>>?>(null)
    val data: StateFlow<NetworkResult<ApiResponse<AuthData>>?> = _data.asStateFlow()

    fun register(registerRequest: RegisterRequest) {
        _data.value = null
        viewModelScope.launch {
            when (val result = useCase.invoke(registerRequest)) {
                is NetworkResult.Success -> _data.value = NetworkResult.Success(result.data)
                is NetworkResult.Error -> _data.value =
                    NetworkResult.Error(result.status, result.message)

                is NetworkResult.Exception -> _data.value =
                    NetworkResult.Exception(result.message)
            }
        }
    }
}