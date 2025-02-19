package io.goorm.youtube.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 설정을 담당하는 설정 클래스
 * 정적 리소스 핸들링을 위한 설정을 정의합니다.
 *
 * @Configuration 어노테이션은 이 클래스가 Spring 설정 클래스임을 나타냅니다.
 * WebMvcConfigurer 인터페이스를 구현하여 Spring MVC의 설정을 커스터마이징합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * application.properties 또는 application.yml에서
     * file.upload.directory 속성 값을 주입받습니다.
     * 파일이 업로드될 실제 디렉토리 경로를 저장합니다.
     */
    @Value("${file.upload.directory}")
    private String uploadDirectory;

    /**
     * 정적 리소스 핸들러를 추가하는 메소드
     * WebMvcConfigurer 인터페이스의 메소드를 오버라이드합니다.
     *
     * @param registry ResourceHandlerRegistry 객체를 통해 리소스 핸들러를 등록
     *
     * 설정 내용:
     * - /upload/** 패턴의 URL 요청이 들어오면
     * - file:{업로드 디렉토리}/ 경로에서 파일을 찾아 제공합니다.
     *
     * 예시:
     * - URL 요청: http://도메인/upload/image.jpg
     * - 실제 경로: {uploadDirectory}/image.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadDirectory + "/");
    }
}