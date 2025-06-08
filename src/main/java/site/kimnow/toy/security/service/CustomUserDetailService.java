package site.kimnow.toy.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.kimnow.toy.security.vo.UserPrincipal;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.repository.UserRepositoryAdapter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepositoryAdapter userRepositoryAdapter;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepositoryAdapter.findByEmail(username);
        return new UserPrincipal(user);
    }
}
