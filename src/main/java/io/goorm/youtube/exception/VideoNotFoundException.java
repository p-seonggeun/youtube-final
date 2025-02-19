package io.goorm.youtube.exception;

import lombok.Getter;

@Getter
public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(String message) {
        super(message);
    }

    public VideoNotFoundException(Long videoSeq) {
        super("Video not found with id: " + videoSeq);
    }
}
