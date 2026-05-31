package com.example.autocheckmobile.domain.usecase.assignment

import com.example.netlib.data.dto.ApiResponse
import com.example.netlib.data.dto.AssignmentsData
import com.example.netlib.data.dto.CreateAssignmentRequest
import com.example.netlib.data.dto.DeleteAssignmentResponse
import com.example.netlib.data.dto.GetAssignmentByIdResponse
import com.example.netlib.data.result.NetworkResult
import com.example.netlib.domain.repository.AssignmentsRepository
import javax.inject.Inject

// Получение списка заданий
class GetAssignmentsUseCase @Inject constructor(
    private val repository: AssignmentsRepository
) {
    suspend operator fun invoke(token: String): NetworkResult<ApiResponse<AssignmentsData>> {
        return repository.getAssignments(token)
    }
}

// Создание задания
class CreateAssignmentUseCase @Inject constructor(
    private val repository: AssignmentsRepository
) {
    suspend operator fun invoke(
        token: String,
        title: String,
        description: String,
        checkerWeights: Map<String, Int>
    ): NetworkResult<ApiResponse<GetAssignmentByIdResponse>> {
        val request = CreateAssignmentRequest(
            title = title,
            description = description,
            checkerWeights = checkerWeights
        )
        return repository.createAssignment(token, request)
    }
}

// Получение задания по ID
class GetAssignmentByIdUseCase @Inject constructor(
    private val repository: AssignmentsRepository
) {
    suspend operator fun invoke(
        token: String,
        assignmentId: Int
    ): NetworkResult<ApiResponse<GetAssignmentByIdResponse>> {
        return repository.getAssignmentById(token, assignmentId)
    }
}

// Обновление задания
class UpdateAssignmentUseCase @Inject constructor(
    private val repository: AssignmentsRepository
) {
    suspend operator fun invoke(
        token: String,
        assignmentId: Int,
        title: String? = null,
        description: String? = null,
        checkerWeights: Map<String, Int>? = null
    ): NetworkResult<ApiResponse<GetAssignmentByIdResponse>> {
        val request = CreateAssignmentRequest(
            title = title ?: "",
            description = description ?: "",
            checkerWeights = checkerWeights ?: emptyMap()
        )
        return repository.updateAssignmentById(token, assignmentId, request)
    }
}

// Удаление задания
class DeleteAssignmentUseCase @Inject constructor(
    private val repository: AssignmentsRepository
) {
    suspend operator fun invoke(
        token: String,
        assignmentId: Int
    ): NetworkResult<ApiResponse<DeleteAssignmentResponse>> {
        return repository.deleteAssignmentById(token, assignmentId)
    }
}