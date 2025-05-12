package site.kimnow.toy.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.kimnow.toy.common.validate.annotation.Password;
import site.kimnow.toy.common.validate.annotation.PasswordMatch;

@Getter
@PasswordMatch
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserJoinRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    @Password
    private String password;

    @NotBlank
    private String confirmPassword;
}

