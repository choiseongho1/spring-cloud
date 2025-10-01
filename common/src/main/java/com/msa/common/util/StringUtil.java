package com.msa.common.util;

import java.util.UUID;
import java.util.regex.Pattern;

public class StringUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{3}-\\d{3,4}-\\d{4}$");
    
    private StringUtil() {
        throw new IllegalStateException("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }
    
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }
    
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber).matches();
    }
    
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
    
    public static String maskEmail(String email) {
        if (isEmpty(email)) {
            return email;
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        
        String name = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        int nameLength = name.length();
        int maskLength = Math.max(nameLength / 2, 1);
        int visibleLength = nameLength - maskLength;
        
        String visiblePart = name.substring(0, visibleLength);
        String maskedPart = "*".repeat(maskLength);
        
        return visiblePart + maskedPart + domain;
    }
    
    public static String maskPhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        
        String[] parts = phoneNumber.split("-");
        if (parts.length != 3) {
            return phoneNumber;
        }
        
        return parts[0] + "-****-" + parts[2];
    }
}
