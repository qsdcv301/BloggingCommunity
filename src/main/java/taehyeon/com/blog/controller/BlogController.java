package taehyeon.com.blog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import taehyeon.com.blog.entity.Blog;
import taehyeon.com.blog.entity.User;
import taehyeon.com.blog.service.BlogService;
import taehyeon.com.blog.service.UserService;

@RequiredArgsConstructor
@RequestMapping("/blog")
@Controller
public class BlogController {

    private final BlogService blogService;

    private final UserService userService;

    @PostMapping("/findBlog")
    public String findBlog(@RequestParam(name = "searchBlog") String email, Model model) {
        return "redirect:/blog/"+email;
    }

    @GetMapping("/{email}")
    public String userBlog(@PathVariable String email, Model model){
//        Blog blog = blogService.findById(userService.findByEmail(email).getId());
//        model.addAttribute("blog", blog);
        model.addAttribute("email", email);
        return "blog";
    }

    @GetMapping("/{email}/write")
    public String userBlogWrite(@PathVariable String email, Model model){
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        return "write";
    }

    @PostMapping("/{email}/write")
    public String userBlogWrite(@PathVariable String email, @ModelAttribute Blog blog, Model model){
        return "redirect:/blog/"+email;
    }

    @PostMapping("/{email}/delete")
    public String userBlogDelete(@PathVariable String email, Long id, Model model){
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        return "redirect:/blog/"+email;
    }

    @GetMapping("/{email}/update")
    public String userBlogUpdate(@PathVariable String email, Model model){
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        return "update";
    }

    @PostMapping("/{email}/update")
    public String userBlogUpdate(@PathVariable String email, @ModelAttribute Blog blog, Model model){
        return "redirect:/blog/"+email;
    }

}
