package io.goorm.youtube.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "아이디를 입력해주세요")
    private String memberId;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String memberPw;
}
