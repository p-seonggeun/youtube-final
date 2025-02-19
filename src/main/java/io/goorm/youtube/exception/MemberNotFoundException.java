package io.goorm.youtube.exception;

import lombok.Getter;

@Getter
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException(Long memberId) {
        super("Video not found with id: " + memberId);
    }
}
