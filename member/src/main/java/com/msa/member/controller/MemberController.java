package com.msa.member.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
