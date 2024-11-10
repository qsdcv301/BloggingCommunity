package taehyeon.com.blog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import taehyeon.com.blog.entity.*;
import taehyeon.com.blog.service.*;

import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/blog")
@Controller
public class BlogController {

    private final BlogService blogService;

    private final NeighborService neighborService;

    private final CategoryService categoryService;

    private final PostService postService;

    private final UserService userService;

    private final CommentService commentService;

    @PostMapping("/{email}/neighbor")
    public ResponseEntity<Map<String, Boolean>> neighbor(@PathVariable String email,
                                                         @RequestParam("myNeighbor") Boolean neighbor, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Boolean success = false;
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            Blog blog = blogService.findByUserId(userService.findByEmail(email).getId());
            // User 객체인지 CustomOAuth2User 객체인지 확인
            if (principal instanceof User userData) {
                if (neighbor) {
                    Long neighborId =
                            neighborService.findByBlogIdAndNeighborBlogId(blogService.findByUserId(userService.findByEmail(userData.getEmail()).getId()).getId(),
                                    blog.getId()).getId();
                    neighborService.delete(neighborId);
                    success = true;
                } else {
                    Neighbor newNeighbor = Neighbor.builder()
                            .neighborBlog(blog)
                            .blog(blogService.findByUserId(userService.findByEmail(userData.getEmail()).getId()))
                            .build();
                    neighborService.create(newNeighbor);
                    success = true;
                }
            } else if (principal instanceof CustomOAuth2User customOAuth2User) {
                if (neighbor) {
                    Long neighborId =
                            neighborService.findByBlogIdAndNeighborBlogId(blogService.findByUserId(userService.findByEmail(customOAuth2User.getEmail()).getId()).getId(),
                                    blog.getId()).getId();
                    neighborService.delete(neighborId);
                    success = true;
                } else {
                    Neighbor newNeighbor = Neighbor.builder()
                            .neighborBlog(blog)
                            .blog(blogService.findByUserId(userService.findByEmail(customOAuth2User.getEmail()).getId()))
                            .build();
                    neighborService.create(newNeighbor);
                    success = true;
                }
            }
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success); // 실제 중복 여부에 따라 설정
        return ResponseEntity.ok(response);
    }

    @GetMapping("/random")
    public String blogRandom(Model model) {
        List<Blog> blogList = blogService.findAll();
        Random random = new Random();
        int randomIndex = random.nextInt(blogList.size());
        Blog randomBlog = blogList.get(randomIndex);
        User user = userService.findById(randomBlog.getUser().getId());
        String email = user.getEmail();
        return "redirect:/blog/" + email;
    }

    @PostMapping("/findBlog")
    public String findBlog(@RequestParam(name = "searchBlog") String email, Model model) {
        return "redirect:/blog/" + email;
    }

    @GetMapping("/{email}/setting")
    public String blogSetting(@PathVariable String email, Model model) {
        Blog blog = blogService.findByUserId(userService.findByEmail(email).getId());
        List<Neighbor> neighborList = neighborService.findAllByBlogId(blog.getId());
        List<Category> categoryList = categoryService.findAllByBlogId(blog.getId());
        List<Post> postList = postService.findAllByBlogId(blog.getId());
        List<Comment> commentList = new ArrayList<>();
        for (Post post : postList) {
            commentList.addAll(commentService.findAllByPostId(post.getId()));
        }
        User user = userService.findByEmail(email);
        model.addAttribute("myPage", true);
        model.addAttribute("user", user);
        model.addAttribute("blog", blog);
        model.addAttribute("postList", postList);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("neighborList", neighborList);
        model.addAttribute("commentList", commentList);
        return "/blog/setting";
    }

    @PostMapping("/{email}/setting/blogUpdate")
    public ResponseEntity<Map<String, Boolean>> blogUpdate(@PathVariable String email, @ModelAttribute Blog blog) {
        boolean success;
        Blog oldBlog = blogService.findById(blog.getId());
        try {
            Blog newBlog = Blog.builder()
                    .id(oldBlog.getId())
                    .user(oldBlog.getUser())
                    .title(blog.getTitle())
                    .description(blog.getDescription())
                    .createdAt(oldBlog.getCreatedAt())
                    .build();
            blogService.update(newBlog.getId(), newBlog);
            success = true;
        } catch (Exception e) {
            success = false;
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{email}/setting/categoryUpdate")
    public ResponseEntity<Map<String, Boolean>> categoryUpdate(@PathVariable String email,
                                                               @ModelAttribute Category category) {
        boolean success;
        Category oldCategory = categoryService.findById(category.getId());
        try {
            Category newCategory = Category.builder()
                    .id(oldCategory.getId())
                    .name(category.getName())
                    .blogId(oldCategory.getBlogId())
                    .build();
            categoryService.update(newCategory.getId(), newCategory);
            success = true;
        } catch (Exception e) {
            success = false;
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{email}/setting/categoryCreate")
    public ResponseEntity<Map<String, Boolean>> categoryCreate(@PathVariable String email,
                                                               @ModelAttribute Category category) {
        boolean success;
        Blog blog = blogService.findByUserId(userService.findByEmail(email).getId());
        try {
            Category newCategory = Category.builder()
                    .name(category.getName())
                    .blogId(blog.getId())
                    .build();
            categoryService.create(newCategory);
            success = true;
        } catch (Exception e) {
            success = false;
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
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
                .user(user)
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
            Blog blog = blogService.findByUserId(userService.findByEmail(email).getId());
            // User 객체인지 CustomOAuth2User 객체인지 확인
            if (principal instanceof User userData) {
                if (userData.getEmail().equals(email)) {
                    model.addAttribute("myPage", true);
                } else {
                    model.addAttribute("myPage", false);
                    if (neighborService.findByBlogIdAndNeighborBlogId(userService.findByEmail(userData.getEmail()).getId(),
                            blog.getId()) != null) {
                        model.addAttribute("myNeighbor", true);
                    } else {
                        model.addAttribute("myNeighbor", false);
                    }
                }
            } else if (principal instanceof CustomOAuth2User customOAuth2User) {
                if (customOAuth2User.getEmail().equals(email)) {
                    model.addAttribute("myPage", true);
                } else {
                    model.addAttribute("myPage", false);
                    if (neighborService.findByBlogIdAndNeighborBlogId(userService.findByEmail(customOAuth2User.getEmail()).getId(),
                            blog.getId()) != null) {
                        model.addAttribute("myNeighbor", true);
                    } else {
                        model.addAttribute("myNeighbor", false);
                    }
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
