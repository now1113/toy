package site.kimnow.toy.security.vo;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.kimnow.toy.user.domain.User;

import java.util.Collection;
import java.util.List;

@Data
public class UserPrincipal implements UserDetails {

    private final User user;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 계정이 가지고 있는 권한목록을 리턴
        return List.of(new SimpleGrantedAuthority(user.getAuthority()));
    }

    public String getAuthority() {
        return this.user.getAuthority();
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
