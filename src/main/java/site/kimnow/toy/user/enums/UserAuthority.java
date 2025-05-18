package site.kimnow.toy.user.enums;

import lombok.Getter;

@Getter
public enum UserAuthority {

    ROLE_USER("USER", "사용자"),
    ROLE_ADMIN("ADMIN", "관리자")
    ;
    private String info;
    private String description;
    UserAuthority(String info, String description) {
        this.info = info;
        this.description = description;
    }
}
