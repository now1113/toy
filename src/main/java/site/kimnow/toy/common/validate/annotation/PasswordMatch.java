package site.kimnow.toy.common.validate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import site.kimnow.toy.common.validate.validator.PasswordMatchValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {

    String message() default "비밀번호와 비밀번호 확인이 일치하지 않습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
