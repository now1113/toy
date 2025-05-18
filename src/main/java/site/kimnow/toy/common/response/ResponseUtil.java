package site.kimnow.toy.common.response;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import site.kimnow.toy.common.exception.ErrorCode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseUtil {

    public static <T> ResponseEntity<CommonResponse<T>> ok(T data) {
        return ResponseEntity.ok(CommonResponse.success(data));
    }

    public static <T> ResponseEntity<CommonResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(CommonResponse.success(data, message));
    }

    public static <T> ResponseEntity<CommonResponse<T>> fail(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(CommonResponse.fail(errorCode));
    }

    public static <T> ResponseEntity<CommonResponse<T>> fail(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(CommonResponse.fail(message, status));
    }
}
