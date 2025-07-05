package site.kimnow.toy.user.exception;

import site.kimnow.toy.common.exception.BusinessException;

public class UserVerificationExpiredException extends BusinessException {
    public UserVerificationExpiredException() {
        super(UserErrorCode.USER_VERIFICATION_NOT_FOUND);
    }
}
