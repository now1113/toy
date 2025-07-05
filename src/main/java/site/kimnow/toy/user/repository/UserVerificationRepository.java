package site.kimnow.toy.user.repository;

import site.kimnow.toy.user.domain.UserVerification;

public interface UserVerificationRepository {

    void save(UserVerification userVerification);
    void delete(UserVerification userVerification);
    UserVerification findByToken(String token);
}
