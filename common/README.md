# MSA Common 프로젝트

이 프로젝트는 마이크로서비스 아키텍처(MSA)에서 여러 서비스 간에 공통으로 사용되는 컴포넌트들을 제공합니다.

## 주요 기능

### 공통 DTO 클래스
- `ResponseDto`: API 응답을 위한 표준 형식 제공
- `ErrorDto`: 오류 정보를 위한 표준 형식 제공
- `PageRequestDto`: 페이지네이션 요청을 위한 표준 형식 제공
- `PageResponseDto`: 페이지네이션 응답을 위한 표준 형식 제공

### 공통 유틸리티 클래스
- `DateTimeUtil`: 날짜/시간 관련 유틸리티 함수 제공
- `StringUtil`: 문자열 관련 유틸리티 함수 제공
- `JsonUtil`: JSON 직렬화/역직렬화 관련 유틸리티 함수 제공

### 예외 처리 공통 클래스
- `BaseException`: 모든 예외의 기본 클래스
- `BusinessException`: 비즈니스 로직 관련 예외
- `EntityNotFoundException`: 엔티티를 찾을 수 없을 때 발생하는 예외
- `InvalidValueException`: 유효하지 않은 값에 대한 예외
- `ErrorCode`: 표준화된 오류 코드 정의
- `GlobalExceptionHandler`: 전역 예외 처리기

### 보안 관련 클래스
- `JwtTokenProvider`: JWT 토큰 생성 및 검증
- `JwtFilter`: JWT 인증 필터
- `JwtAuthenticationEntryPoint`: 인증 실패 처리
- `JwtAccessDeniedHandler`: 접근 거부 처리
- `SecurityConfig`: 보안 설정

## 사용 방법

### 의존성 추가
다른 서비스에서 common 프로젝트를 사용하려면 build.gradle 파일에 다음과 같이 의존성을 추가합니다:

```gradle
dependencies {
    implementation project(':common')
}
```

### 예외 처리 예시
```java
// 비즈니스 예외 발생
throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 입력값입니다.");

// 엔티티를 찾을 수 없을 때
throw new EntityNotFoundException("User", userId);
```

### JWT 토큰 사용 예시
```java
// 토큰 생성
String token = jwtTokenProvider.createToken(authentication);

// 토큰 검증
boolean isValid = jwtTokenProvider.validateToken(token);

// 인증 정보 추출
Authentication auth = jwtTokenProvider.getAuthentication(token);
```

## 테스트
- `DateTimeUtilTest`: 날짜/시간 유틸리티 테스트
- `StringUtilTest`: 문자열 유틸리티 테스트
- `JwtTokenProviderTest`: JWT 토큰 제공자 테스트
