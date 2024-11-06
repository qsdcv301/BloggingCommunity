package taehyeon.com.blog.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import taehyeon.com.blog.entity.User;
import taehyeon.com.blog.repository.UserRepository;

import java.sql.Timestamp;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(User user) {
        user.setEmail(user.getEmail());
        user.setProvider(user.getProvider());
        user.setProviderId(user.getProviderId());
        user.setName(user.getName());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user).getId();
    }
}
