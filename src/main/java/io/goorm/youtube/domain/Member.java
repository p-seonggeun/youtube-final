package io.goorm.youtube.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberSeq;

    @Column(unique = true, nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String memberPw;

    @Column(nullable = false)
    private String memberName;

    private String profilePath;

    private String memberInfo;

    private LocalDateTime withdrawalDate;

    private String withdrawalReason;

    @Column(nullable = false)
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    // 정적 팩토리 메서드
    public static Member createMember(String memberId, String memberPw,
                                      String memberName, String profilePath, String memberInfo) {
        Member member = new Member();
        member.memberId = memberId;
        member.memberPw = memberPw;
        member.memberName = memberName;
        member.profilePath = profilePath;
        member.memberInfo = memberInfo;
        member.role = Role.USER;
        member.enabled = true;
        return member;
    }

    public void encodePw(PasswordEncoder passwordEncoder) {
        this.memberPw = passwordEncoder.encode(this.memberPw);
    }

    public void updateProfile(String memberName, String profilePath, String memberInfo) {
        this.memberName = memberName;
        this.profilePath = profilePath;
        this.memberInfo = memberInfo;
    }

    public void updateMetadata(String memberName,String memberInfo) {
        this.memberName = memberName;
        this.memberInfo = memberInfo;
    }

    public void updateProfile(String profilePath) {
        this.profilePath = profilePath;
    }

    public void updatePassword(String encodedPassword) {
        this.memberPw = encodedPassword;
    }

    public void withdraw(String reason) {
        this.withdrawalDate = LocalDateTime.now();
        this.withdrawalReason = reason;
        this.enabled = false;
    }
}