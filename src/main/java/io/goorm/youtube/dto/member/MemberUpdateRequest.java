package io.goorm.youtube.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUpdateRequest {
    @NotBlank(message = "이름을 입력해주세요")
    private String memberName;
    private String memberInfo;
}
