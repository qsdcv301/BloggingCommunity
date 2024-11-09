package taehyeon.com.blog.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import taehyeon.com.blog.entity.Blog;
import taehyeon.com.blog.service.BlogService;
import taehyeon.com.blog.service.UserService;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final BlogService blogService;

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        Blog blog = blogService.findByUserId(userService.findByEmail(email).getId());
        if (blog != null) {
            response.sendRedirect("/" + email);
        } else {
            response.sendRedirect("/login");
        }
    }
}
