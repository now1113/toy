package site.kimnow.toy.user.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.kimnow.toy.common.exception.ErrorCode;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserErrorCode implements ErrorCode {

    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호 확인이 일치하지 않습니다"),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호는 특수문자를 포함하고 15자 이하로 입력해야 합니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "아이디 또는 비밀번호가 일치하지 않습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다.")
    ;
    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int getStatusCode() {
        return status.value();
    }
}
