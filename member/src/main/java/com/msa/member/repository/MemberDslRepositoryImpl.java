package com.msa.member.repository;

import com.msa.common.util.QueryUtils;
import com.msa.member.domain.Member;
import com.msa.member.dto.MemberPageDto;
import com.msa.member.dto.QMemberPageDto;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.msa.member.domain.QMember.member;

/**
 * MemberDslRepository 구현체
 */
@Repository
@RequiredArgsConstructor
public class MemberDslRepositoryImpl implements MemberDslRepository {

    private final JPAQueryFactory queryFactory;


    // ------------------------------------------------------------------------------------------------------------------------------------------//

    @Override
    public Page<MemberPageDto> findMemberListWithPaging(Pageable pageable) {
        // 전체 카운트 조회
        final Long totalCount = findMemberListCount().fetchOne();

        // 데이터 조회
        final List<MemberPageDto> content = findMemberList()
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        return new PageImpl<>(content, pageable, (totalCount==null)? 0 : totalCount);

    }

    // findMemberListWithPaging 전체 카운트 조회
    private JPAQuery<Long> findMemberListCount(){
        return queryFactory
                .select(member.count())
                .from(member)
                .where(memberListSearchCondition());
    }

    // findMemberListWithPaging 목록 조회
    private JPAQuery<MemberPageDto> findMemberList(){
        return queryFactory
                .select(new QMemberPageDto(
                        member.id,
                        member.username,
                        member.name,
                        member.email,
                        member.age,
                        member.role
                ))
                .from(member)
                .where(memberListSearchCondition());
    }

    // findMemberListWithPaging 조회 조건
    private Predicate[] memberListSearchCondition(){
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(QueryUtils.eq(Member.Role.ROLE_USER , member.role));

        return predicates.toArray(new Predicate[0]);
    }

}
