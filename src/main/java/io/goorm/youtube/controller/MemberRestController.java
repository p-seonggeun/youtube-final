package io.goorm.youtube.controller;

import io.goorm.youtube.dto.ApiResponse;
import io.goorm.youtube.dto.member.MemberCreateRequest;
import io.goorm.youtube.dto.member.MemberResponse;
import io.goorm.youtube.dto.member.MemberUpdateRequest;
import io.goorm.youtube.dto.member.PasswordUpdateRequest;
import io.goorm.youtube.service.MemberService;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberRestController {

    private final MemberService memberService;

    @ApiOperation(value = "회원가입", notes = "회원가입용 입니다.")
    @PostMapping(value = "/members", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> createMember(
            @Valid @RequestPart MemberCreateRequest request,
            @RequestPart(required = false) MultipartFile profileImage) {

        Long memberId = memberService.createMember(request, profileImage);
        return ResponseEntity
                .created(URI.create("/api/members/" + memberId))
                .body(ApiResponse.success("회원가입이 성공적으로 완료되었습니다.", memberId));
    }


    @ApiOperation(value = "중복아이디 확인", notes = "회원가입 시 중복 아이디 확인 용입니다.")
    @GetMapping("/members/{id}/duplicate")
    public ResponseEntity<ApiResponse<Boolean>> checkMemberIdDuplicate(@PathVariable("id") String memberId) {
        boolean isDuplicate = memberService.checkMemberIdDuplicate(memberId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        isDuplicate ? "이미 사용중인 아이디입니다." : "사용 가능한 아이디입니다.",
                        isDuplicate
                ));
    }


    @ApiOperation(value = "본인 상세정보 조회", notes = "비밀번호를 제외한 본인 상세정보 조회용입니다.")
    @GetMapping("/me/members")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyInfo() {

        MemberResponse memberInfo = memberService.getMyInfo();

        return ResponseEntity.ok(
                ApiResponse.success("회원 정보를 성공적으로 조회했습니다.", memberInfo));
    }


    @ApiOperation(value = "본인정보 수정", notes = "비밀번호를 제외한 본인정보 수정용입니다.")
    @PutMapping("/me/members")
    public ResponseEntity<ApiResponse<Void>> updateMyInfo(
            @Valid @RequestPart MemberUpdateRequest request,
            @RequestPart(required = false) MultipartFile profileImage) {

        memberService.updateMyInfo(request, profileImage);

        return ResponseEntity.ok(
                ApiResponse.success("회원 정보가 성공적으로 수정되었습니다."));
    }


    @ApiOperation(value = "비밀번호 수정", notes = "비밀번호를 수정용입니다.")
    @PutMapping("/me/members/{id}/password")
    public ResponseEntity<ApiResponse> updatePassword(
            @PathVariable("id") Long memberSeq,
            @Valid @RequestBody PasswordUpdateRequest request) {

        memberService.updatePassword(memberSeq,request);

        return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."));
    }
}
