package com.msa.member.service;

import com.msa.member.domain.Member;
import com.msa.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public Member createMember(Member member) {
        // 비밀번호 암호화
        if (member.getPassword() != null && !member.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(member.getPassword());
            member.setPassword(hashedPassword);
            log.info("비밀번호 암호화 완료: {}", member.getUsername());
        }
        
        return memberRepository.save(member);
    }
    
    /**
     * 회원 수정 (비밀번호 변경 시 암호화 적용)
     */
    @Transactional
    public Member updateMember(Long id, Member memberDetails) {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        
        existingMember.setName(memberDetails.getName());
        existingMember.setEmail(memberDetails.getEmail());
        existingMember.setAge(memberDetails.getAge());
        
        // 비밀번호가 변경된 경우 암호화 적용
        if (memberDetails.getPassword() != null && !memberDetails.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(memberDetails.getPassword());
            existingMember.setPassword(hashedPassword);
            log.info("비밀번호 변경 및 암호화 완료: {}", existingMember.getUsername());
        }
        
        return memberRepository.save(existingMember);
    }
    
    /**
     * 회원 삭제 
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
