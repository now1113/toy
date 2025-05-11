package site.kimnow.toy.common.response;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import site.kimnow.toy.common.exception.ErrorCode;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonResponse<T> {

    private final int status;
    private final T data;
    private final String message;

    // 성공
    public static <T> ResponseEntity<CommonResponse<T>> success() {
        return success(null, "SUCCESS");
    }

    // 성공
    public static <T> ResponseEntity<CommonResponse<T>> success(T data) {
        return ResponseEntity.ok(
                CommonResponse.<T>builder()
                        .status(HttpStatus.OK.value())
                        .data(data)
                        .build()
        );
    }

    // 성공
    public static <T> ResponseEntity<CommonResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(
                CommonResponse.<T>builder()
                        .status(HttpStatus.OK.value())
                        .data(data)
                        .message(message)
                        .build()
        );
    }

    // 실패
    public static <T> ResponseEntity<CommonResponse<T>> fail(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(CommonResponse.<T>builder()
                        .status(errorCode.getStatusCode())
                        .message(errorCode.getMessage())
                        .data(null)
                        .build()
                );

    }
    // 실패
    public static <T> ResponseEntity<CommonResponse<T>> fail(String message, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(
                CommonResponse.<T>builder()
                        .status(httpStatus.value())
                        .data(null)
                        .message(message)
                        .build()
        );
    }
}
