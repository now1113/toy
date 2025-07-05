package site.kimnow.toy.user.enums;

import lombok.Getter;

@Getter
public enum UserStatus {

    PENDING_EMAIL_VERIFICATION("PENDING_EMAIL_VERIFICATION", "이메일 인증 대기"),
    EMAIL_VERIFIED("EMAIL_VERIFIED", "이메일 인증 완료"),
    ACTIVE("ACTIVE", "활성"),
    DORMANT("DORMANT", "휴면"),
    DELETED("DELETED", "탈퇴")
    ;

    private final String status;
    private final String description;

    UserStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }
}
