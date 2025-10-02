package com.msa.common.util;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Function;

/**
 * QueryDSL 조건식 유틸리티 클래스
 * 
 * 동적 쿼리 작성 시 자주 사용되는 조건식을 편리하게 사용할 수 있도록 지원
 */
public class QueryUtils {
    
    /**
     * 문자열 필드에 대한 equals 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression eq(String value, StringPath field) {
        return value != null ? field.eq(value) : null;
    }
    
    /**
     * 숫자 필드에 대한 equals 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static <T extends Number & Comparable<?>> BooleanExpression eq(T value, NumberPath<T> field) {
        return value != null ? field.eq(value) : null;
    }
    
    /**
     * Boolean 필드에 대한 equals 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression eq(Boolean value, BooleanPath field) {
        return value != null ? field.eq(value) : null;
    }
    
    /**
     * LocalDateTime 필드에 대한 equals 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression eq(LocalDateTime value, DateTimePath<LocalDateTime> field) {
        return value != null ? field.eq(value) : null;
    }
    
    /**
     * LocalDate 필드에 대한 equals 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression eq(LocalDate value, DatePath<LocalDate> field) {
        return value != null ? field.eq(value) : null;
    }
    
    /**
     * Enum 필드에 대한 equals 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static <T extends Enum<T>> BooleanExpression eq(T value, EnumPath<T> field) {
        return value != null ? field.eq(value) : null;
    }
    
    /**
     * 문자열 필드에 대한 contains 조건 (null 안전)
     * 
     * @param value 포함 여부를 확인할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression contains(String value, StringPath field) {
        return value != null ? field.contains(value) : null;
    }
    
    /**
     * 문자열 필드에 대한 startsWith 조건 (null 안전)
     * 
     * @param value 시작 문자열
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression startsWith(String value, StringPath field) {
        return value != null ? field.startsWith(value) : null;
    }
    
    /**
     * 문자열 필드에 대한 endsWith 조건 (null 안전)
     * 
     * @param value 끝 문자열
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression endsWith(String value, StringPath field) {
        return value != null ? field.endsWith(value) : null;
    }
    
    /**
     * 문자열 필드에 대한 대소문자 무시 contains 조건 (null 안전)
     * 
     * @param value 포함 여부를 확인할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression containsIgnoreCase(String value, StringPath field) {
        return value != null ? field.containsIgnoreCase(value) : null;
    }
    
    /**
     * 숫자 필드에 대한 크거나 같음(>=) 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static <T extends Number & Comparable<?>> BooleanExpression goe(T value, NumberPath<T> field) {
        return value != null ? field.goe(value) : null;
    }
    
    /**
     * 숫자 필드에 대한 작거나 같음(<=) 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static <T extends Number & Comparable<?>> BooleanExpression loe(T value, NumberPath<T> field) {
        return value != null ? field.loe(value) : null;
    }
    
    /**
     * LocalDateTime 필드에 대한 크거나 같음(>=) 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression goe(LocalDateTime value, DateTimePath<LocalDateTime> field) {
        return value != null ? field.goe(value) : null;
    }
    
    /**
     * LocalDateTime 필드에 대한 작거나 같음(<=) 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression loe(LocalDateTime value, DateTimePath<LocalDateTime> field) {
        return value != null ? field.loe(value) : null;
    }
    
    /**
     * LocalDate 필드에 대한 크거나 같음(>=) 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression goe(LocalDate value, DatePath<LocalDate> field) {
        return value != null ? field.goe(value) : null;
    }
    
    /**
     * LocalDate 필드에 대한 작거나 같음(<=) 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression loe(LocalDate value, DatePath<LocalDate> field) {
        return value != null ? field.loe(value) : null;
    }
    
    /**
     * 컬렉션에 포함 여부 조건 (in) (null 안전)
     * 
     * @param values 값 컬렉션
     * @param field 필드 표현식
     * @return 조건식 (값이 null이거나 비어있으면 null 반환)
     */
    public static <T> BooleanExpression in(Collection<T> values, SimpleExpression<T> field) {
        return values != null && !values.isEmpty() ? field.in(values) : null;
    }
    
