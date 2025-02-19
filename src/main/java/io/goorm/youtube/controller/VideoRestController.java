package io.goorm.youtube.controller;

import io.goorm.youtube.dto.video.PublishRequest;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class VideoRestController {

    private final VideoService videoService;

    @ApiOperation(value = "게시된 동영상목록 조회", notes = "게시상태의 동영상 조회용입니다. 메인페이지에서 사용합니다.")
    @GetMapping("/movies")
    public ResponseEntity<Page<VideoResponse>> getPublicVideos(
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(videoService.getPublicVideos(pageable));
    }

    @ApiOperation(value = "동영상상세 조회", notes = "게시상태인 동영상 상세 조회용입니다.")
    @GetMapping("/movies/{videoSeq}")
    public ResponseEntity<VideoResponse> getVideoDetail(
            @PathVariable Long videoSeq) {

        return ResponseEntity.ok(videoService.getPublicVideoDetail(videoSeq));
    }


    @ApiOperation(value = "동영상 등록", notes = "로그인한 사용자의 동영상 등록용입니다.")
    @PostMapping("/me/movies")
    public ResponseEntity<Long> uploadVideo(
            @Valid @RequestPart VideoCreateRequest request,
            @RequestPart MultipartFile videoFile,
            @RequestPart(required = false) MultipartFile thumbnailFile) {

        return ResponseEntity.ok(videoService.uploadVideo(request, videoFile, thumbnailFile));
    }

    @ApiOperation(value = "나의 동영상상세 조회", notes = "로그인한 사용자의 동영상 상세 조회용입니다.")
    @GetMapping("/me/movies/{videoSeq}")
    public ResponseEntity<VideoResponse> getMyVideoDetail(
            @PathVariable Long videoSeq) {

        return ResponseEntity.ok(videoService.getVideoDetail(videoSeq));
    }

    @ApiOperation(value = "나의 동영상목록 조회", notes = "로그인한 사용자의 동영상 목록 조회용입니다.")
    @GetMapping("/me/movies")
    public ResponseEntity<Page<VideoResponse>> getMyVideos(
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(videoService.getMyVideos(pageable));
    }

    @ApiOperation(value = "동영상 수정", notes = "로그인한 사용자의 동영상 수정용 입니다.")
    @PutMapping("/me/movies/{videoSeq}")
    public ResponseEntity<Void> updateVideo(
            @PathVariable Long videoSeq,
            @Valid @RequestPart VideoUpdateRequest request,
            @RequestPart(required = false) MultipartFile videoFile,
            @RequestPart(required = false) MultipartFile thumbnailFile) {

        videoService.updateVideo(videoSeq, request, videoFile,thumbnailFile);

        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "동영상 삭제", notes = "로그인한 사용자의 동영상 삭제용 입니다.")
    @DeleteMapping("/me/movies/{videoSeq}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long videoSeq) {
        videoService.deleteVideo(videoSeq);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "동영상 게시상태 변경", notes = "로그인한 사용자의 동영상 게시상태 변경용입니다.")
    @PutMapping("/me/movies/{videoSeq}/publish")
    public ResponseEntity<Void> updatePublishStatus(
            @PathVariable Long videoSeq,
            @Valid @RequestBody PublishRequest request) {
        videoService.updatePublishStatus(videoSeq, request.isPublishYn());
        return ResponseEntity.ok().build();
    }
}
