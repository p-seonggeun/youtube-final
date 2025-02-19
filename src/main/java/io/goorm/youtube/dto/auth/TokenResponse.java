package io.goorm.youtube.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponse {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String memberName;

    @Builder
    public TokenResponse(String grantType, String accessToken, String refreshToken, String memberName) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.memberName = memberName;
    }
}
