package site.kimnow.toy.user.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String email;
    private String name;
    private LocalDateTime createTime;
}
