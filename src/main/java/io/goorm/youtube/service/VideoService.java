package io.goorm.youtube.service;

import io.goorm.youtube.domain.Member;
import io.goorm.youtube.domain.Video;
import io.goorm.youtube.dto.FileUploadResult;
import io.goorm.youtube.dto.video.VideoCreateRequest;
import io.goorm.youtube.dto.video.VideoResponse;
import io.goorm.youtube.dto.video.VideoUpdateRequest;
import io.goorm.youtube.exception.VideoNotFoundException;
import io.goorm.youtube.file.FileUploadStrategy;
import io.goorm.youtube.repository.VideoRepository;
import io.goorm.youtube.security.IsVideoOwner;
import io.goorm.youtube.security.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoService {

    private final VideoRepository videoRepository;
    private final FileUploadStrategy fileUploadStrategy;
    private final SecurityUtils securityUtils;

    @ApiOperation(value = "비디오목록 조회", notes = "삭제되지 않고 게시상태인 메인화면용 입니다.")
    public Page<VideoResponse> getPublicVideos(Pageable pageable) {
        return videoRepository.findByPublishYnTrueAndDeleteYnFalseOrderByRegAtDesc(pageable)
                .map(VideoResponse::of);
    }


    @ApiOperation(value = "비디오상세 조회", notes = "삭제되지 않고 게시상태인 메인화면용 입니다.")
    public VideoResponse getPublicVideoDetail(Long videoSeq) {

        Video video =  videoRepository.findByVideoSeqAndPublishYnTrueAndDeleteYnFalse(videoSeq)
                .orElseThrow(() -> new VideoNotFoundException(videoSeq));

        return VideoResponse.of(video);
    }

    @ApiOperation(value = "내비디오 목록 조회", notes = "로그인한 사용자 비디오 목록조회 입니다.")
    @PreAuthorize("isAuthenticated()")
    public Page<VideoResponse> getMyVideos(Pageable pageable) {

        Member member = securityUtils.getCurrentMember();  // 현재 로그인한 사용자 정보 조회

        return videoRepository.findByMember_MemberSeq(member.getMemberSeq(), pageable)
                .map(VideoResponse::of);

    }

    @ApiOperation(value = "내비디오 상세 조회", notes = "로그인한 사용자 비디오 상세 조회 입니다.")
    @IsVideoOwner
    public VideoResponse getVideoDetail(Long videoSeq) {

        Video video = videoRepository.findById(videoSeq)
                .orElseThrow(() -> new VideoNotFoundException(videoSeq));

        return VideoResponse.of(video);
    }

    @ApiOperation(value = "내비디오 등록", notes = "로그인한 사용자 비디오 등록 입니다.")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public Long uploadVideo(VideoCreateRequest request, MultipartFile videoFile, MultipartFile thumbnailFile) {

        Member member = securityUtils.getCurrentMember();

        // 비디오 파일 업로드
        FileUploadResult videoUploadResult = fileUploadStrategy.uploadVideo(videoFile);

        //선네일 파일 업로드
        FileUploadResult thumbnailUploadResult = fileUploadStrategy.uploadThumbnail(thumbnailFile);

        // 비디오 엔티티 생성 및 저장
        Video video = Video.createVideo(
                videoUploadResult.getFilePath(),
                thumbnailUploadResult.getFilePath(),
                member,
                request.getTitle(),
                request.getContent()
        );

        return videoRepository.save(video).getVideoSeq();
    }

    //로그인한 사용자 비디오 수정 입니다.
    @IsVideoOwner
    @Transactional
    public void updateVideo(Long videoSeq, VideoUpdateRequest request, MultipartFile videoFile, MultipartFile thumbnailFile) {
        Video video = videoRepository.findById(videoSeq)
                .orElseThrow(() -> new VideoNotFoundException(videoSeq));

        // 새로운 비디오 파일이 있는 경우
        if (videoFile != null && !videoFile.isEmpty()) {
            FileUploadResult videoUploadResult = fileUploadStrategy.uploadVideo(videoFile);
            fileUploadStrategy.deleteFile(video.getVideoPath()); // 기존 파일 삭제
            video.updateVideoPath(videoUploadResult.getFilePath());
        }

        // 새로운 썸네일 파일이 있는 경우
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            FileUploadResult thumbnailUploadResult = fileUploadStrategy.uploadThumbnail(thumbnailFile);
            if (video.getThumbnailPath() != null) {
                fileUploadStrategy.deleteFile(video.getThumbnailPath()); // 기존 썸네일이 있으면 삭제
            }
            video.updateThumbnailPath(thumbnailUploadResult.getFilePath());
        }

        // 제목과 내용 업데이트
        video.updateMetadata(request.getTitle(), request.getContent());
    }

    //로그인한 사용자 비디오 삭제 입니다.
    @IsVideoOwner
    @Transactional
    public void deleteVideo(Long videoSeq) {
        Video video = videoRepository.findById(videoSeq)
                .orElseThrow(() -> new VideoNotFoundException(videoSeq));
        video.delete();
    }

    //내비디오 게시상태 변경", notes = "로그인한 사용자 비디오 게시상태 변경 입니다.
    @IsVideoOwner
    @Transactional
    public void updatePublishYn(Long videoSeq) {
        Video video = videoRepository.findById(videoSeq)
                .orElseThrow(() -> new VideoNotFoundException(videoSeq));

        video.updatePublishStatus(!video.isPublishYn());
    }


}