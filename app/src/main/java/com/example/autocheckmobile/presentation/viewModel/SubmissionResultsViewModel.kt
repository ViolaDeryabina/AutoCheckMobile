package com.example.autocheckmobile.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocheckmobile.domain.usecase.submission.GetSubmissionResultsUseCase
import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.sub.SubmissionResultsResponse
import com.example.netlib.data.result.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubmissionResultsViewModel @Inject constructor(private val useCase: GetSubmissionResultsUseCase) :
    ViewModel() {
    private val _data =
        MutableStateFlow<NetworkResult<ApiResponse<SubmissionResultsResponse>>?>(null)
    val data: StateFlow<NetworkResult<ApiResponse<SubmissionResultsResponse>>?> =
        _data.asStateFlow()

    fun getSubmissionResults(token: String, submissionId: Int) {
        _data.value = null
        viewModelScope.launch {
            when (val result = useCase.invoke(token, submissionId)) {
                is NetworkResult.Success -> _data.value = NetworkResult.Success(result.data)
                is NetworkResult.Error -> _data.value =
                    NetworkResult.Error(result.status, result.message)

                is NetworkResult.Exception -> _data.value =
                    NetworkResult.Exception(result.message)
            }
        }
    }
}