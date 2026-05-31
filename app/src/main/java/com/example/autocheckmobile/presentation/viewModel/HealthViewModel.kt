package com.example.autocheckmobile.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocheckmobile.domain.HealthCheckUseCase
import com.example.netlib.data.dto.HealthResponse
import com.example.netlib.data.result.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(private val useCase: HealthCheckUseCase) : ViewModel() {
    private val _data = MutableStateFlow<NetworkResult<HealthResponse>?>(null)
    val data: StateFlow<NetworkResult<HealthResponse>?> = _data.asStateFlow()

    fun healthCheck() {
        _data.value = null
        viewModelScope.launch {
            when (val result = useCase.invoke()) {
                is NetworkResult.Success -> _data.value = NetworkResult.Success(result.data)
                is NetworkResult.Error -> _data.value =
                    NetworkResult.Error(result.status, result.message)

                is NetworkResult.Exception -> _data.value =
                    NetworkResult.Exception(result.message)
            }
        }
    }
}