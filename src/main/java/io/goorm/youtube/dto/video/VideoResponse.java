package io.goorm.youtube.dto.video;

import io.goorm.youtube.domain.Video;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class VideoResponse {
    private Long videoSeq;
    private String videoPath;
    private String thumbnailPath;
    private String memberName;
    private String title;
    private String content;
    private boolean publishYn;
    private LocalDateTime regAt;
    private LocalDateTime updateAt;

    @Builder
    private VideoResponse(Long videoSeq, String videoPath, String thumbnailPath,
                          String memberName, String title, String content,
                          boolean publishYn, LocalDateTime regAt,
                          LocalDateTime updateAt) {
        this.videoSeq = videoSeq;
        this.videoPath = videoPath;
        this.thumbnailPath = thumbnailPath;
        this.memberName = memberName;
        this.title = title;
        this.content = content;
        this.publishYn = publishYn;
        this.regAt = regAt;
        this.updateAt = updateAt;
    }

    public static VideoResponse of(Video video) {
        return VideoResponse.builder()
                .videoSeq(video.getVideoSeq())
                .videoPath(video.getVideoPath())
                .thumbnailPath(video.getThumbnailPath())
                .memberName(video.getMember().getMemberName())
                .title(video.getTitle())
                .content(video.getContent())
                .publishYn(video.isPublishYn())
                .regAt(video.getRegAt())
                .updateAt(video.getUpdateAt())
                .build();
    }
}

