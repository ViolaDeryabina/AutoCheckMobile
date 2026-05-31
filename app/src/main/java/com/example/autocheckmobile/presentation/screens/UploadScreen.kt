package com.example.autocheckmobile.presentation.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.autocheckmobile.presentation.components.FilterChip
import com.example.autocheckmobile.presentation.components.FormInput
import com.example.autocheckmobile.presentation.components.PrimaryButton
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import com.example.autocheckmobile.presentation.theme.Dimens
import com.example.autocheckmobile.presentation.theme.Space12H
import com.example.autocheckmobile.presentation.theme.Space16H
import com.example.netlib.data.dto.AssignmentItem

/**
 * Назначение: экран отправки решения — выбор задания и ZIP-файла или Git URL.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    assignments: List<AssignmentItem>,
    isLoading: Boolean,
    onSubmitZip: (assignmentId: Int, uri: Uri) -> Unit,
    onSubmitGit: (assignmentId: Int, gitUrl: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var mode by rememberSaveable { mutableStateOf("zip") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedAssignment by rememberSaveable { mutableStateOf<AssignmentItem?>(assignments.firstOrNull()) }
    var gitUrl by rememberSaveable { mutableStateOf("") }
    var zipName by rememberSaveable { mutableStateOf<String?>(null) }
    var zipUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    Log.i("[UploadScreen]", "Отрисовка — assignments=${assignments.size}")

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            zipUri = uri
            zipName = uri.lastPathSegment ?: "solution.zip"
        }
    }

    val gitValid = gitUrl.startsWith("https://", ignoreCase = true)
    val zipValid = zipUri != null
    val canSubmit = selectedAssignment != null && ((mode == "git" && gitValid) || (mode == "zip" && zipValid))

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.space16),
    ) {
        Text(
            text = "Отправить задание",
            color = DesignTokens.TextPrimary,
            style = CustomTheme.typography.geistSemiBold32,
        )
        Space12H()
        Text(
            text = "Выберите задание и загрузите ZIP-архив или Git URL",
            color = DesignTokens.TextMuted,
            style = CustomTheme.typography.geistNormal16,
        )
        Space16H()

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selectedAssignment?.title ?: "Выберите задание",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DesignTokens.TextPrimary,
                    unfocusedTextColor = DesignTokens.TextPrimary,
                    focusedContainerColor = DesignTokens.Card,
                    unfocusedContainerColor = DesignTokens.Card,
                ),
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                assignments.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.title) },
                        onClick = {
                            selectedAssignment = item
                            expanded = false
                        },
                    )
                }
            }
        }

        Space16H()
        Row {
            FilterChip(label = "ZIP-файл", selected = mode == "zip", onClick = { mode = "zip" })
            FilterChip(
                label = "Git URL",
                selected = mode == "git",
                onClick = { mode = "git" },
                modifier = Modifier.padding(start = Dimens.space8),
            )
        }

        Space16H()
        if (mode == "zip") {
            PrimaryButton(
                text = zipName ?: "Выбрать ZIP-файл",
                onClick = { picker.launch("application/zip") },
            )
            Space12H()
            Text(
                text = "Допустим только .zip до 50 МБ",
                color = DesignTokens.TextMuted,
                style = CustomTheme.typography.geistNormal10,
            )
        } else {
            FormInput(
                label = "Git URL",
                value = gitUrl,
                onValueChange = { gitUrl = it },
                placeholder = "https://github.com/user/repo",
                error = if (gitUrl.isNotEmpty() && !gitValid) "Формат: https://..." else null,
            )
        }

        Space16H()
        PrimaryButton(
            text = "Отправить на проверку",
            onClick = {
                val assignmentId = selectedAssignment?.id ?: return@PrimaryButton
                if (mode == "git") {
                    onSubmitGit(assignmentId, gitUrl.trim())
                } else {
                    val uri = zipUri ?: return@PrimaryButton
                    onSubmitZip(assignmentId, uri)
                }
            },
            enabled = canSubmit,
            loading = isLoading,
        )
    }
}
