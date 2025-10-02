package com.msa.common.auth.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Getter
public class GlobalCustomUserDetails implements UserDetails {
    private final String role;
    private final String userIdStr;
    private final Long userId;
    private final boolean enabled;

    /**
     * 사용자 ID를 Long 형태로 받는 생성자
     * 
     * @param role 사용자 역할
     * @param id 사용자 ID (Long 형태)
     */
    public GlobalCustomUserDetails(String role, Long id) {
        this.role = role;
        this.userId = id;
        this.userIdStr = id != null ? id.toString() : "";
        this.enabled = true;
    }

    /**
     * 사용자 ID를 문자열 형태로 받는 생성자
     * 
     * @param role 사용자 역할
     * @param idStr 사용자 ID (문자열 형태)
     */
    public GlobalCustomUserDetails(String role, String idStr) {
        this.role = role;
        this.userIdStr = idStr;
        
        // 문자열을 Long으로 변환 시도 (변환 실패 시 null)
        Long parsedId = null;
        try {
            if (idStr != null && !idStr.isEmpty()) {
                parsedId = Long.parseLong(idStr);
            }
        } catch (NumberFormatException e) {
            // 변환 실패 시 parsedId는 null로 유지
        }
        this.userId = parsedId;
        this.enabled = true;
    }

    /**
     * 사용자의 권한 정보를 반환합니다.
     * role이 이미 ROLE_ 접두사를 포함하고 있는 경우 그대로 사용하고,
     * 그렇지 않은 경우 ROLE_ 접두사를 추가합니다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // role이 이미 ROLE_로 시작하는지 확인
        String authority = (role != null && role.startsWith("ROLE_")) ? role : "ROLE_" + role;
        
        log.debug("[사용자 정보] 생성된 권한: {}", authority);
        
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }

    // 사용하지 않음
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userIdStr;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
