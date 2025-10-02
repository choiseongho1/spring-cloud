package com.msa.member.controller;

import com.msa.member.domain.Member;
import com.msa.member.dto.MemberDto;
import com.msa.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 내부 서비스 간 통신을 위한 API 컨트롤러
 * 이 API는 게이트웨이를 통해 외부에서 접근할 수 없도록 설정해야 함
 */
@Slf4j
@RestController
@RequestMapping("/api/members/internal")
@RequiredArgsConstructor
public class InternalApiController {

    private final MemberService memberService;

    /**
     * 사용자 이름으로 회원 정보 조회 (내부 API)
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<MemberDto> getMemberByUsername(@PathVariable String username) {
        log.info("[내부 API] 사용자 이름으로 회원 조회 시작: {}", username);
        
        try {
            // 사용자 이름으로 회원 조회
            Optional<Member> memberOptional = memberService.findByUsername(username);
            
            if (memberOptional.isPresent()) {
                Member member = memberOptional.get();

                // DTO 변환
                MemberDto dto = MemberDto.from(member);

                return ResponseEntity.ok(dto);
            } else {
                log.warn("[내부 API] 회원 조회 실패 - 사용자 없음: {}", username);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("[내부 API] 회원 조회 중 오류 발생: {}, 오류: {}", username, e.getMessage());
            throw e;
        }
    }

    /**
     * ID로 회원 정보 조회 (내부 API)
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long id) {
        log.info("내부 API 호출: ID로 회원 조회 - {}", id);
        
        return memberService.getMemberById(id)
                .map(member -> ResponseEntity.ok(MemberDto.from(member)))
                .orElse(ResponseEntity.notFound().build());
    }
}
