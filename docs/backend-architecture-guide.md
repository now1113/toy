# backend-architecture-guide

## 1. 철학 및 전제

- 코드는 명확하고 예측 가능해야 하며, 변경에 강해야 한다.
- 도메인 객체는 외부 객체(DTO, Entity)에 의존하지 않는다.
- 비즈니스 로직은 도메인 내부에 위치하며, 그 외 계층은 흐름을 조율한다.
- 불변성(immutability)을 선호하며, 상태 변경보다는 새 객체 반환을 기본으로 한다.


## 2. 계층 구조

```text
Controller → Application → Service → Repository
     ↓             ↓           ↓         ↕
    DTO       Command/Query  Domain    Entity
```

- **Controller**: 외부 요청 수신 및 응답 Wrapping
- **Application**: 흐름 조율, 유효성 검사, 트랜잭션 처리 책임
- **Service**: 실질적인 비즈니스 로직을 수행하는 계층입니다. 도메인 객체와 리포지토리를 사용하여 로직을 수행.
- **Domain**: 순수한 비즈니스 규칙과 로직을 담는 객체, 상태 변경 시 이벤트를 생성하여 자신에게 등록(registerEvent)한다.
- **Entity**: 데이터베이스 테이블과 직접 매핑되는 객체.
- **Repository**: 데이터의 영속성(Persistence)을 책임진다. Domain ↔ Entity 변환을 위해 Mapper를 사용하며, 도메인 이벤트 발행(Event Dispatching)의 책임을 가진다.
- **Mapper**: Domain ↔ Entity 간의 상호 변환을 담당.

## 3. 도메인 공통 핵심 컴포넌트

### `AggregateRoot`
- **역할**: 애그리거트 경계의 루트로서, 상태 변경 과정에서 발생하는 도메인 이벤트를 수집, 관리
- **주요 메서드**
  - `registerEvent(DomainEvent event)`: 도메인 이벤트 등록
  - `getDomainEvents()`: 수집된 이벤트 읽기 전용 반환
  - `clearDomainEvents()`: 이벤트 버퍼 초기화
- **도입 이유**
  - **트랜잭션 경계 명확화**: 하나의 애그리거트 단위로 일관된 트랜잭션 처리 보장
  - **느슨한 후처리**: 이벤트 기반 후속 작업을 유연하게 연결

### `DomainEntity`
- **역할**: 모든 도메인 엔티티의 공통 기능(ID 기반 동등성, 해시코드)을 제공하는 추상 베이스 클래스
- **주요 기능**
  - `equals() / hashCode()`: `getId()` 값 기준 동등성 비교
  - `getId()`: 엔티티 고유 식별자 반환(추상 메서드)
- **도입 이유**
  - **일관된 식별 로직**: 엔티티 비교 시 중복 구현 제거
  - **안정성 향상**: 잘못된 동등성 비교로 인한 버그 예방

### `ValueObject`
- **역할**: 불변값 객체의 동등성 로직을 제공하는 추상 베이스 클래스
- **주요 기능**:
  - `equals() / hashCode()`: 내부 필드 배열(`getEqualityFields()`) 기반 비교
- **도입 이유**
    - **값 기반 동등성**: 객체 속성 값만으로 비교해 참조 공유 시 안전
    - **재사용성**: 다양한 값 객체에 공통 로직 적용 가능

### `LongTypeIdentifier`
- **역할**: Long 원시 ID를 래핑한 식별자 전용 값 객체(Value Object)
- **주요 기능**
  - `getEqualityFields()`: 동등성 비교용 필드 배열 제공 
  - `longValue()`: 내부 Long 값 반환
- **도입 이유**
  - **타입 안전성**: `UserId`,` OrderId` 등 서로 다른 ID를 구분 
  - **가독성 향상**: 로그·메서드 시그니처에 도메인 맥락 노출

### `DomainEvent`
- **역할**: 도메인 이벤트의 `마커 인터페이스`
- **도입 이유**
  - **추상화**: 모든 이벤트를 공통 타입으로 관리 
  - **확장성**: 신규 이벤트 구현체 추가만으로 즉시 통합 처리

