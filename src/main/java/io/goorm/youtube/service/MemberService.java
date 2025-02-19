package io.goorm.youtube.service;

import io.goorm.youtube.domain.Member;
import io.goorm.youtube.dto.FileUploadResult;
import io.goorm.youtube.dto.member.MemberCreateRequest;
import io.goorm.youtube.dto.member.MemberResponse;
import io.goorm.youtube.dto.member.MemberUpdateRequest;
import io.goorm.youtube.dto.member.PasswordUpdateRequest;
import io.goorm.youtube.exception.DuplicateMemberException;
import io.goorm.youtube.exception.PasswordMismatchException;
import io.goorm.youtube.file.FileUploadStrategy;
import io.goorm.youtube.repository.MemberRepository;
import io.goorm.youtube.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    private final FileUploadStrategy fileUploadStrategy;
    private final SecurityUtils securityUtils;

    //사용자 생성
    @Transactional
    public Long createMember(MemberCreateRequest request, MultipartFile profileImage) {
        // 아이디 중복 검사
        if (memberRepository.existsByMemberId(request.getMemberId())) {
            throw new DuplicateMemberException(request.getMemberId());
        }

        // 프로필 이미지 업로드
        FileUploadResult profileUploadResult = fileUploadStrategy.uploadProfileImage(profileImage);

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

    //아이디 중복 확인
    public boolean checkMemberIdDuplicate(String memberId) {
        return memberRepository.existsByMemberId(memberId);
    }

    public MemberResponse getMyInfo() {

        Member member = securityUtils.getCurrentMember();

        return MemberResponse.of(member);
    }

    //사용자 수정
    @Transactional
    @PreAuthorize("isAuthenticated()")  // 인증된 사용자만 접근 가능
    public void updateMyInfo(MemberUpdateRequest request, MultipartFile profileImage) {

        Member member = securityUtils.getCurrentMember();

        // 새로운 비디오 파일이 있는 경우
        if (profileImage != null && !profileImage.isEmpty()) {
            FileUploadResult profileResult = fileUploadStrategy.uploadProfileImage(profileImage);
            fileUploadStrategy.deleteFile(member.getProfilePath()); // 기존 파일 삭제
            member.updateProfile(profileResult.getFilePath());
        }

        member.updateMetadata(
                request.getMemberName(),
                request.getMemberInfo()
        );
    }

    //비밀번호 수정
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void updatePassword(Long memberSeq, PasswordUpdateRequest request) {  // memberId 추가

        // 현재 인증된 사용자가 이 memberSeq의 소유자인지 확인
        Member currentMember = securityUtils.getCurrentMember();

        if (!currentMember.getMemberSeq().equals(memberSeq)) {
            throw new AccessDeniedException("비밀번호 변경 권한이 없습니다.");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentMember.getMemberPw())) {
            throw new PasswordMismatchException();
        }

        // 새 비밀번호가 현재 비밀번호와 같은지 확인
        if (passwordEncoder.matches(request.getNewPassword(), currentMember.getMemberPw())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        currentMember.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

}