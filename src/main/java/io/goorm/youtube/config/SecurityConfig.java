package io.goorm.youtube.config;

import io.goorm.youtube.security.service.jwt.JwtAccessDeniedHandler;
import io.goorm.youtube.security.service.jwt.JwtAuthenticationEntryPoint;
import io.goorm.youtube.security.service.jwt.JwtFilter;
import io.goorm.youtube.security.service.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 설정을 담당하는 설정 클래스
 *
 * @Configuration: 스프링 설정 클래스임을 나타냄
 * @EnableWebSecurity: 웹 보안 활성화
 * @EnableMethodSecurity: 메소드 수준 보안 활성화 (예: @PreAuthorize 사용 가능)
 * @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * JWT 관련 의존성 주입
     * - tokenProvider: JWT 토큰 생성 및 검증
     * - jwtAuthenticationEntryPoint: 인증 실패 시 처리 (401)
     * - jwtAccessDeniedHandler: 인가 실패 시 처리 (403)
     */
    private final JwtTokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * 인증 없이 접근 가능한 공개 API 경로 목록
     */
    private static final String[] PUBLIC_URLS = {
            "/",
            "/api/movies",
            "/api/movies/{id}",
            "/api/auth/login",
            "/api/members",
            "/api/members/{id}/duplicate",
            "/api/members/{id}/password"
    };

    /**
     * 정적 리소스 접근 경로 목록
     */
    private static final String[] RESOURCE_URLS = {
            "/h2-console/**",    // H2 데이터베이스 콘솔
            "/upload/**"         // 업로드된 파일 접근 경로
    };

    /**
     * 비밀번호 암호화를 위한 BCrypt 인코더 빈 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 필터 체인 설정
     * 모든 보안 설정의 핵심이 되는 메소드
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 기능 비활성화 (JWT 사용으로 불필요)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // JWT 예외 처리 설정
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)          // 403 권한 없음
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)// 401 인증 실패
                )

                // 세션 설정: JWT 사용으로 세션을 생성하지 않음
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // URL별 접근 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_URLS).permitAll()         // 공개 API는 모두 허용
                        .requestMatchers(RESOURCE_URLS).permitAll()       // 정적 리소스 접근 허용
                        .anyRequest().authenticated()                      // 그 외 요청은 인증 필요
                )

                // JWT 인증 필터 추가 (UsernamePassword 인증 필터 전에 실행)
                .addFilterBefore(
                        new JwtFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )

                // H2 콘솔 사용을 위한 설정 (개발 환경용)
                .headers(headers -> headers
                        .frameOptions(frameOptions ->
                                frameOptions.sameOrigin()  // same origin에서 iframe 허용
                        )
                );

        return http.build();
    }

    /**
     * CORS(Cross-Origin Resource Sharing) 설정
     * 다른 도메인에서의 API 접근 정책을 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");    // 모든 출처 허용
        configuration.addAllowedMethod("*");           // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*");           // 모든 헤더 허용
        configuration.setAllowCredentials(true);       // 인증 정보 포함 허용
        configuration.setMaxAge(3600L);                // preflight 요청 결과 캐시 시간 (1시간)

        // 모든 경로에 대해 위의 CORS 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}