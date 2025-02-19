package io.goorm.youtube.util;

import io.goorm.youtube.dto.FileUploadResult;
import io.goorm.youtube.exception.FileDeleteException;
import io.goorm.youtube.exception.FileUploadException;
import io.goorm.youtube.exception.FileValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Component
public class FileUtil {

    @Value("${file.upload.directory}")
    private String uploadDirectory;

    @Value("${file.profile.max-size}")
    private long profileMaxSize;

    @Value("${file.video.max-size}")
    private long videoMaxSize;

    @Value("${file.thumbnail.max-size}")
    private long thumbnailMaxSize;

    @Value("${file.profile.allowed-extensions}")
    private String profileAllowedExtensions;

    @Value("${file.video.allowed-extensions}")
    private String videoAllowedExtensions;

    @Value("${file.thumbnail.allowed-extensions}")
    private String thumbnailAllowedExtensions;

    /**
     * 프로필 이미지 업로드
     * @param file 업로드할 프로필 이미지 파일
     * @return 파일 업로드 결과 정보
     * @throws FileValidationException 파일 유효성 검사 실패 시
     * @throws FileUploadException 파일 업로드 실패 시
     */
    public FileUploadResult uploadProfileImage(MultipartFile file) {
        validateFile(file, profileMaxSize, profileAllowedExtensions, "프로필 이미지");
        return uploadFile(file, "profiles");
    }

    /**
     * 비디오 파일 업로드
     * @param file 업로드할 비디오 파일
     * @return 파일 업로드 결과 정보
     * @throws FileValidationException 파일 유효성 검사 실패 시
     * @throws FileUploadException 파일 업로드 실패 시
     */
    public FileUploadResult uploadVideo(MultipartFile file) {
        validateFile(file, videoMaxSize, videoAllowedExtensions, "비디오");
        return uploadFile(file, "videos");
    }

    /**
     * 썸네일 이미지 업로드
     * @param file 업로드할 썸네일 이미지 파일
     * @return 파일 업로드 결과 정보
     * @throws FileValidationException 파일 유효성 검사 실패 시
     * @throws FileUploadException 파일 업로드 실패 시
     */
    public FileUploadResult uploadThumbnail(MultipartFile file) {
        validateFile(file, thumbnailMaxSize, thumbnailAllowedExtensions, "썸네일");
        return uploadFile(file, "thumbnails");
    }

    /**
     * 파일 업로드 공통 로직
     * @param file 업로드할 파일
     * @param directory 저장될 디렉토리 경로
     * @return 파일 업로드 결과 정보
     * @throws FileUploadException 파일 업로드 실패 시
     */
    private FileUploadResult uploadFile(MultipartFile file, String directory) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String savedFileName = UUID.randomUUID() + extension;
            String filePath = directory + "/" + savedFileName;

            File uploadDir = new File(uploadDirectory + "/" + directory);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File dest = new File(uploadDirectory + "/" + filePath);
            file.transferTo(dest);

            log.info("File uploaded successfully: {}", filePath);

            return FileUploadResult.builder()
                    .filePath(filePath)
                    .originalFileName(originalFilename)
                    .savedFileName(savedFileName)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .build();

        } catch (IOException e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new FileUploadException("파일 업로드에 실패했습니다.", e);
        }
    }

    /**
     * 파일 삭제
     * @param filePath 삭제할 파일 경로
     * @throws FileDeleteException 파일 삭제 실패 시
     */
    public void deleteFile(String filePath) {
        File file = new File(uploadDirectory + "/" + filePath);
        if (file.exists() && !file.delete()) {
            log.error("Failed to delete file: {}", filePath);
            throw new FileDeleteException("파일 삭제에 실패했습니다.");
        }
        log.info("File deleted successfully: {}", filePath);
    }

    /**
     * 파일 유효성 검사 통합
     * @param file 검사할 파일
     * @param maxSize 최대 허용 크기
     * @param allowedExtensions 허용된 확장자 목록 (콤마로 구분)
     * @param fileType 파일 유형 설명
     * @throws FileValidationException 유효성 검사 실패 시
     */
    private void validateFile(MultipartFile file, long maxSize, String allowedExtensions, String fileType) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException(fileType + " 파일이 비어있습니다.");
        }

        validateFileSize(file, maxSize, fileType);
        validateFileExtension(file, allowedExtensions, fileType);
    }

    /**
     * 파일 크기 검사
     */
    private void validateFileSize(MultipartFile file, long maxSize, String fileType) {
        if (file.getSize() > maxSize) {
            throw new FileValidationException(
                    String.format("%s 파일 크기가 제한(%d bytes)을 초과합니다.", fileType, maxSize)
            );
        }
    }

    /**
     * 파일 확장자 검사
     */
    private void validateFileExtension(MultipartFile file, String allowedExtensions, String fileType) {
        String extension = getExtension(file.getOriginalFilename()).substring(1);
        if (!Arrays.asList(allowedExtensions.split(",")).contains(extension.toLowerCase())) {
            throw new FileValidationException(
                    String.format("%s 파일의 확장자가 허용되지 않습니다. (허용: %s)", fileType, allowedExtensions)
            );
        }
    }

    /**
     * 파일명에서 확장자 추출
     */
    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
}