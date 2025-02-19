package io.goorm.youtube.security;

import io.goorm.youtube.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoAuthChecker {
    private final VideoRepository videoRepository;

    /**
     * 비디오 소유자 확인 메소드
     * @param videoSeq 비디오 시퀀스 번호
     * @return 현재 인증된 사용자가 비디오 소유자인 경우 true
     */
    public boolean isOwner(Long videoSeq) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        return videoRepository.findById(videoSeq)
                .map(video -> {
                    boolean isOwner = video.getMember().getMemberId().equals(currentUsername);

                    if (!isOwner) {
                        log.warn("사용자 {} - 비디오 {} 소유권 없음", currentUsername, videoSeq);
                    }

                    return isOwner;
                })
                .orElseGet(() -> {
                    log.error("존재하지 않는 비디오 ID: {}", videoSeq);
                    return false;
                });
    }
}