    /**
     * Between 조건 (null 안전)
     * 
     * @param from 시작 값
     * @param to 종료 값
     * @param field 필드 표현식
     * @return 조건식 (from 또는 to가 null이면 null 반환)
     */
    public static <T extends Comparable<?>> BooleanExpression between(T from, T to, ComparableExpression<T> field) {
        return from != null && to != null ? field.between(from, to) : null;
    }
    
    /**
     * LocalDateTime 필드에 대한 Between 조건 (null 안전)
     * 
     * @param from 시작 시간
     * @param to 종료 시간
     * @param field 필드 표현식
     * @return 조건식 (from 또는 to가 null이면 null 반환)
     */
    public static BooleanExpression between(LocalDateTime from, LocalDateTime to, DateTimePath<LocalDateTime> field) {
        return from != null && to != null ? field.between(from, to) : null;
    }
    
    /**
     * LocalDate 필드에 대한 Between 조건 (null 안전)
     * 
     * @param from 시작 날짜
     * @param to 종료 날짜
     * @param field 필드 표현식
     * @return 조건식 (from 또는 to가 null이면 null 반환)
     */
    public static BooleanExpression between(LocalDate from, LocalDate to, DatePath<LocalDate> field) {
        return from != null && to != null ? field.between(from, to) : null;
    }
    
    /**
     * 특정 날짜가 시작일과 종료일 사이에 있는지 확인하는 Between 조건 (null 안전)
     * 
     * @param startDateField 시작일 필드
     * @param endDateField 종료일 필드
     * @param date 확인할 날짜
     * @return 조건식 (date가 null이면 null 반환)
     */
    public static BooleanExpression between(DatePath<LocalDate> startDateField, DatePath<LocalDate> endDateField, LocalDate date) {
        if (date == null) {
            return null;
        }
        return startDateField.loe(date).and(endDateField.goe(date));
    }
    
    /**
     * 특정 날짜가 시작일과 종료일 사이에 있는지 확인하는 Between 조건 (null 안전)
     * 
     * @param startDateTimeField 시작시간 필드
     * @param endDateTimeField 종료시간 필드
     * @param dateTime 확인할 시간
     * @return 조건식 (dateTime이 null이면 null 반환)
     */
    public static BooleanExpression between(DateTimePath<LocalDateTime> startDateTimeField, DateTimePath<LocalDateTime> endDateTimeField, LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return startDateTimeField.loe(dateTime).and(endDateTimeField.goe(dateTime));
    }
    
    /**
     * 특정 날짜가 시작 시간과 종료 시간 사이에 있는지 확인하는 Between 조건 (null 안전)
     * LocalDate와 LocalDateTime 간의 비교를 지원합니다.
     * 
     * @param startTimeField 시작 시간 필드
     * @param endTimeField 종료 시간 필드
     * @param date 확인할 날짜
     * @return 조건식 (date가 null이면 null 반환)
     */
    public static BooleanExpression between(DateTimePath<LocalDateTime> startTimeField, 
                                           DateTimePath<LocalDateTime> endTimeField, 
                                           LocalDate date) {
        if (date == null) {
            return null;
        }
        
        // 날짜의 시작(00:00:00)
        LocalDateTime startOfDay = date.atStartOfDay();
        // 날짜의 끝(23:59:59.999999999)
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);
        
