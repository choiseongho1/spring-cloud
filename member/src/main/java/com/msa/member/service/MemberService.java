package com.msa.member.service;

import com.msa.member.domain.Member;
import com.msa.member.dto.MemberPageDto;
import com.msa.member.dto.MemberSaveDto;
import com.msa.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 회원 생성 (비밀번호 암호화 적용)
     */
    @Transactional
    public void createMember(MemberSaveDto memberSaveDto) {
        Optional<Member> opMember = findByUsername(memberSaveDto.getUsername());
        opMember.ifPresent(member -> {
            throw new RuntimeException("Member already exists with username: " + memberSaveDto.getUsername());
        });


        // 비밀번호 암호화
        if (!StringUtils.isEmpty(memberSaveDto.getPassword())) {
            String hashedPassword = passwordEncoder.encode(memberSaveDto.getPassword());
            memberSaveDto.setPassword(hashedPassword);
        }


        Member savedMember = memberRepository.save(memberSaveDto.toEntity());
    }



    /**
     * 모든 회원 조회
     */
    @Transactional(readOnly = true)
    public Page<MemberPageDto> findMemberListWithPaging(Pageable pageable) {
        return memberRepository.findMemberListWithPaging(pageable);
    }



















    // ---------------------------------------------------------------------------------------------------
    /**
     * 회원 ID로 조회 
     */
    @Transactional(readOnly = true)
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }
    
    /**
     * 사용자 이름으로 회원 조회 
     */
    @Transactional(readOnly = true)
    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }
}
