package com.backend.seperate.configuration.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    
    private SecurityUtil() {}

    public static Long getCurrentMemberId() {
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("::::::::::::    SecurityUtil-getCurrentMemberId     :::::::::::");
        
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        return Long.parseLong(authentication.getName());
    }
}
