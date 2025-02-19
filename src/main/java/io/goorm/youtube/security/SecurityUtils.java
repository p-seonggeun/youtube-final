package io.goorm.youtube.security;

import io.goorm.youtube.domain.Member;
import io.goorm.youtube.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final MemberRepository memberRepository;

    /**
     * 현재 인증된 사용자의 Member 엔티티 조회
     * @return 인증된 사용자의 Member 엔티티
     * @throws BadCredentialsException 인증된 사용자가 없는 경우
     * @throws EntityNotFoundException 사용자 정보가 DB에 없는 경우
     */
    public Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("인증된 사용자가 아닙니다.");
        }

        return memberRepository.findByMemberId(authentication.getName())
                .orElseThrow(() -> {
                    log.error("사용자 정보를 찾을 수 없습니다: {}", authentication.getName());
                    return new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
                });
    }
}