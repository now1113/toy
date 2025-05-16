package site.kimnow.toy.user.dto.response;

public record UserJoinResponse(String name, String message) {
    public static UserJoinResponse from(String name) {
        return new UserJoinResponse(name,name +  "님 회원가입을 환영합니다.");
    }
}
