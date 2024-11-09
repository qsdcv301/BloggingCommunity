package taehyeon.com.blog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import taehyeon.com.blog.entity.Blog;
import taehyeon.com.blog.entity.Category;
import taehyeon.com.blog.entity.Neighbor;
import taehyeon.com.blog.entity.User;
import taehyeon.com.blog.service.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/blog")
@Controller
public class BlogController {

    private final BlogService blogService;

    private final NeighborService neighborService;

    private final CategoryService categoryService;

    private final PostService postService;

    private final UserService userService;

    @PostMapping("/findBlog")
    public String findBlog(@RequestParam(name = "searchBlog") String email, Model model) {
        return "redirect:/blog/" + email;
    }

    @GetMapping("/blogFirstSetting/{email}")
    public String blogSetting(@PathVariable String email, Model model) {
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
        blogService.create(newBlog);
        return "redirect:/blog/" + email;
    }

    @GetMapping("/{email}")
    public String userBlog(@PathVariable String email, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        List<Neighbor> neighborList = neighborService.findAllByBlogId(blog.getId());
        List<Category> categoryList = categoryService.findAllByBlogId(blog.getId());
        User user = userService.findByEmail(email);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("user", user);
        model.addAttribute("blog", blog);
        model.addAttribute("neighborList", neighborList);
        return "blog";
    }

    @GetMapping("/{email}/write")
    public String userBlogWrite(@PathVariable String email, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        return "write";
    }

    @PostMapping("/{email}/write")
    public String userBlogWrite(@PathVariable String email, @ModelAttribute Blog blog, Model model) {
        return "redirect:/blog/" + email;
    }

    @PostMapping("/{email}/delete")
    public String userBlogDelete(@PathVariable String email, Long id, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        return "redirect:/blog/" + email;
    }

    @GetMapping("/{email}/update")
    public String userBlogUpdate(@PathVariable String email, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        return "update";
    }

    @PostMapping("/{email}/update")
    public String userBlogUpdate(@PathVariable String email, @ModelAttribute Blog blog, Model model) {
        return "redirect:/blog/" + email;
    }

}
