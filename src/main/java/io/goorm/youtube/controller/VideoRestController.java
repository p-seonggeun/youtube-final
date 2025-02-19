package io.goorm.youtube.controller;

import io.goorm.youtube.dto.ApiResponse;
import io.goorm.youtube.dto.video.VideoCreateRequest;
import io.goorm.youtube.dto.video.VideoResponse;
import io.goorm.youtube.dto.video.VideoUpdateRequest;
import io.goorm.youtube.service.VideoService;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class VideoRestController {

    private final VideoService videoService;

    @ApiOperation(value = "게시된 동영상목록 조회", notes = "게시상태의 동영상 조회용입니다. 메인페이지에서 사용합니다.")
    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<Page<VideoResponse>>> getPublicVideos(
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success("동영상 목록을 성공적으로 조회했습니다.",
                        videoService.getPublicVideos(pageable)));
    }

    @ApiOperation(value = "동영상상세 조회", notes = "게시상태인 동영상 상세 조회용입니다.")
    @GetMapping("/movies/{videoSeq}")
    public ResponseEntity<ApiResponse<VideoResponse>> getVideoDetail(
            @PathVariable Long videoSeq) {

        return ResponseEntity.ok(
                ApiResponse.success("동영상을 성공적으로 조회했습니다.",
                        videoService.getPublicVideoDetail(videoSeq)));
    }


    @ApiOperation(value = "동영상 등록", notes = "로그인한 사용자의 동영상 등록용입니다.")
    @PostMapping("/me/movies")
    public ResponseEntity<ApiResponse<Long>> uploadVideo(
            @Valid @RequestPart VideoCreateRequest request,
            @RequestPart MultipartFile videoFile,
            @RequestPart(required = false) MultipartFile thumbnailFile) {

        Long videoSeq = videoService.uploadVideo(request, videoFile, thumbnailFile);

        return ResponseEntity
                .created(URI.create("/api/movies/" + videoSeq))
                .body(ApiResponse.success("동영상이 성공적으로 업로드되었습니다.", videoSeq));
    }

    @ApiOperation(value = "나의 동영상상세 조회", notes = "로그인한 사용자의 동영상 상세 조회용입니다.")
    @GetMapping("/me/movies/{videoSeq}")
    public ResponseEntity<ApiResponse<VideoResponse>> getMyVideoDetail(
            @PathVariable Long videoSeq) {

        return ResponseEntity.ok(
                ApiResponse.success("동영상을 성공적으로 조회했습니다.",
                        videoService.getVideoDetail(videoSeq)));
    }

    @ApiOperation(value = "나의 동영상목록 조회", notes = "로그인한 사용자의 동영상 목록 조회용입니다.")
    @GetMapping("/me/movies")
    public ResponseEntity<ApiResponse<Page<VideoResponse>>> getMyVideos(
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success("동영상 목록을 성공적으로 조회했습니다.",
                        videoService.getMyVideos(pageable)));
    }

    @ApiOperation(value = "동영상 수정", notes = "로그인한 사용자의 동영상 수정용 입니다.")
    @PutMapping("/me/movies/{videoSeq}")
    public ResponseEntity<ApiResponse<Void>> updateVideo(
            @PathVariable Long videoSeq,
            @Valid @RequestPart VideoUpdateRequest request,
            @RequestPart(required = false) MultipartFile videoFile,
            @RequestPart(required = false) MultipartFile thumbnailFile) {

        videoService.updateVideo(videoSeq, request, videoFile,thumbnailFile);

        return ResponseEntity.ok(ApiResponse.success("동영상이 성공적으로 수정되었습니다."));
    }

    @ApiOperation(value = "동영상 삭제", notes = "로그인한 사용자의 동영상 삭제용 입니다.")
    @DeleteMapping("/me/movies/{videoSeq}")
    public ResponseEntity<ApiResponse<Void>> deleteVideo(@PathVariable Long videoSeq) {

        videoService.deleteVideo(videoSeq);

        return ResponseEntity.ok(ApiResponse.success("동영상이 성공적으로 삭제되었습니다."));
    }

    @ApiOperation(value = "동영상 게시상태 변경", notes = "로그인한 사용자의 동영상 게시상태 변경용입니다.")
    @PutMapping("/me/movies/{videoSeq}/publish")
    public ResponseEntity<ApiResponse<Void>> updatePublishStatus(
            @PathVariable Long videoSeq) {

        videoService.updatePublishYn(videoSeq);

        return ResponseEntity.ok(ApiResponse.success("동영상 게시상태가 성공적으로 변경되었습니다."));
    }
}
