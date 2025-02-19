package io.goorm.youtube.dto.member;

import io.goorm.youtube.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponse {
    private String memberId;
    private String memberName;
    private String profilePath;
    private String memberInfo;

    @Builder
    private MemberResponse(String memberId, String memberName, String profilePath, String memberInfo) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.profilePath = profilePath;
        this.memberInfo = memberInfo;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .profilePath(member.getProfilePath())
                .memberInfo(member.getMemberInfo())
                .build();
    }
}
