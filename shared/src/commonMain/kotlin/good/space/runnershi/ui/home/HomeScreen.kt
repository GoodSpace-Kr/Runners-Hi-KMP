package good.space.runnershi.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import good.space.runnershi.model.dto.user.QuestResponse
import good.space.runnershi.ui.components.GradientCircleButton
import good.space.runnershi.ui.components.GradientCircleButtonColor
import good.space.runnershi.ui.components.GradientCircleButtonIcon
import good.space.runnershi.ui.components.Logo
import good.space.runnershi.ui.components.QuestCard
import good.space.runnershi.ui.components.SettingsButtonIcon.SETTINGS
import good.space.runnershi.ui.components.SettingsButtonIcon.VOLUME_MUTE
import good.space.runnershi.ui.components.SettingsButtonIcon.VOLUME_UP
import good.space.runnershi.ui.components.SettingsCircleButton
import good.space.runnershi.ui.components.SettingsPopup
import good.space.runnershi.ui.theme.RunnersHiTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    navigateToRun: () -> Unit,
    onSettingsClick: () -> Unit,
    onTtsClick: () -> Unit,
    showSettingsPopup: Boolean,
    onDismissSettingsPopup: () -> Unit,
    onToggleAutoPause: () -> Unit,
    onLogout: suspend () -> Unit,
    onWithdraw: suspend () -> Unit,
    onWithdrawErrorShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.withdrawErrorMessage) {
        uiState.withdrawErrorMessage?.let { errorMessage ->
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Short
            )
            onWithdrawErrorShown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                Logo(width = 200.dp)
                Spacer(modifier = Modifier.height(32.dp))

                QuestSection(
                    uiState = uiState
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                ButtonSection(
                    onSettingsClick = onSettingsClick,
                    navigateToRun = navigateToRun,
                    onTtsClick = onTtsClick,
                    isTtsEnabled = uiState.isTtsEnabled,
                    showSettingsPopup = showSettingsPopup,
                    isAutoPauseEnabled = uiState.isAutoPauseEnabled,
                    onDismissSettingsPopup = onDismissSettingsPopup,
                    onToggleAutoPause = onToggleAutoPause,
                    onLogout = onLogout,
                    onWithdraw = onWithdraw
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.systemBars)
        )
    }
}

@Composable
private fun ColumnScope.QuestSection(
    uiState: HomeUiState
) {
    // 제목
    Text(
        text = "오늘의 퀘스트",
        style = RunnersHiTheme.typography.titleLarge,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(20.dp))

    // 퀘스트 리스트
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = RunnersHiTheme.colorScheme.primary
                )
            }
            uiState.errorMessage != null -> {
                Text("퀘스트 목록을 불러올 수 없습니다.")
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.quests) { quest ->
                        QuestCard(
                            title = quest.title,
                            description = quest.description,
                            exp = quest.exp,
                            isCleared = quest.isCompleted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ButtonSection(
    onSettingsClick: () -> Unit,
    navigateToRun: () -> Unit,
    onTtsClick: () -> Unit,
    isTtsEnabled: Boolean,
    showSettingsPopup: Boolean,
    isAutoPauseEnabled: Boolean,
    onDismissSettingsPopup: () -> Unit,
    onToggleAutoPause: () -> Unit,
    onLogout: suspend () -> Unit,
    onWithdraw: suspend () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽: 설정 버튼
        Box {
            SettingsCircleButton(
                buttonIcon = SETTINGS,
                onClick = onSettingsClick,
                size = 56.dp
            )
            if (showSettingsPopup) {
                SettingsPopup(
                    isAutoPauseEnabled = isAutoPauseEnabled,
                    offset = IntOffset(x = 130, y = -130),
                    onDismissRequest = onDismissSettingsPopup,
                    onToggleAutoPause = onToggleAutoPause,
                    onLogout = {
                        coroutineScope.launch {
                            onLogout()
                        }
                    },
                    onWithdraw = {
                        coroutineScope.launch {
                            onWithdraw()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.width(24.dp))

        // 중앙: 시작 버튼
        GradientCircleButton(
            buttonColor = GradientCircleButtonColor.BLUE,
            buttonIcon = GradientCircleButtonIcon.START,
            onClick = navigateToRun,
            size = 120.dp
        )

        Spacer(modifier = Modifier.width(24.dp))

        // 오른쪽: TTL 설정
        SettingsCircleButton(
            buttonIcon = if (isTtsEnabled) VOLUME_UP else VOLUME_MUTE,
            onClick = onTtsClick,
            size = 56.dp
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    RunnersHiTheme {
        HomeScreen(
            uiState = HomeUiState(
                quests = listOf(
                    QuestResponse(
                        title = "3km 달리기",
                        description = "3km를 한 번에 달려보세요",
                        exp = 100,
                        isCompleted = false
                    ),
                    QuestResponse(
                        title = "15분 달리기",
                        description = "3km를 한 번에 달려보세요",
                        exp = 150,
                        isCompleted = true
                    ),
                    QuestResponse(
                        title = "10km 달리기",
                        description = "10km를 한 번에 달려보세요",
                        exp = 300,
                        isCompleted = false
                    )
                )
            ),
            navigateToRun = {},
            onSettingsClick = {},
            onTtsClick = {},
            showSettingsPopup = true,
            onDismissSettingsPopup = {},
            onToggleAutoPause = {},
            onLogout = {},
            onWithdraw = {},
            onWithdrawErrorShown = {},
        )
    }
}
