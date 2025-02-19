package io.goorm.youtube.service;

import io.goorm.youtube.dto.FileUploadResult;
import io.goorm.youtube.security.SecurityUtils;
import io.goorm.youtube.util.FileUtil;
import io.goorm.youtube.util.SecurityUtil;
import io.goorm.youtube.domain.Member;
import io.goorm.youtube.dto.member.MemberCreateRequest;
import io.goorm.youtube.dto.member.MemberResponse;
import io.goorm.youtube.dto.member.MemberUpdateRequest;
import io.goorm.youtube.dto.member.PasswordUpdateRequest;
import io.goorm.youtube.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUtil fileUtil;
    private final SecurityUtils securityUtils;

    @Transactional
    public Long createMember(MemberCreateRequest request, MultipartFile profileImage) {
        // 아이디 중복 검사
        if (memberRepository.existsByMemberId(request.getMemberId())) {
            throw new RuntimeException("이미 가입된 아이디입니다.");
        }

        // 프로필 이미지 업로드
        FileUploadResult profileUploadResult = fileUtil.uploadProfileImage(profileImage);

        Member member = Member.createMember(
                request.getMemberId(),
                request.getMemberPw(),
                request.getMemberName(),
                profileUploadResult.getFilePath(),
                request.getMemberInfo()
        );

        member.encodePw(passwordEncoder);
        return memberRepository.save(member).getMemberSeq();
    }

    public boolean checkMemberIdDuplicate(String memberId) {
        return memberRepository.existsByMemberId(memberId);
    }

    public MemberResponse getMyInfo() {
        return SecurityUtil.getCurrentMemberId()
                .flatMap(memberRepository::findByMemberId)
                .map(MemberResponse::of)
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")  // 인증된 사용자만 접근 가능
    public void updateMyInfo(MemberUpdateRequest request, MultipartFile profileImage) {

        Member member = securityUtils.getCurrentMember();

        // 새로운 비디오 파일이 있는 경우
        if (profileImage != null && !profileImage.isEmpty()) {
            FileUploadResult profileResult = fileUtil.uploadVideo(profileImage);
            fileUtil.deleteFile(member.getProfilePath()); // 기존 파일 삭제
            member.updateProfile(profileResult.getFilePath());
        }

        member.updateMetadata(
                request.getMemberName(),
                request.getMemberInfo()
        );
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")  // 인증된 사용자만 접근 가능
    public void updatePassword(PasswordUpdateRequest request) {
        Member member = securityUtils.getCurrentMember();

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getMemberPw())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        member.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

}