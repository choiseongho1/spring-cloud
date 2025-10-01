package com.msa.member.service;

import com.msa.member.domain.Member;
import com.msa.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    
    /**
     * 회원 생성 (Master 데이터소스 사용)
     */
    @Transactional
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }
    
    /**
     * 회원 수정 (Master 데이터소스 사용)
     */
    @Transactional
    public Member updateMember(Long id, Member memberDetails) {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        
        existingMember.setName(memberDetails.getName());
        existingMember.setEmail(memberDetails.getEmail());
        existingMember.setAge(memberDetails.getAge());
        
        return memberRepository.save(existingMember);
    }
    
    /**
     * 회원 삭제 (Master 데이터소스 사용)
     */
    @Transactional
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
    
    /**
     * 모든 회원 조회
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
    
    /**
     * 회원 ID로 조회 (Slave 데이터소스 사용 - @Transactional(readOnly = true) 사용)
     */
    @Transactional(readOnly = true)
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }
}
