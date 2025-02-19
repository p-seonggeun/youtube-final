package io.goorm.youtube.dto.video;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VideoUpdateRequest {
    @NotBlank(message = "제목을 입력해주세요")
    private String title;
    private String content;
}
