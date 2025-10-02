package com.msa.member.repository;

import com.msa.member.dto.MemberPageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberDslRepository {


    Page<MemberPageDto> findMemberListWithPaging(Pageable pageable);
}
