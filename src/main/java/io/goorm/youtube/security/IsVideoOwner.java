package io.goorm.youtube.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@videoAuthChecker.isOwner(#videoSeq)")  // 여기서 @videoAuthChecker는
public @interface IsVideoOwner {}                       // Spring Bean의 이름을 참조

