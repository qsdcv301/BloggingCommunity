package taehyeon.com.blog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import taehyeon.com.blog.entity.Blog;
import taehyeon.com.blog.entity.CustomOAuth2User;
import taehyeon.com.blog.entity.User;
import taehyeon.com.blog.service.BlogService;
import taehyeon.com.blog.service.UserService;

import java.util.Objects;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;
    private final BlogService blogService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, Model model) {
        if (userService.findByEmail(user.getEmail()) == null) {
            userService.create(user);
            return "redirect:/login";
        } else {
            model.addAttribute("error", "이메일이 중복되어 생성이 불가합니다.");
            return "error/error";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

    @GetMapping("/loginSuccess")
    public String getLoginInfo(@AuthenticationPrincipal CustomOAuth2User user, Model model) {
        String provider = user.getProvider();
        String email = user.getEmail();
        String name = user.getName();
        User findUser = userService.findByEmail(email);
        if (findUser.getEmail() == null) {
            // 새로운 User 객체 생성
            User newUser = User.builder()
                    .provider(provider)
                    .email(email)
                    .name(name)
                    .build();
            userService.create(newUser);
            findUser = newUser;
        }
        if (blogService.findByUserId(Objects.requireNonNull(findUser).getId()) == null) {
            return "redirect:/blog/blogFirstSetting/" + email;
        }
        return "redirect:/blog/" + email;
    }

}
