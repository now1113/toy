package site.kimnow.toy.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.kimnow.toy.common.response.CommonResponse;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static site.kimnow.toy.common.exception.CommonErrorCode.INTERNAL_SERVER_ERROR;
import static site.kimnow.toy.common.exception.CommonErrorCode.INVALID_INPUT;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = getErrorMessage(ex.getBindingResult());
        log.warn("Validation failed: {}", errorMessage, ex);
        return CommonResponse.fail(INVALID_INPUT.getMessage() + " : " + errorMessage, INVALID_INPUT.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage(), ex);
        return CommonResponse.fail(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Void>> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("BusinessException occurred: {}", ex.getMessage());
        return CommonResponse.fail(errorCode.getMessage(), errorCode.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception ex) {
        log.error("handleValidationExceptions", ex);
        return CommonResponse.fail(INTERNAL_SERVER_ERROR);
    }


    private String getErrorMessage(BindingResult bindingResult) {
        String fieldErrors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> String.format("[%s] %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        String objectErrors = bindingResult.getGlobalErrors().stream()
                .map(objectError -> String.format("[%s] %s", objectError.getObjectName(), objectError.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        return Stream.of(fieldErrors, objectErrors)
                .filter(msg -> !msg.isBlank())
                .collect(Collectors.joining(", "));
    }
}
