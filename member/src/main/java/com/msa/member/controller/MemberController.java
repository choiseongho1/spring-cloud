package com.msa.member.controller;

import com.msa.common.dto.ResponseDto;
import com.msa.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
