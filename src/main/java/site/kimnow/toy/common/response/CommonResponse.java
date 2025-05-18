package site.kimnow.toy.common.response;

import lombok.*;
import org.springframework.http.HttpStatus;
import site.kimnow.toy.common.exception.ErrorCode;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonResponse<T> {

    private final int status;
    private final T data;
    private final String message;

    // 성공: 기본 메시지
    public static <T> CommonResponse<T> success() {
        return success(null, "SUCCESS");
    }

    // 성공: 데이터만
    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .data(data)
                .message(null)
                .build();
    }

    // ✅ 성공: 데이터 + 메시지
    public static <T> CommonResponse<T> success(T data, String message) {
        return CommonResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .data(data)
                .message(message)
                .build();
    }

    // 실패: ErrorCode 기반
    public static <T> CommonResponse<T> fail(ErrorCode errorCode) {
        return CommonResponse.<T>builder()
                .status(errorCode.getStatusCode())
                .message(errorCode.getMessage())
                .data(null)
                .build();
    }

    // 실패: 메시지 + HttpStatus 수동 지정
    public static <T> CommonResponse<T> fail(String message, HttpStatus httpStatus) {
        return CommonResponse.<T>builder()
                .status(httpStatus.value())
                .message(message)
                .data(null)
                .build();
    }
}
