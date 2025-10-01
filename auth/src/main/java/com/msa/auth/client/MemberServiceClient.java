package com.msa.auth.client;

import com.msa.auth.client.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member-service", url = "http://localhost:10001")
public interface MemberServiceClient {

    @GetMapping("/api/members/internal/username/{username}")
    MemberDto getMemberByUsername(@PathVariable String username);
}