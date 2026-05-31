package com.example.autocheckmobile.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocheckmobile.domain.usecase.assignment.UpdateAssignmentUseCase
import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.GetAssignmentByIdResponse
import com.example.netlib.data.result.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateAssignmentViewModel @Inject constructor(private val useCase: UpdateAssignmentUseCase) : ViewModel() {
    private val _data = MutableStateFlow<NetworkResult<ApiResponse<GetAssignmentByIdResponse>>?>(null)
    val data: StateFlow<NetworkResult<ApiResponse<GetAssignmentByIdResponse>>?> = _data.asStateFlow()

    fun updateAssignment(
        token: String,
        assignmentId: Int,
        title: String? = null,
        description: String? = null,
        checkerWeights: Map<String, Int>? = null
    ) {
        _data.value = null
        viewModelScope.launch {
            when (val result = useCase.invoke(token, assignmentId, title, description, checkerWeights)) {
                is NetworkResult.Success -> _data.value = NetworkResult.Success(result.data)
                is NetworkResult.Error -> _data.value =
                    NetworkResult.Error(result.status, result.message)
                is NetworkResult.Exception -> _data.value =
                    NetworkResult.Exception(result.message)
            }
        }
    }
}