        // 상영 시간이 해당 날짜와 겹치는지 확인
        // (상영 시작 시간이 날짜의 끝보다 이전이고, 상영 종료 시간이 날짜의 시작보다 이후인 경우)
        return startTimeField.loe(endOfDay).and(endTimeField.goe(startOfDay));
    }
    
    /**
     * 문자열 필드에 대한 like 조건 (null 안전)
     * 
     * @param value 검색할 값 (% 포함 가능)
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression like(String value, StringPath field) {
        return value != null ? field.like(value) : null;
    }
    
    /**
     * 문자열 필드에 대한 대소문자 무시 like 조건 (null 안전)
     * 
     * @param value 검색할 값 (% 포함 가능)
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression likeIgnoreCase(String value, StringPath field) {
        return value != null ? field.likeIgnoreCase(value) : null;
    }
    
    /**
     * 숫자 필드에 대한 초과(>) 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static <T extends Number & Comparable<?>> BooleanExpression gt(T value, NumberPath<T> field) {
        return value != null ? field.gt(value) : null;
    }
    
    /**
     * 숫자 필드에 대한 미만(<) 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static <T extends Number & Comparable<?>> BooleanExpression lt(T value, NumberPath<T> field) {
        return value != null ? field.lt(value) : null;
    }
    
    /**
     * LocalDate 필드에 대한 초과(>) 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression gt(LocalDate value, DatePath<LocalDate> field) {
        return value != null ? field.gt(value) : null;
    }
    
    /**
     * LocalDate 필드에 대한 미만(<) 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression lt(LocalDate value, DatePath<LocalDate> field) {
        return value != null ? field.lt(value) : null;
    }
    
    /**
     * LocalDateTime 필드에 대한 초과(>) 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression gt(LocalDateTime value, DateTimePath<LocalDateTime> field) {
        return value != null ? field.gt(value) : null;
    }
    
    /**
     * LocalDateTime 필드에 대한 미만(<) 조건 (null 안전)
     * 
     * @param value 비교할 값
     * @param field 필드 표현식
     * @return 조건식 (값이 null이면 null 반환)
     */
    public static BooleanExpression lt(LocalDateTime value, DateTimePath<LocalDateTime> field) {
        return value != null ? field.lt(value) : null;
    }
    
    /**
     * 여러 조건식을 AND 연산으로 결합 (null 안전)
     * null인 조건은 무시됨
     * 
     * @param predicates 조건식 배열
     * @return 결합된 조건식
     */
    public static BooleanBuilder and(Predicate... predicates) {
        BooleanBuilder builder = new BooleanBuilder();
        
        for (Predicate predicate : predicates) {
            if (predicate != null) {
                builder.and(predicate);
            }
        }
        
        return builder;
    }
    
    /**
     * 여러 조건식을 OR 연산으로 결합 (null 안전)
     * null인 조건은 무시됨
     * 
     * @param predicates 조건식 배열
     * @return 결합된 조건식
     */
    public static BooleanBuilder or(Predicate... predicates) {
        BooleanBuilder builder = new BooleanBuilder();
        
        boolean first = true;
        for (Predicate predicate : predicates) {
            if (predicate != null) {
                if (first) {
                    builder.and(predicate);
                    first = false;
                } else {
                    builder.or(predicate);
                }
            }
        }
        
        return builder;
    }
    
    /**
     * 동적 정렬 조건 생성
     * 
     * @param property 정렬 속성명
     * @param direction 정렬 방향 ("asc" 또는 "desc")
     * @param paths 정렬 가능한 경로 맵
     * @return 정렬 표현식
     */
    public static <T> OrderSpecifier<?> getOrderSpecifier(String property, String direction,
                                                          Function<String, Expression<?>> pathResolver) {
        Expression<?> path = pathResolver.apply(property);
        
        if (path == null) {
            throw new IllegalArgumentException("Unknown sort property: " + property);
        }
        
        Order order = "desc".equalsIgnoreCase(direction) ? Order.DESC : Order.ASC;
        
        return new OrderSpecifier(order, path);
    }
    
    /**
     * 문자열이 null이거나 빈 문자열인지 확인
     * 
     * @param value 확인할 문자열
     * @return true: null이거나 빈 문자열, false: 그 외
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * 문자열이 null이 아니고 빈 문자열이 아닌지 확인
     * 
     * @param value 확인할 문자열
     * @return true: null이 아니고 빈 문자열이 아님, false: 그 외
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }
    
    /**
     * 문자열이 "Y"인지 확인 (대소문자 구분 없이)
     * 
     * @param value 확인할 문자열
     * @return true: "Y" 또는 "y", false: 그 외
     */
    public static boolean isYes(String value) {
        return "Y".equalsIgnoreCase(value);
    }
}
