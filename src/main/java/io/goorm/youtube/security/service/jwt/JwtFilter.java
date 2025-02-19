package io.goorm.youtube.security.service.jwt;

import io.goorm.youtube.exception.JwtAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = resolveToken(request);
            String requestURI = request.getRequestURI();

            if (StringUtils.hasText(jwt)) {
                if (tokenProvider.validateToken(jwt)) {
                    Authentication authentication = tokenProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}",
                            authentication.getName(), requestURI);
                } else {
                    log.debug("유효하지 않은 JWT 토큰입니다, uri: {}", requestURI);
                    throw new JwtAuthenticationException("유효하지 않은 JWT 토큰입니다.");
                }
            } else if (!shouldNotFilter(request)) {
                log.debug("JWT 토큰이 없습니다, uri: {}", requestURI);
                throw new JwtAuthenticationException("JWT 토큰이 없습니다.");
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.", e);
            SecurityContextHolder.clearContext();
            throw new JwtAuthenticationException("만료된 JWT 토큰입니다.", e);
        } catch (SecurityException e) {
            log.error("유효하지 않은 JWT 서명입니다.", e);
            SecurityContextHolder.clearContext();
            throw new JwtAuthenticationException("유효하지 않은 JWT 서명입니다.", e);
        } catch (JwtException e) {
            log.error("JWT 토큰이 잘못되었습니다.", e);
            SecurityContextHolder.clearContext();
            throw new JwtAuthenticationException("JWT 토큰이 잘못되었습니다.", e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/") ||
                path.equals("/api/movies") ||
                path.matches("/api/movies/[^/]+") ||
                path.equals("/api/auth/login") ||
                path.equals("/api/members") ||
                path.matches("/api/members/[^/]+/duplicate") ||
                path.matches("/api/members/[^/]+/password") ||
                path.startsWith("/h2-console") ||
                path.startsWith("/upload");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}