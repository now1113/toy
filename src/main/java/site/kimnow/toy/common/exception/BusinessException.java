package site.kimnow.toy.common.exception;

public class BusinessException extends RuntimeException{
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
