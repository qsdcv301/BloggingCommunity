package taehyeon.com.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import taehyeon.com.blog.entity.User;
import taehyeon.com.blog.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User create(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            // 비밀번호가 null일 경우 기본 비밀번호를 설정
            user.setPassword("소셜 로그인 유저");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User update(User user) {
        User newUser =User.builder()
                .id(user.getId())
                .provider(user.getProvider())
                .email(user.getEmail())
                .name(user.getName())
                .build();
        return userRepository.save(newUser);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findByEmailAndProvider(String email, String provider) {
        return userRepository.findByEmailAndProvider(email, provider).orElse(null);
    }

}
