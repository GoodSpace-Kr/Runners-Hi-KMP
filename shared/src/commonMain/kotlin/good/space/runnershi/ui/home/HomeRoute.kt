package good.space.runnershi.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(
    navigateToRunning: () -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var showSettingsPopup by remember { mutableStateOf(false) }

    // 화면 진입 시 퀘스트 데이터 조회 (한 번만 실행)
    LaunchedEffect(Unit) {
        viewModel.fetchQuestData()
    }

    HomeScreen(
        uiState = uiState,
        navigateToRun = navigateToRunning,
        onSettingsClick = { showSettingsPopup = true },
        onTtsClick = viewModel::toggleTts,
        showSettingsPopup = showSettingsPopup,
        onDismissSettingsPopup = { showSettingsPopup = false },
        onToggleAutoPause = {
            viewModel.toggleAutoPause()
        },
        onLogout = {
            coroutineScope.launch {
                viewModel.logout()
                navigateToLogin()
            }
        },
    )
}
