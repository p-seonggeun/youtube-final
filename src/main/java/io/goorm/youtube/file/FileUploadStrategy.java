package io.goorm.youtube.file;

import io.goorm.youtube.dto.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadStrategy {
    FileUploadResult uploadProfileImage(MultipartFile file);
    FileUploadResult uploadVideo(MultipartFile file);
    FileUploadResult uploadThumbnail(MultipartFile file);
    void deleteFile(String filePath);
}