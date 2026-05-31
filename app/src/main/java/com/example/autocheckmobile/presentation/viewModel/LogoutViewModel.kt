package com.example.autocheckmobile.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocheckmobile.domain.usecase.auth.LogoutUseCase
import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.LogoutData
import com.example.netlib.data.result.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(private val useCase: LogoutUseCase) : ViewModel() {
    private val _data = MutableStateFlow<NetworkResult<ApiResponse<LogoutData>>?>(null)
    val data: StateFlow<NetworkResult<ApiResponse<LogoutData>>?> = _data.asStateFlow()

    fun logout(token: String) {
        _data.value = null
        viewModelScope.launch {
            when (val result = useCase.invoke(token)) {
                is NetworkResult.Success -> _data.value = NetworkResult.Success(result.data)
                is NetworkResult.Error -> _data.value =
                    NetworkResult.Error(result.status, result.message)

                is NetworkResult.Exception -> _data.value =
                    NetworkResult.Exception(result.message)
            }
        }
    }
}