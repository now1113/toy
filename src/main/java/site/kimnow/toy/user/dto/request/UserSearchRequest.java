package site.kimnow.toy.user.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.kimnow.toy.user.query.UserSearchQuery;

@Getter
@NoArgsConstructor
public class UserSearchRequest {

    @Min(1)
    private int page;
    @Min(10)
    private int size;
    private boolean emailVerified;

    public UserSearchQuery toQuery() {
        return UserSearchQuery.of(this.page - 1, size, this.emailVerified);
    }
}
