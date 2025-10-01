package com.msa.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {


    @GetMapping("/ping")
    public String ping() {
        return "members Ping Success";
    }

}
