package io.goorm.youtube.exception;

public class DuplicateMemberException extends RuntimeException {
    public DuplicateMemberException(String memberId) {
        super("이미 가입된 아이디입니다: " + memberId);
    }

    public DuplicateMemberException(String message, Throwable cause) {
        super(message, cause);
    }
}