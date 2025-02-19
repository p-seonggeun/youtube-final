package io.goorm.youtube.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 파일 업로드 결과를 담는 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileUploadResult {
    /**
     * 저장된 파일의 상대 경로
     * ex) videos/uuid-filename.mp4
     */
    private String filePath;

    /**
     * 원본 파일명
     * ex) my-video.mp4
     */
    private String originalFileName;

    /**
     * 저장된 파일명 (UUID)
     * ex) 123e4567-e89b-12d3-a456-426614174000.mp4
     */
    private String savedFileName;

    /**
     * 파일 크기 (bytes)
     */
    private long fileSize;

    /**
     * 파일 MIME 타입
     * ex) video/mp4, image/jpeg
     */
    private String contentType;

    @Builder
    public FileUploadResult(String filePath, String originalFileName, String savedFileName,
                            long fileSize, String contentType) {
        this.filePath = filePath;
        this.originalFileName = originalFileName;
        this.savedFileName = savedFileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }

    /**
     * 파일 경로로부터 확장자 추출
     * @return 파일 확장자 (점 포함)
     */
    public String getExtension() {
        return originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    /**
     * 파일 전체 경로 생성
     * @param baseUrl 기본 URL 또는 경로
     * @return 전체 파일 경로
     */
    public String getFullPath(String baseUrl) {
        return baseUrl + "/" + filePath;
    }

    /**
     * 파일 크기를 사람이 읽기 쉬운 형태로 변환
     * @return 변환된 파일 크기 (예: "1.5 MB")
     */
    public String getHumanReadableSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        }
        int exp = (int) (Math.log(fileSize) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "B";
        return String.format("%.1f %s", fileSize / Math.pow(1024, exp), pre);
    }

    /**
     * 파일이 이미지인지 확인
     */
    public boolean isImage() {
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * 파일이 비디오인지 확인
     */
    public boolean isVideo() {
        return contentType != null && contentType.startsWith("video/");
    }

    @Override
    public String toString() {
        return "FileUploadResult{" +
                "filePath='" + filePath + '\'' +
                ", originalFileName='" + originalFileName + '\'' +
                ", savedFileName='" + savedFileName + '\'' +
                ", fileSize=" + getHumanReadableSize() +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}