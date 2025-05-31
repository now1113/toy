# backend-architecture-guide

## 1. 철학 및 전제

- 코드는 명확하고 예측 가능해야 하며, 변경에 강해야 한다.
- 도메인 객체는 외부 객체(DTO, Entity)에 의존하지 않는다.
- 비즈니스 로직은 도메인 내부에 위치하며, 그 외 계층은 흐름을 조율한다.
- 불변성(immutability)을 선호하며, 상태 변경보다는 새 객체 반환을 기본으로 한다.


## 2. 계층 구조

```text
Controller → Application → Domain → Repository
                   ↓             ↕
               Mapper (DTO ↔ Domain ↔ Entity)
```

- **Controller**: 외부 요청 수신 및 응답 Wrapping
- **Application**: 흐름 조율, 유효성 검사, 트랜잭션 처리 책임
- **Service**: 도메인 로직 수행 및 외부 컴포넌트 호출
- **Domain**: 비즈니스 로직을 담당하는 순수 객체
- **Entity**: DB 매핑 전용 객체
- **Repository**: JPA 인터페이스 및 어댑터 구성
- **Mapper**: DTO ↔ Domain, Domain ↔ Entity 간 변환 담당

## 3. 도메인 객체 불변성 유지

- setter 사용을 지양하고, 상태 변경이 필요한 경우 `withXxx()` 형태의 새 인스턴스를 반환한다.
- 예)

```java
public User withEncodedPassword(String encodedPassword) {
    return User.builder()
        .userId(this.userId)
        .email(this.email)
        .name(this.name)
        .password(encodedPassword)
        .authority(this.authority)
        .isDeleted(this.isDeleted)
        .build();
}
```

## 4. Mapper 사용 규칙

- DTO ↔ Domain, Entity ↔ Domain 변환은 모두 `Mapper`에서 수행
- Mapper는 상태 없는 `static` 메서드만 사용 (Spring Bean으로 등록하지 않음)
- Mapper는 Application Layer뿐만 아니라 RepositoryAdapter 등 외부 계층에서 도메인을 안전하게 변환하기 위해 사용된다.
- Mapper는 Domain ↔ DTO, Domain ↔ Entity 간 변환을 모두 담당하되, Domain은 외부 객체에 직접적으로 의존하지 않는다. 즉, 변환은 가능하되, 의존은 단방향이다.

## 5. Response 처리 원칙

- 모든 응답은 `CommonResponse<T>` 형태로 감싼다.
- Controller는 항상 `ResponseEntity<CommonResponse<T>>`를 반환

예)

```java
@PostMapping
public ResponseEntity<CommonResponse<UserJoinResponse>> join(...) {
    return ResponseUtil.ok(response);
}
```

## 6. 디렉토리 구성

```java
src/main/java/...
├── application         // 유스케이스 흐름 조율
├── controller          // REST API endpoint
├── domain              // 핵심 도메인 객체 (e.g. User.java)
├── dto                 // 요청/응답 DTO
├── entity              // JPA Entity (e.g. UserEntity.java)
├── enums               // Enum 타입들 (권한, 상태 등)
├── exception           // 비즈니스 예외 정의
├── mapper              // DTO ↔ Domain ↔ Entity 변환
├── repository          // Repository interface 및 Adapter
├── service             // 도메인 로직 수행
```

### 설계 원칙

- Entity와 Domain은 역할과 책임이 다르므로 분리
- Domain은 외부 계층을 전혀 알지 않도록 Mapper를 두어 간접 연결
- DTO는 외부 입출력 전용으로만 사용되며 내부 로직에 사용하지 않음
- Repository는 interface -> adapter 패턴으로 설계하여 확장 가능성 확보


## 7. Exception 설계 가이드

### 기본 원칙

- 모든 도메인 예외는 `BusinessException`을 상속한다.
- 예외 코드는 `ErrorCode` 인터페이스를 구현한 Enum으로 관리한다.
- 에러 메세지와 상태 코드는 Enum이 책임진다.
- 공통 에러는 `CommonErrorCode`, 도메인별 에러는 `{Domain}ErrorCode`등으로 분리한다.
- 예외 응답은 `GlobalExceptionHanlder`에서 통일된 형식으로 처리한다.

## 예외 구성 구조

```text
exception
├── BusinessException.java         // 모든 커스텀 예외의 부모
├── ErrorCode.java                 // 상태코드, 메시지를 갖는 인터페이스
├── CommonErrorCode.java          // 공통 예외코드 모음
├── GlobalExceptionHandler.java   // 예외 핸들러
└── user
    ├── UserErrorCode.java        // 도메인별 에러코드
    ├── UserNotFoundException.java
    └── DuplicateEmailException.java
```


## ErrorCode 인터페이스

```java
public interface ErrorCode extends Serializable {
    String getMessage();
    int getStatusCode();

    default HttpStatus getStatus() {
        return HttpStatus.valueOf(getStatusCode());
    }
}
```

## 공통 에러 코드 예시

```java
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommonErrorCode implements ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
}
```

## 비즈니스 예외 베이스 클래스

```java
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(String.format("%s : %s", errorCode.getMessage(), detailMessage));
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
```

## 도메인별 예외 정의 예시 (User)

```java
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserErrorCode implements ErrorCode {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "아이디 또는 비밀번호가 일치하지 않습니다");
}

```
```java
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String email) {
        super(UserErrorCode.DUPLICATE_EMAIL, String.format("email=%s", email));
    }
}
```

## GlobalExceptionHandler 예시

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Void>> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("BusinessException: {}", ex.getMessage());
        return ResponseUtil.fail(errorCode.getMessage(), errorCode.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = getErrorMessage(ex.getBindingResult());
        return ResponseUtil.fail(CommonErrorCode.INVALID_INPUT.getMessage() + " : " + errorMessage,
                                 CommonErrorCode.INVALID_INPUT.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseUtil.fail(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }

    private String getErrorMessage(BindingResult bindingResult) {
        ...
    }
}
```
