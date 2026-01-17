package good.space.runnershi.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import good.space.runnershi.auth.TokenStorage
import good.space.runnershi.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val tokenStorage: TokenStorage,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoggedIn = Channel<Boolean>()
    val isLoggedIn = _isLoggedIn.receiveAsFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val refreshToken = tokenStorage.getRefreshToken()
            if (refreshToken == null) {
                _isLoggedIn.send(false)
                return@launch
            }

            val result = authRepository.refreshAccessToken(refreshToken)
            // 서버 통신이 너무 빠를 것을 대비한 지연
            delay(1000)

            result.onSuccess {
                tokenStorage.saveTokens(
                    accessToken = it.accessToken,
                    refreshToken = it.refreshToken
                )
            }

            _isLoggedIn.send(result.isSuccess)
        }
    }
}
