package com.msa.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 비밀번호 암호화 유틸리티 클래스
 * Spring Security의 BCryptPasswordEncoder를 사용하여 일관된 암호화 및 검증 제공
 */
public class PasswordUtil {
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * 비밀번호를 BCrypt로 암호화
     * 
     * @param password 평문 비밀번호
     * @return 암호화된 비밀번호
     */
    public static String hashPassword(String password) {
        return encoder.encode(password);
    }
    
    /**
     * 비밀번호 검증
     * 
     * @param password 평문 비밀번호
     * @param hashedPassword 암호화된 비밀번호
     * @return 일치 여부
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return encoder.matches(password, hashedPassword);
    }
}
