package good.space.runnershi.global.auth

import good.space.runnershi.model.dto.auth.LoginRequest
import good.space.runnershi.model.dto.auth.SignUpRequest
import good.space.runnershi.model.dto.auth.TokenRefreshRequest
import good.space.runnershi.model.dto.auth.TokenRefreshResponse
import good.space.runnershi.model.dto.auth.TokenResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    // 회원가입 API
    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<String> {
        authService.signUp(request)
        return ResponseEntity
            .status(HttpStatus.CREATED) // 201 Created
            .body("회원가입 성공!")
    }

    // 로그인 API
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        val token = authService.login(request)
        return ResponseEntity.ok(token) // 200 OK + 토큰 반환
    }

    // 토큰 갱신 API
    // RequestBody로 refreshToken 문자열 하나만 받거나, DTO를 만들어 받아도 됩니다.
    @PostMapping("/refresh")
    fun refresh(@RequestBody request: TokenRefreshRequest): ResponseEntity<TokenRefreshResponse> {
        val token = authService.refreshAccessToken(request.refreshToken)
        return ResponseEntity.ok(token)
    }

}
