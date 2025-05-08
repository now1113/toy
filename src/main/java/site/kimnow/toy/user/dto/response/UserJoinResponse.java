package site.kimnow.toy.user.dto.response;

public record UserJoinResponse(String name) {
    public static final UserJoinResponse from(String name) {
        return new UserJoinResponse(name);
    }
}
