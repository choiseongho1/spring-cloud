package com.msa.member.controller;

import com.msa.common.dto.ResponseDto;
import com.msa.member.dto.MemberSaveDto;
import com.msa.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/members/")
@RequiredArgsConstructor
public class MemberController {

    final String CREATE_MEMBER_SUCCESS = "사용자 가입이 성공적으로 완료되었습니다.";

    private final MemberService memberService;


    @PostMapping
    public ResponseEntity<ResponseDto<?>> createMember(@Valid @RequestBody MemberSaveDto memberSaveDto) {
        memberService.createMember(memberSaveDto);

        ResponseDto<?> success = ResponseDto.success(CREATE_MEMBER_SUCCESS);
        return ResponseEntity.ok(success);
    }
}
