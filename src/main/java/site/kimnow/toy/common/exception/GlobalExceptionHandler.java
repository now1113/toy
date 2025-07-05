package site.kimnow.toy.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.kimnow.toy.common.properties.AuthRedirectUrlProperties;
import site.kimnow.toy.common.response.CommonResponse;
import site.kimnow.toy.common.response.ResponseUtil;
import site.kimnow.toy.user.exception.UserVerificationExpiredException;

import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static site.kimnow.toy.common.exception.CommonErrorCode.INTERNAL_SERVER_ERROR;
import static site.kimnow.toy.common.exception.CommonErrorCode.INVALID_INPUT;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AuthRedirectUrlProperties authRedirectUrlProperties;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = getErrorMessage(ex.getBindingResult());
        log.warn("Validation failed: {}", errorMessage, ex);
        return ResponseUtil.fail(INVALID_INPUT.getMessage() + " : " + errorMessage,  INVALID_INPUT.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage(), ex);
        return ResponseUtil.fail(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserVerificationExpiredException.class)
    public ResponseEntity<Void> handleExpired(UserVerificationExpiredException ex) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(authRedirectUrlProperties.getFail()))
                .build();
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Void>> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("BusinessException occurred: {}", ex.getMessage());
        return ResponseUtil.fail(errorCode.getMessage(), errorCode.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception ex) {
        log.error("handleValidationExceptions", ex);
        return ResponseUtil.fail(INTERNAL_SERVER_ERROR);
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
