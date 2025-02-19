package io.goorm.youtube.exception;

/**
 * 파일 관련 커스텀 예외 클래스들
 */
public class FileUploadException extends RuntimeException {
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
