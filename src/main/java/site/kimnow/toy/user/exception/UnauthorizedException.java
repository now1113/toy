package site.kimnow.toy.user.exception;

import site.kimnow.toy.common.exception.BusinessException;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException() {
        super(UserErrorCode.UNAUTHORIZED);
    }
}
