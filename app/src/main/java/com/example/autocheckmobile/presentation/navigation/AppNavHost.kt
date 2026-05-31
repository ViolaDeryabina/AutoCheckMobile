package com.example.autocheckmobile.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.autocheckmobile.presentation.components.ToastHost
import com.example.autocheckmobile.presentation.screens.AuthScreen
import com.example.autocheckmobile.presentation.screens.DashBoard
import com.example.autocheckmobile.presentation.screens.MySubmissionsScreen
import com.example.autocheckmobile.presentation.screens.ReportTest
import com.example.autocheckmobile.presentation.screens.SettingsScreen
import com.example.autocheckmobile.presentation.screens.StatsScreen
import com.example.autocheckmobile.presentation.screens.TestCandidates
import com.example.autocheckmobile.presentation.screens.UploadScreen
import com.example.autocheckmobile.presentation.theme.DarkBlue
import com.example.autocheckmobile.presentation.theme.DesignTokens
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
    val snackbarHostState = remember { SnackbarHostState() }

    ToastHost(
        toast = uiState.toast,
        snackbarHostState = snackbarHostState,
        onConsumed = sessionViewModel::consumeToast,
    )

    LaunchedEffect(uiState.sessionExpired) {
        if (uiState.sessionExpired) {
            navController.navigate(AppRoutes.AUTH) {
                popUpTo(0) { inclusive = true }
            }
            sessionViewModel.clearSessionExpired()
        }
    }

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
                snackbarHostState = snackbarHostState,
                candidateName = sessionViewModel::candidateName,
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
                onShowToast = { message, isError ->
                    if (isError) snackbarHostState.showSnackbar(message) else snackbarHostState.showSnackbar(message)
                },
            )
        }

        composable(AppRoutes.SUBMISSION_DETAILS) { entry ->
            val submissionId = entry.arguments?.getString("submissionId")?.toIntOrNull() ?: return@composable
            val currentSession = session ?: return@composable
            val detailsViewModel: SubmissionDetailsViewModel = hiltViewModel()
            val detailsState by detailsViewModel.state.collectAsStateWithLifecycle()
            val canSetVerdict = currentSession.role == "expert" || currentSession.role == "admin"

            LaunchedEffect(submissionId) {
                detailsViewModel.load(currentSession.token, submissionId)
            }

            LaunchedEffect(detailsState.successMessage) {
                detailsState.successMessage?.let {
                    snackbarHostState.showSnackbar(it)
                    detailsViewModel.clearMessages()
                }
            }
            LaunchedEffect(detailsState.errorMessage) {
                detailsState.errorMessage?.let {
                    snackbarHostState.showSnackbar(it)
                    detailsViewModel.clearMessages()
                }
            }

            Scaffold(
                topBar = { HeaderAvatar(modifier = Modifier) },
                containerColor = DarkBlue,
                snackbarHost = { ToastHost(null, snackbarHostState, {}) },
            ) { padding ->
                ReportTest(
                    state = detailsState,
                    assignmentTitle = sessionViewModel.assignmentTitle(
                        detailsState.submission?.assignmentId ?: 0,
                    ),
                    candidateLabel = sessionViewModel.candidateName(
                        detailsState.submission?.candidateId ?: 0,
                    ),
                    canSetVerdict = canSetVerdict,
                    onLoadAiReview = { detailsViewModel.loadAiReview(currentSession.token, submissionId) },
                    onRerun = { detailsViewModel.rerun(currentSession.token, submissionId) },
                    onDownloadReport = { detailsViewModel.downloadReport(currentSession.token, submissionId) },
                    onApprove = { detailsViewModel.setVerdict(currentSession.token, submissionId, "approved") },
                    onReject = { detailsViewModel.setVerdict(currentSession.token, submissionId, "rejected") },
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
    snackbarHostState: SnackbarHostState,
    candidateName: (Int) -> String,
    assignmentTitle: (Int) -> String,
    onLogout: () -> Unit,
    onOpenSubmission: (Int) -> Unit,
    onRefresh: () -> Unit,
    onShowToast: suspend (String, Boolean) -> Unit,
    uploadViewModel: UploadViewModel = hiltViewModel(),
) {
    val isCandidate = session.role == "candidate"
    val isExpert = !isCandidate
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showStats by remember { mutableStateOf(false) }
    val uploadState by uploadViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(uploadState.successSubmissionId) {
        if (uploadState.successSubmissionId != null && !uploadState.isLoading) {
            onShowToast("Решение отправлено · статус: ${uploadState.trackingStatus ?: "pending"}", false)
            uploadViewModel.resetState()
            selectedTab = 0
            onRefresh()
        }
    }

    LaunchedEffect(uploadState.errorMessage) {
        uploadState.errorMessage?.let {
            onShowToast(it, true)
            uploadViewModel.resetState()
        }
    }

    val tabs = if (isCandidate) {
        listOf("Мои задания", "Отправить", "Настройки")
    } else {
        listOf("Дашборд", "Тесты", "Настройки")
    }

    Scaffold(
        topBar = { HeaderAvatar(modifier = Modifier, onClickSearch = { if (isExpert) selectedTab = 1 }) },
        bottomBar = {
            if (!showStats) {
                BottomBar(
                    data = listOf(
                        BottomBarData(R.drawable.dashboard, tabs[0]) { selectedTab = 0; showStats = false },
                        BottomBarData(R.drawable.file, tabs[1]) { selectedTab = 1; showStats = false },
                        BottomBarData(R.drawable.search, tabs[2]) { selectedTab = 2; showStats = false },
                    ),
                    selected = selectedTab,
                )
            }
        },
        floatingActionButton = {
            if (!showStats) {
                FloatingActionButton(
                    onClick = {
                        if (isCandidate) selectedTab = 1 else {
                            selectedTab = 0
                            onRefresh()
                        }
                    },
                    containerColor = DesignTokens.Primary,
                    contentColor = Color.White,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Действие")
                }
            }
        },
        containerColor = DarkBlue,
        snackbarHost = { ToastHost(uiState.toast, snackbarHostState, {}) },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                showStats && isExpert -> StatsScreen(
                    stats = uiState.stats,
                    submissions = uiState.submissions,
                    onBack = { showStats = false },
                )
                isCandidate && selectedTab == 0 -> MySubmissionsScreen(
                    submissions = uiState.submissions,
                    assignmentTitle = assignmentTitle,
                    onSelectSubmission = { onOpenSubmission(it.id) },
                    onUploadClick = { selectedTab = 1 },
                )
                isCandidate && selectedTab == 1 -> UploadScreen(
                    assignments = uiState.assignments,
                    isLoading = uploadState.isLoading,
                    trackingStatus = uploadState.trackingStatus,
                    onSubmitZip = { assignmentId, uri ->
                        uploadViewModel.submitZip(session.token, assignmentId, uri)
                    },
                    onSubmitGit = { assignmentId, url ->
                        uploadViewModel.submitGit(session.token, assignmentId, url)
                    },
                )
                isExpert && selectedTab == 0 -> DashBoard(
                    userName = session.fullName,
                    stats = uiState.stats,
                    submissions = uiState.submissions,
                    candidateName = candidateName,
                    onOpenSubmission = onOpenSubmission,
                    onOpenStats = { showStats = true },
                )
                isExpert && selectedTab == 1 -> TestCandidates(
                    submissions = uiState.submissions,
                    assignmentTitle = assignmentTitle,
                    candidateName = candidateName,
                    onSelectSubmission = { onOpenSubmission(it.id) },
                )
                selectedTab == 2 -> SettingsScreen(session = session, onLogout = onLogout)
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.i("[MainShell]", "Старт — role=${session.role}")
        onRefresh()
    }
}
