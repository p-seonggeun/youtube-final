package io.goorm.youtube.controller.auth;

import io.goorm.youtube.dto.auth.LoginRequest;
import io.goorm.youtube.dto.auth.TokenResponse;
import io.goorm.youtube.security.service.auth.AuthService;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;

    @ApiOperation(value = "로그인", notes = "사용자 로그인용입니다.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @ApiOperation(value = "로그아웃", notes = "사용자 로그아웃용입니다. jwt 방식이기에 실제 토큰 삭제는 클라이언트에서 수행한다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "리프레시토큰", notes = "리프레시토큰용입니다.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}
