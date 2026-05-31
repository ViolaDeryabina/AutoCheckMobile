package com.example.autocheckmobile.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocheckmobile.domain.usecase.submission.CreateSubmissionWithZipUseCase
import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.sub.SubmissionResponse
import com.example.netlib.data.result.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateSubmissionZipViewModel @Inject constructor(private val useCase: CreateSubmissionWithZipUseCase) :
    ViewModel() {
    private val _data = MutableStateFlow<NetworkResult<ApiResponse<SubmissionResponse>>?>(null)
    val data: StateFlow<NetworkResult<ApiResponse<SubmissionResponse>>?> = _data.asStateFlow()

    fun createSubmission(token: String, assignmentId: Int, zipFile: File) {
        _data.value = null
        viewModelScope.launch {
            when (val result = useCase.invoke(token, assignmentId, zipFile)) {
                is NetworkResult.Success -> _data.value = NetworkResult.Success(result.data)
                is NetworkResult.Error -> _data.value =
                    NetworkResult.Error(result.status, result.message)

                is NetworkResult.Exception -> _data.value =
                    NetworkResult.Exception(result.message)
            }
        }
    }
}