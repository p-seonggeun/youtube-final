package io.goorm.youtube.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberCreateRequest {
    @NotBlank(message = "아이디를 입력해주세요")
    private String memberId;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$",
            message = "비밀번호는 8자 이상이며, 대문자와 특수문자를 포함해야 합니다")
    private String memberPw;

    @NotBlank(message = "이름을 입력해주세요")
    private String memberName;

    private String memberInfo;
}