### `DomainRepository`
- **역할**: 도메인 객체의 저장·조회·삭제 등 영속성 작업을 추상화한 포트(인터페이스)
- **주요 메서드**
  - `save(DOMAIN root)`
  - `Optional<DOMAIN> find(ID id)`
  - `removeById(ID id)`
  - `remove(DOMAIN root)`
- **도입 이유**
  - **계층 분리**: 도메인 서비스와 영속 계층 완전 분리
  - **테스트 용이**: 포트를 모킹해 도메인 로직 단위 테스트 용이

### `BaseJpaRepository`
- **역할**: `DomainRepository`를 JPA로 구현한 추상 클래스
- **주요 기능**
  - `save()`:` Domain → Entity` 변환, 저장, 이벤트 디스패치 포함
  - `find()`, `removeById()`, `remove()`: 공통 CRUD 처리
- **도입 이유**
  - **중복 제거**: 매핑·CRUD·이벤트 발행 로직을 한 곳에 집중
  - **생산성 향상**: 각 리포지토리 구현 시 도메인별 로직에만 집중

### `DomainEntityMapper`
- **역할**: 도메인 객체(`AggregateRoot`)와 JPA 엔티티(`@Entity`) 간 변환을 담당
- **주요 메서드**
  - `toDomain(ENTITY entity)`: 엔티티 → 도메인
  - `toEntity(DOMAIN domain)`: 도메인 → 엔티티
- **도입 이유**
  - **단일 책임**: 변환 로직을 중앙화해 유지보수·테스트 용이
  - **의존 역전**: 도메인 모델이 JPA 스펙에 의존하지 않음

## 4. 객체 변환 규칙

### DTO, Command, Domain 간의 변환

- **DTO → Command**: 외부 요청을 애플리케이션의 의도로 변환하는 과정입니다. DTO 내부에 `toCommand()` 메서드를 구현하여 변환 책임을 DTO가 갖습니다. 이는 의존성 규칙(외부→내부)을 위반하지 않습니다.
- **Command → Domain**: Application 계층에서 Command 객체를 사용하여 Domain 객체를 생성합니다. `User.create(command)`와 같이 Domain 객체의 정적 팩토리 메서드나 생성자를 사용합니다.
- **Domain → DTO**: Application 계층에서 로직 처리 후, 결과를 외부로 전달하기 위해 Domain 객체를 DTO로 변환합니다. `ResponseDto.from(domain)`과 같이 DTO의 정적 팩토리 메서드를 사용합니다.

### Domain ↔ Entity 변환 (Mapper 사용)

- `Domain ↔ Entity` 간의 변환은 영속성 계층과의 분리를 위해 **Mapper**에서 수행합니다.
- Mapper는 인터페이스로 정의하고, 이를 직접 구현한 클래스를 만들어 사용합니다. 이를 통해 변환 로직을 중앙에서 관리하고 테스트 용이성을 확보합니다.
- Mapper는 Repository 구현체에서 주로 사용됩니다.

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

```text
src/main/java/...
├── application         // 유스케이스 흐름 조율
├── controller          // REST API endpoint
├── domain              // 핵심 도메인 객체 (e.g. User.java)
├── dto                 // 요청/응답 DTO
├── entity              // JPA Entity (e.g. UserEntity.java)
├── enums               // Enum 타입들 (권한, 상태 등)
├── exception           // 비즈니스 예외 정의
├── mapper              // Domain ↔ Entity 변환
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

### 예외 구성 구조

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


### ErrorCode 인터페이스

```java
public interface ErrorCode extends Serializable {
    String getMessage();
    int getStatusCode();

    default HttpStatus getStatus() {
        return HttpStatus.valueOf(getStatusCode());
    }
}
```

### 공통 에러 코드 예시

```java
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommonErrorCode implements ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
}
```

### 비즈니스 예외 베이스 클래스

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

### 도메인별 예외 정의 예시 (User)

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

### GlobalExceptionHandler 예시

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
