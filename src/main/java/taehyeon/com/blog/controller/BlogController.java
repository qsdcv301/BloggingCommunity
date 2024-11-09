package taehyeon.com.blog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import taehyeon.com.blog.entity.*;
import taehyeon.com.blog.service.*;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@RequestMapping("/blog")
@Controller
public class BlogController {

    private final BlogService blogService;

    private final NeighborService neighborService;

    private final CategoryService categoryService;

    private final PostService postService;

    private final UserService userService;

    @GetMapping("/random")
    public String blogRandom(Model model) {
        List<Blog> blogList = blogService.findAll();
        Random random = new Random();
        int randomIndex = random.nextInt(blogList.size());
        Blog randomBlog = blogList.get(randomIndex);
        User user = userService.findById(randomBlog.getUserId());
        String email = user.getEmail();
        return "redirect:/blog/" + email;
    }

    @PostMapping("/findBlog")
    public String findBlog(@RequestParam(name = "searchBlog") String email, Model model) {
        return "redirect:/blog/" + email;
    }

    @GetMapping("/{email}/setting")
    public String blogSetting(@PathVariable String email, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        List<Neighbor> neighborList = neighborService.findAllByBlogId(blog.getId());
        List<Category> categoryList = categoryService.findAllByBlogId(blog.getId());
        List<Post> postList = postService.findAllByBlogId(blog.getId());
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("blog", blog);
        model.addAttribute("postList", postList);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("neighborList", neighborList);
        return "/blog/setting";
    }

    @GetMapping("/blogFirstSetting/{email}")
    public String blogFirstSetting(@PathVariable String email, Model model) {
        model.addAttribute("email", email);
        return "blogFirstSetting";
    }

    @PostMapping("/blogSetting/{email}")
    public String blogSettingOk(@PathVariable String email, @ModelAttribute Blog blog,
                                @ModelAttribute Category category, Model model) {
        User user = userService.findByEmail(email);
        Blog newBlog = Blog.builder()
                .userId(user.getId())
                .title(blog.getTitle())
                .description(blog.getDescription())
                .build();
        Blog createBlog = blogService.create(newBlog);
        Category newCategory = Category.builder()
                .blogId(createBlog.getId())
                .name(category.getName())
                .build();
        categoryService.create(newCategory);
        return "redirect:/blog/" + email;
    }

    @GetMapping("/{email}")
    public String userBlog(@PathVariable String email, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            // User 객체인지 CustomOAuth2User 객체인지 확인
            if (principal instanceof User userData) {
                if (userData.getEmail().equals(email)) {
                    model.addAttribute("myPage", true);
                } else {
                    model.addAttribute("myPage", false);
                }
            } else if (principal instanceof CustomOAuth2User customOAuth2User) {
                if (customOAuth2User.getEmail().equals(email)) {
                    model.addAttribute("myPage", true);
                } else {
                    model.addAttribute("myPage", false);
                }
            }
        }
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        List<Neighbor> neighborList = neighborService.findAllByBlogId(blog.getId());
        List<Category> categoryList = categoryService.findAllByBlogId(blog.getId());
        List<Post> postList = postService.findAllByBlogId(blog.getId());
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("blog", blog);
        model.addAttribute("postList", postList);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("neighborList", neighborList);
        return "blog";
    }

    @GetMapping("/{email}/view")
    public String userBlogView(@PathVariable String email, @RequestParam(name = "postId") Long postId, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        Post post = postService.findById(postId);
        model.addAttribute("blog", blog);
        model.addAttribute("post", post);
        return "/blog/view";
    }

    @GetMapping("/{email}/write")
    public String userBlogWrite(@PathVariable String email, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        return "/blog/write";
    }

    @PostMapping("/{email}/write")
    public String userBlogWrite(@PathVariable String email, @ModelAttribute Blog blog, Model model) {
        return "redirect:/blog/" + email;
    }

    @GetMapping("/{email}/update")
    public String userBlogUpdate(@PathVariable String email, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        return "/blog/update";
    }

    @PostMapping("/{email}/update")
    public String userBlogUpdate(@PathVariable String email, @ModelAttribute Blog blog, Model model) {
        return "redirect:/blog/" + email;
    }

    @PostMapping("/{email}/delete")
    public String userBlogDelete(@PathVariable String email, Long id, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        return "redirect:/blog/" + email;
    }

}
