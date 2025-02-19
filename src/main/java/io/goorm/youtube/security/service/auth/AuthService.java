package io.goorm.youtube.security.service.auth;

import io.goorm.youtube.dto.auth.LoginRequest;
import io.goorm.youtube.dto.auth.TokenResponse;
import io.goorm.youtube.security.service.jwt.JwtTokenProvider;
import io.goorm.youtube.domain.Member;
import io.goorm.youtube.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 1. ID/PW로 Authentication 객체 생성
        // 이 객체는 아직 인증되지 않은 상태
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getMemberId(), request.getMemberPw());

        // 2. 실제 검증이 일어나는 부분
        // authenticate 메서드가 실행될 때 CustomUserDetailsService의 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 인증된 정보를 기반으로 JWT 토큰 생성
        String accessToken = tokenProvider.createToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        Member member = memberRepository.findByMemberId(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));

        return TokenResponse.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberName(member.getMemberName())
                .build();
    }

    @Transactional
    public void logout() {
        // 현재는 클라이언트에서 토큰 제거만 하면 되므로 별도의 서버 처리는 없음
        // 추후 Redis 등을 도입하여 블랙리스트 관리를 할 수 있음
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        // 리프레시 토큰 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 리프레시 토큰에서 Authentication 추출
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);

        // 새로운 액세스 토큰 생성
        String newAccessToken = tokenProvider.createToken(authentication);

        return TokenResponse.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 기존 리프레시 토큰 유지
                .build();
    }
}