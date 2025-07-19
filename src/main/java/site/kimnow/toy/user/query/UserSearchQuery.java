package site.kimnow.toy.user.query;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSearchQuery {
    private final int page;
    private final int size;
    private final boolean emailVerified;

    public static UserSearchQuery of(int page, int size, boolean emailVerified) {
        return UserSearchQuery.builder()
                .page(page)
                .size(size)
                .emailVerified(emailVerified)
                .build();
    }
}
