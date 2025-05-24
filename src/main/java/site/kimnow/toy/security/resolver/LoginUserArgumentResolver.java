package site.kimnow.toy.security.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import site.kimnow.toy.security.annotation.LoginUser;
import site.kimnow.toy.user.dto.request.AuthenticatedUser;
import site.kimnow.toy.user.exception.UnauthorizedException;

import static site.kimnow.toy.common.constant.Constants.LOGIN_USER;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class)
                && parameter.getParameterType().equals(AuthenticatedUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Object userSession = request.getAttribute(LOGIN_USER);

        if (userSession == null) {
            throw new UnauthorizedException();
        }
        return userSession;
    }
}
