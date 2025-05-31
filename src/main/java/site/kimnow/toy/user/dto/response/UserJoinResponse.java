package site.kimnow.toy.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.kimnow.toy.user.domain.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinResponse {

    private String userId;
    private String name;
    private String email;

    public static UserJoinResponse from(User user) {
        return new UserJoinResponse(user.getUserId(), user.getName(), user.getEmail());
    }
}
