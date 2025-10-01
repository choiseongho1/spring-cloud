package com.msa.member.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msa.common.dto.ResponseDto;
import com.msa.member.domain.Member;
import com.msa.member.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/ping")
    public ResponseDto<?> ping() {
        ResponseDto<?> test = ResponseDto.success("members Ping Success");
        return test;
    }

    @GetMapping("/ping2")
    public ResponseDto<?> ping2() {
        ResponseDto<?> test = ResponseDto.success("members Ping Success");
        return test;
    }
    
    /**
     * 회원 생성 (Master 데이터소스 사용)
     */
    @PostMapping
    public ResponseEntity<ResponseDto<Member>> createMember(@RequestBody Member member) {
        Member createdMember = memberService.createMember(member);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("회원이 성공적으로 생성되었습니다.", createdMember));
    }
    
    /**
     * 회원 수정 (Master 데이터소스 사용)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<Member>> updateMember(
            @PathVariable Long id, 
            @RequestBody Member member) {
        Member updatedMember = memberService.updateMember(id, member);
        return ResponseEntity.ok(ResponseDto.success("회원 정보가 성공적으로 수정되었습니다.", updatedMember));
    }
    
    /**
     * 회원 삭제 (Master 데이터소스 사용)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok(ResponseDto.success("회원이 성공적으로 삭제되었습니다.", null));
    }
    
    /**
     * 모든 회원 조회 (Slave 데이터소스 사용 - @ReadOnly 어노테이션)
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<Member>>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(ResponseDto.success("모든 회원 정보를 성공적으로 조회했습니다.", members));
    }
    
    /**
     * 회원 ID로 조회 (Slave 데이터소스 사용 - @Transactional(readOnly = true))
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<Member>> getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .map(member -> ResponseEntity.ok(ResponseDto.success("회원 정보를 성공적으로 조회했습니다.", member)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.fail("해당 ID의 회원을 찾을 수 없습니다.")));
    }
}
