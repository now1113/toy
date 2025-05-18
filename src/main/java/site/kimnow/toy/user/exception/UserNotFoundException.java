package site.kimnow.toy.user.exception;

import site.kimnow.toy.common.exception.BusinessException;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
