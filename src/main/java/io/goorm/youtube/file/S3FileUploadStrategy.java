package io.goorm.youtube.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.goorm.youtube.dto.FileUploadResult;
import io.goorm.youtube.exception.FileDeleteException;
import io.goorm.youtube.exception.FileValidationException;
import io.goorm.youtube.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@Profile("aws")
@RequiredArgsConstructor
public class S3FileUploadStrategy implements FileUploadStrategy {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${file.upload.uri}")
    private String baseUri;

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

    @Override
    public FileUploadResult uploadProfileImage(MultipartFile file) {
        validateFile(file, profileMaxSize, profileAllowedExtensions, "프로필 이미지");
        return uploadToS3(file, "profiles");
    }

    @Override
    public FileUploadResult uploadVideo(MultipartFile file) {
        validateFile(file, videoMaxSize, videoAllowedExtensions, "비디오");
        return uploadToS3(file, "videos");
    }

    @Override
    public FileUploadResult uploadThumbnail(MultipartFile file) {
        validateFile(file, thumbnailMaxSize, thumbnailAllowedExtensions, "썸네일");
        return uploadToS3(file, "thumbnails");
    }

    private void validateFile(MultipartFile file, long maxSize, String allowedExtensions, String fileType) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException(fileType + " 파일이 비어있습니다.");
        }

        validateFileSize(file, maxSize, fileType);
        validateFileExtension(file, allowedExtensions, fileType);
    }

    private void validateFileSize(MultipartFile file, long maxSize, String fileType) {
        if (file.getSize() > maxSize) {
            throw new FileValidationException(
                    String.format("%s 파일 크기가 제한(%d bytes)을 초과합니다.", fileType, maxSize)
            );
        }
    }

    private void validateFileExtension(MultipartFile file, String allowedExtensions, String fileType) {
        String extension = getExtension(file.getOriginalFilename()).substring(1);
        if (!Arrays.asList(allowedExtensions.split(",")).contains(extension.toLowerCase())) {
            throw new FileValidationException(
                    String.format("%s 파일의 확장자가 허용되지 않습니다. (허용: %s)", fileType, allowedExtensions)
            );
        }
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    private FileUploadResult uploadToS3(MultipartFile file, String directory) {
        try {
            File convertFile = convertMultiPartToFile(file)
                    .orElseThrow(() -> new FileUploadException("파일 변환에 실패했습니다.", new IOException()));

            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String savedFileName = UUID.randomUUID() + extension;
            String filePath = directory + "/" + savedFileName;

            // S3 업로드
            String s3Url = uploadFileToS3(convertFile, filePath);

            // 로컬 임시 파일 삭제
            removeLocalFile(convertFile);

            return FileUploadResult.builder()
                    .filePath(s3Url)
                    .originalFileName(originalFilename)
                    .savedFileName(savedFileName)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .build();

        } catch (IOException e) {
            log.error("S3 파일 업로드 실패: {}", file.getOriginalFilename(), e);
            throw new FileUploadException("파일 업로드에 실패했습니다.", e);
        }
    }

    private Optional<File> convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertFile = File.createTempFile(
                UUID.randomUUID().toString(),
                file.getOriginalFilename()
        );

        try (FileOutputStream fos = new FileOutputStream(convertFile)) {
            fos.write(file.getBytes());
        }

        return Optional.of(convertFile);
    }

    private String uploadFileToS3(File file, String fileName) {
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (Exception e) {
            log.error("S3 업로드 중 오류 발생", e);
            throw new FileUploadException("S3 업로드에 실패했습니다.", e);
        }
    }

    private void removeLocalFile(File file) {
        if (file.delete()) {
            log.info("임시 파일 삭제 성공: {}", file.getName());
        } else {
            log.warn("임시 파일 삭제 실패: {}", file.getName());
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            amazonS3Client.deleteObject(bucket, filePath);
            log.info("S3에서 파일 삭제 성공: {}", filePath);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", filePath, e);
            throw new FileDeleteException("파일 삭제에 실패했습니다.");
        }
    }
}