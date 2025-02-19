package io.goorm.youtube.controller.auth;

import io.goorm.youtube.dto.auth.LoginRequest;
import io.goorm.youtube.dto.auth.TokenResponse;
import io.goorm.youtube.security.service.auth.AuthService;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 요청을 처리하는 REST 컨트롤러
 * 로그인, 로그아웃, 토큰 갱신 등의 인증 관련 엔드포인트를 제공
 *
 * @RestController: REST API 컨트롤러 지정
 * @RequestMapping("/api/auth"): 기본 URL 경로 설정
 * @RequiredArgsConstructor: 필수 필드에 대한 생성자 자동 생성
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    /**
     * 인증 관련 비즈니스 로직을 처리하는 서비스
     */
    private final AuthService authService;

    /**
     * 사용자 로그인을 처리하는 엔드포인트
     *
     * @param request 로그인 요청 정보 (아이디, 비밀번호)
     * @return JWT 토큰이 포함된 응답
     *
     * @Valid: 요청 객체의 유효성 검증
     * @RequestBody: JSON 요청 본문을 객체로 변환
     *
     * 성공 시: 200 OK와 함께 액세스 토큰, 리프레시 토큰 반환
     */
    @ApiOperation(value = "로그인", notes = "사용자 로그인용입니다.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * 사용자 로그아웃을 처리하는 엔드포인트
     *
     * @return 로그아웃 성공 시 빈 응답 (200 OK)
     *
     * JWT 기반 인증의 특성상 서버에서는 별도의 토큰 무효화 처리를 하지 않음
     * 실제 토큰 삭제는 클라이언트 측에서 저장된 토큰을 제거하는 방식으로 수행
     */
    @ApiOperation(value = "로그아웃", notes = "사용자 로그아웃용입니다. jwt 방식이기에 실제 토큰 삭제는 클라이언트에서 수행한다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하는 엔드포인트
     *
     * @param refreshToken 리프레시 토큰 (헤더에서 추출)
     * @return 새로 발급된 액세스 토큰 정보
     *
     * @RequestHeader: HTTP 헤더에서 리프레시 토큰 추출
     *
     * 성공 시: 200 OK와 함께 새로운 액세스 토큰 반환
     * 실패 시: 401 Unauthorized 응답
     */
    @ApiOperation(value = "리프레시토큰", notes = "리프레시토큰용입니다.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}