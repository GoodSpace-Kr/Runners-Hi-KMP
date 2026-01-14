package good.space.runnershi.ui.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResultRoute(
    onCloseClick: () -> Unit,
    onDataMissing: () -> Unit,
    viewModel: ResultViewModel = koinViewModel()
) {
    val runResult = viewModel.runResult
    val userInfo = viewModel.userInfo

    // 데이터가 없으면 (예: 비정상 진입) 즉시 홈으로
    LaunchedEffect(runResult) {
        if (runResult == null) {
            onDataMissing()
        }
    }

    if (runResult != null) {
        ResultScreen(
            userInfo = userInfo,
            runResult = runResult,
            onCloseClick = onCloseClick
        )
    }
}
