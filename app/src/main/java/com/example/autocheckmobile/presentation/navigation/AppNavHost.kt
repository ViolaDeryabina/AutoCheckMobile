package com.example.autocheckmobile.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.autocheckmobile.R
import com.example.autocheckmobile.presentation.components.BottomBar
import com.example.autocheckmobile.presentation.components.BottomBarData
import com.example.autocheckmobile.presentation.components.HeaderAvatar
import com.example.autocheckmobile.presentation.screens.AuthScreen
import com.example.autocheckmobile.presentation.screens.DashBoard
import com.example.autocheckmobile.presentation.screens.MySubmissionsScreen
import com.example.autocheckmobile.presentation.screens.ReportTest
import com.example.autocheckmobile.presentation.screens.SettingsScreen
import com.example.autocheckmobile.presentation.screens.TestCandidates
import com.example.autocheckmobile.presentation.screens.UploadScreen
import com.example.autocheckmobile.presentation.theme.DarkBlue
import com.example.autocheckmobile.presentation.viewModel.AppUiState
import com.example.autocheckmobile.presentation.viewModel.SessionViewModel
import com.example.autocheckmobile.presentation.viewModel.SubmissionDetailsViewModel
import com.example.autocheckmobile.presentation.viewModel.UploadViewModel
import com.example.autocheckmobile.presentation.viewModel.UserSession

/**
 * Назначение: корневой граф навигации приложения.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    sessionViewModel: SessionViewModel = hiltViewModel(),
) {
    val session by sessionViewModel.session.collectAsStateWithLifecycle()
    val uiState by sessionViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = AppRoutes.AUTH) {
        composable(AppRoutes.AUTH) {
            if (session != null) {
                LaunchedEffect(session) {
                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(AppRoutes.AUTH) { inclusive = true }
                    }
                }
            } else {
                AuthScreen(
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    onLogin = sessionViewModel::login,
                    onRegister = sessionViewModel::register,
                )
            }
        }

        composable(AppRoutes.MAIN) {
            val currentSession = session ?: return@composable
            MainShell(
                session = currentSession,
                uiState = uiState,
                assignmentTitle = sessionViewModel::assignmentTitle,
                onLogout = {
                    sessionViewModel.logout()
                    navController.navigate(AppRoutes.AUTH) {
                        popUpTo(AppRoutes.MAIN) { inclusive = true }
                    }
                },
                onOpenSubmission = { id ->
                    navController.navigate(AppRoutes.submissionDetails(id))
                },
                onRefresh = sessionViewModel::refreshData,
            )
        }

        composable(AppRoutes.SUBMISSION_DETAILS) { entry ->
            val submissionId = entry.arguments?.getString("submissionId")?.toIntOrNull() ?: return@composable
            val token = session?.token ?: return@composable
            val detailsViewModel: SubmissionDetailsViewModel = hiltViewModel()
            val detailsState by detailsViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(submissionId) {
                detailsViewModel.load(token, submissionId)
            }

            Scaffold(
                topBar = { HeaderAvatar(modifier = Modifier) },
                containerColor = DarkBlue,
            ) { padding ->
                ReportTest(
                    state = detailsState,
                    assignmentTitle = sessionViewModel.assignmentTitle(
                        detailsState.submission?.assignmentId ?: 0,
                    ),
                    onLoadAiReview = { detailsViewModel.loadAiReview(token, submissionId) },
                    onBack = { navController.popBackStack() },
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun MainShell(
    session: UserSession,
    uiState: AppUiState,
    assignmentTitle: (Int) -> String,
    onLogout: () -> Unit,
    onOpenSubmission: (Int) -> Unit,
    onRefresh: () -> Unit,
    uploadViewModel: UploadViewModel = hiltViewModel(),
) {
    val isCandidate = session.role == "candidate"
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val uploadState by uploadViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(uploadState.successSubmissionId) {
        if (uploadState.successSubmissionId != null) {
            uploadViewModel.resetState()
            selectedTab = 0
            onRefresh()
        }
    }

    val tabs = if (isCandidate) {
        listOf("Мои задания", "Отправить", "Настройки")
    } else {
        listOf("Дашборд", "Тесты", "Настройки")
    }

    Scaffold(
        topBar = { HeaderAvatar(modifier = Modifier) },
        bottomBar = {
            BottomBar(
                data = listOf(
                    BottomBarData(R.drawable.dashboard, tabs[0]) { selectedTab = 0 },
                    BottomBarData(R.drawable.file, tabs[1]) { selectedTab = 1 },
                    BottomBarData(R.drawable.search, tabs[2]) { selectedTab = 2 },
                ),
                selected = selectedTab,
            )
        },
        containerColor = DarkBlue,
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                isCandidate && selectedTab == 0 -> {
                    MySubmissionsScreen(
                        submissions = uiState.submissions,
                        assignmentTitle = assignmentTitle,
                        onSelectSubmission = { onOpenSubmission(it.id) },
                        onUploadClick = { selectedTab = 1 },
                    )
                }
                isCandidate && selectedTab == 1 -> {
                    UploadScreen(
                        assignments = uiState.assignments,
                        isLoading = uploadState.isLoading,
                        onSubmitZip = { assignmentId, uri ->
                            uploadViewModel.submitZip(session.token, assignmentId, uri)
                        },
                        onSubmitGit = { assignmentId, url ->
                            uploadViewModel.submitGit(session.token, assignmentId, url)
                        },
                    )
                }
                !isCandidate && selectedTab == 0 -> {
                    DashBoard(
                        userName = session.fullName,
                        stats = uiState.stats,
                        submissions = uiState.submissions,
                    )
                }
                !isCandidate && selectedTab == 1 -> {
                    TestCandidates(
                        submissions = uiState.submissions,
                        assignmentTitle = assignmentTitle,
                        onSelectSubmission = { onOpenSubmission(it.id) },
                    )
                }
                selectedTab == 2 -> SettingsScreen(session = session, onLogout = onLogout)
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.i("[MainShell]", "Старт — role=${session.role}")
        onRefresh()
    }
}
