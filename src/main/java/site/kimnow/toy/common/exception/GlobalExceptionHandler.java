package site.kimnow.toy.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.kimnow.toy.common.response.CommonResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("잘못된 요청: {}", e.getMessage());
        return CommonResponse.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception e) {
        log.error("서버 오류 발생", e);
        return CommonResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
