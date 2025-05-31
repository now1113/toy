package site.kimnow.toy.user.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    private String email;
    private String name;
    private LocalDateTime createTime;

}
