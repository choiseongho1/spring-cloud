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
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberServiceClient memberServiceClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // Member 서비스에서 사용자 정보 조회
            MemberDto memberDto = memberServiceClient.getMemberByUsername(username);

            // 비밀번호 유효성 간단히 검사
            if (memberDto.getPassword() == null || memberDto.getPassword().isEmpty()) {
                log.error("[인증] 비밀번호가 비어있습니다: {}", username);
            }
            
            // 사용자 정보로 UserDetails 생성
            UserDetails userDetails = new User(
                    memberDto.getUsername(),
                    memberDto.getPassword() != null ? memberDto.getPassword() : "", // null 처리
                    Collections.singletonList(new SimpleGrantedAuthority(memberDto.getRole()))
            );
            
            return userDetails;
        } catch (Exception e) {
            log.error("[인증] Member 서비스 호출 중 오류 발생: {}, 오류 메시지: {}", username, e.getMessage());
            log.error("[인증] 오류 상세", e); // 스택 트레이스 출력
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }
    }
}
