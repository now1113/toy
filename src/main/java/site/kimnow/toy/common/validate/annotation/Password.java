package site.kimnow.toy.common.validate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import site.kimnow.toy.common.validate.validator.PasswordValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    String message() default "비밀번호는 특수문자를 포함하고 15자 이하로 입력해야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
