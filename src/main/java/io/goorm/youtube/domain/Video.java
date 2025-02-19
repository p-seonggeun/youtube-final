package io.goorm.youtube.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "videos")
public class Video extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoSeq;

    @Column(nullable = false)
    private String videoPath;

    private String thumbnailPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq")
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean publishYn = false;

    @Column(nullable = false)
    private boolean deleteYn = false;


    // 정적 팩토리 메서드
    public static Video createVideo(String videoPath, String thumbnailPath,
                                    Member member, String title, String content) {
        Video video = new Video();
        video.videoPath = videoPath;
        video.thumbnailPath = thumbnailPath;
        video.member = member;
        video.title = title;
        video.content = content;
        video.publishYn = false;
        video.deleteYn = false;
        return video;
    }

    public void update(String title, String content, String thumbnailPath) {
        this.title = title;
        this.content = content;
        if (thumbnailPath != null) {
            this.thumbnailPath = thumbnailPath;
        }
    }

    public void updatePublishStatus(boolean publishYn) {
        this.publishYn = publishYn;
    }

    public void delete() {
        this.deleteYn = true;
    }

    public void updateVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public void updateThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public void updateMetadata(String title, String content) {
        this.title = title;
        this.content = content;
    }
}