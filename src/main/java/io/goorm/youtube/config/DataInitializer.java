package io.goorm.youtube.config;

import io.goorm.youtube.domain.Member;
import io.goorm.youtube.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("local")
public class DataInitializer {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @PostConstruct
    public void init() {

        Member member = Member.createMember(
                "user",
                passwordEncoder.encode("1234"),
                "Usermember",
                "/upload/user.png",
                "사용자 유저입니다."
        );

        memberRepository.save(member);
    }
}
