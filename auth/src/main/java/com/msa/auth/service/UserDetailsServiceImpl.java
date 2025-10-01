package com.msa.auth.service;

import com.msa.auth.client.MemberServiceClient;
import com.msa.auth.client.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberServiceClient memberServiceClient;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            log.info("[인증] 사용자 정보 조회 시작: {}", username);
            
            // Member 서비스에서 사용자 정보 조회
            MemberDto memberDto = memberServiceClient.getMemberByUsername(username);
            log.info("[인증] Member 서비스에서 사용자 정보 조회 성공: {}", username);
            log.debug("[인증] 사용자 정보 상세: id={}, username={}, 비밀번호 존재={}, 이메일={}", 
                    memberDto.getId(), memberDto.getUsername(), 
                    (memberDto.getPassword() != null ? "O" : "X"), memberDto.getEmail());
            
            // 비밀번호 로깅 (주의: 실제 환경에서는 절대 비밀번호를 로깅하면 안됨)
            if (memberDto.getPassword() == null || memberDto.getPassword().isEmpty()) {
                log.error("[인증] 비밀번호가 비어있습니다: {}", username);
            } else {
                log.debug("[인증] 비밀번호 형식: {}", 
                        memberDto.getPassword().startsWith("$2") ? "BCrypt" : "암호화되지 않음");
            }
            
            // 사용자 정보로 UserDetails 생성
            UserDetails userDetails = new User(
                    memberDto.getUsername(),
                    memberDto.getPassword() != null ? memberDto.getPassword() : "", // null 처리
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            
            log.info("[인증] UserDetails 생성 성공: {}, 권한: {}", 
                    userDetails.getUsername(), userDetails.getAuthorities());
            
            return userDetails;
        } catch (Exception e) {
            log.error("[인증] Member 서비스 호출 중 오류 발생: {}, 오류 메시지: {}", username, e.getMessage());
            log.error("[인증] 오류 상세", e); // 스택 트레이스 출력
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }
    }
}
