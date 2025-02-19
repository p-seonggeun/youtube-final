package io.goorm.youtube.controller;

import io.goorm.youtube.dto.member.MemberCreateRequest;
import io.goorm.youtube.dto.member.MemberResponse;
import io.goorm.youtube.dto.member.MemberUpdateRequest;
import io.goorm.youtube.dto.member.PasswordUpdateRequest;
import io.goorm.youtube.service.MemberService;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberRestController {

    private final MemberService memberService;

    @ApiOperation(value = "회원가입", notes = "회원가입용 입니다.")
    @PostMapping("/members")
    public ResponseEntity<Long> createMember(
            @Valid @RequestPart MemberCreateRequest request,
            @RequestPart(required = false) MultipartFile profileImage) {
        return ResponseEntity.ok(memberService.createMember(request, profileImage));
    }

    @ApiOperation(value = "중복아이디 확인", notes = "회원가입 시 중복 아이디 확인 용입니다.")
    @GetMapping("/members/{id}/duplicate")
    public ResponseEntity<Boolean> checkMemberIdDuplicate(@PathVariable("id") String memberId) {
        return ResponseEntity.ok(memberService.checkMemberIdDuplicate(memberId));
    }

    @ApiOperation(value = "본인 상세정보 조회", notes = "비밀번호를 제외한 본인 상세정보 조회용입니다.")
    @GetMapping("/me/members")
    public ResponseEntity<MemberResponse> getMyInfo() {
        return ResponseEntity.ok(memberService.getMyInfo());
    }

    @ApiOperation(value = "본인정보 수정", notes = "비밀번호를 제외한 본인정보 수정용입니다.")
    @PutMapping("/me/members")
    public ResponseEntity<Void> updateMyInfo(
            @Valid @RequestPart MemberUpdateRequest request,
            @RequestPart(required = false) MultipartFile profileImage) {
        memberService.updateMyInfo(request, profileImage);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "비밀번호 수정", notes = "비밀번호를 수정용입니다.")
    @PutMapping("/me/members/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable("id") String memberId,
            @Valid @RequestBody PasswordUpdateRequest request) {
        memberService.updatePassword(request);
        return ResponseEntity.ok().build();
    }
}
