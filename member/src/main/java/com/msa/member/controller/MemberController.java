package com.msa.member.controller;

import com.msa.common.dto.ResponseDto;
import com.msa.member.dto.MemberPageDto;
import com.msa.member.dto.MemberSaveDto;
import com.msa.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Slf4j
@RestController
@RequestMapping("/api/members/")
@RequiredArgsConstructor

public class MemberController {

    final String CREATE_MEMBER_SUCCESS = "사용자 가입이 성공적으로 완료되었습니다.";
    final String SEARCH_MEMBER_SUCCESS = "사용자 조회가 성공적으로 완료되었습니다.";

    private final MemberService memberService;


    @PostMapping
    public ResponseEntity<ResponseDto<?>> createMember(@Valid @RequestBody MemberSaveDto memberSaveDto) {
        memberService.createMember(memberSaveDto);

        ResponseDto<?> success = ResponseDto.success(CREATE_MEMBER_SUCCESS);
        return ResponseEntity.ok(success);
    }

    @GetMapping("paging")
    public ResponseEntity<?> findMemberListWithPaging(@PageableDefault Pageable pageable) {

        Page<MemberPageDto> page = memberService.findMemberListWithPaging(pageable);
        ResponseDto<Page<MemberPageDto>> success = ResponseDto.success(SEARCH_MEMBER_SUCCESS, page);
        return ResponseEntity.ok(success);
    }
}
