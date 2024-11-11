package taehyeon.com.blog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
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

    public Object findMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getPrincipal();
    }

    @ModelAttribute
    public void findMypage(@PathVariable(required = false) String email, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            if (email == null) {
                email = principal instanceof User user ? user.getEmail() :
                        principal instanceof CustomOAuth2User customOauth2User ? customOauth2User.getEmail() : null;
            }
            Blog blog = null;
            try {
                blog = blogService.findByUserId(userService.findByEmail(email).getId());
                if (blog == null) {
                    model.addAttribute("error", "찾을 수 없는 페이지 입니다.");
                    return;
                }
            } catch (Exception e) {
                model.addAttribute("error", "찾을 수 없는 페이지 입니다.");
                return;
            }
            // User 객체인지 CustomOAuth2User 객체인지 확인
            if (principal instanceof User userData) {
                model.addAttribute("myHome", userData.getEmail());
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
                model.addAttribute("myHome", customOAuth2User.getEmail());
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
    }

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
                    neighborService.deleteById(neighborId);
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
                    neighborService.deleteById(neighborId);
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
    public String findBlog(@ModelAttribute("error") String error, @RequestParam(name = "searchBlog") String email,
                           Model model) {
        if (error != null && !error.isEmpty()) {
            // 예외가 발생한 경우 리다이렉트 처리
            return "redirect:/error/error";  // 예외가 발생했을 경우 error 페이지로 리다이렉트
        }
        try {
            return "redirect:/blog/" + email;
        } catch (Exception e) {
            model.addAttribute("error", "찾을 수 없는 페이지 입니다.");
            return "redirect:error/error";
        }
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
        model.addAttribute("email", email);
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

    @PostMapping("/{email}/setting/categoryDelete")
    public ResponseEntity<Map<String, Boolean>> categoryDelete(@PathVariable String email,
                                                               @ModelAttribute Category category) {
        boolean success;
        try {
            categoryService.deleteById(category.getId());
            success = true;
        } catch (Exception e) {
            success = false;
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{email}/setting/postDelete")
    public ResponseEntity<Map<String, Boolean>> postDelete(@PathVariable String email,
                                                           @RequestParam(name = "view", required = false) boolean view,
                                                           @ModelAttribute Post post) {
        boolean success;
        try {
            postService.deleteById(post.getId());
            success = true;
        } catch (Exception e) {
            success = false;
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        response.put("view", view);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{email}/setting/commentDelete")
    public ResponseEntity<Map<String, Boolean>> commentDelete(@PathVariable String email,
                                                              @ModelAttribute Comment comment) {
        boolean success;
        try {
            commentService.deleteById(comment.getId());
            success = true;
        } catch (Exception e) {
            success = false;
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{email}/setting/neighborDelete")
    public ResponseEntity<Map<String, Boolean>> neighborDelete(@PathVariable String email,
                                                               @ModelAttribute Neighbor neighbor) {
        boolean success;
        try {
            neighborService.deleteById(neighbor.getId());
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
                                @ModelAttribute Category category,
                                Model model) {
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
    public String userBlog(@PathVariable String email, @RequestParam(defaultValue = "0", required = false) int page,
                           @RequestParam(defaultValue = "5" , required = false) int size,
                           @RequestParam(name = "category", required = false) Long categoryBtn,
                           Model model) {
        try {
            Blog blog = blogService.findById(userService.findByEmail(email).getId());
            List<Neighbor> neighborList = neighborService.findAllByBlogId(blog.getId());
            List<Category> categoryList = categoryService.findAllByBlogId(blog.getId());
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts = null;
            System.out.println(categoryBtn);
            if (categoryBtn == null) {
                posts = postService.findAllByBlogId(blog.getId(), pageable);
            } else {
                posts = postService.findAllByBlogIdAndCategoryId(blog.getId(), categoryBtn, pageable);
            model.addAttribute("categoryBtn", categoryBtn);
            }
            List<Post> postList = posts.getContent();
//            int totalPages = posts.getTotalPages();
//            long totalItems = posts.getTotalElements();
            User user = userService.findByEmail(email);
            model.addAttribute("user", user);
            model.addAttribute("blog", blog);
            model.addAttribute("page", posts);
            model.addAttribute("postList", postList);
            model.addAttribute("categoryList", categoryList);
            model.addAttribute("neighborList", neighborList);
        } catch (Exception e) {
            model.addAttribute("error", "찾을 수 없는 페이지 입니다.");
            return "error/error";
        }
        return "blog";
    }

    @GetMapping("/{email}/view")
    public String userBlogView(@PathVariable String email, @RequestParam(name = "postId") Long postId, Model model) {
        try {
            Object userData = findMe();
            if (userData instanceof User) {
                User user = (User) userData;
                model.addAttribute("user", user);
            } else if (userData instanceof CustomOAuth2User) {
                CustomOAuth2User customOAuth2User = (CustomOAuth2User) userData;
                model.addAttribute("user", customOAuth2User);
            }
        } catch (Exception e) {
            model.addAttribute("error", "게정 정보가 없습니다.");
            return "error/error";
        }
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        Post post = postService.findById(postId);
        List<Comment> commentList = commentService.findAllByPostId(postId);
        model.addAttribute("commentList", commentList);
        model.addAttribute("blog", blog);
        model.addAttribute("post", post);
        return "/blog/view";
    }

    @GetMapping("/{email}/write")
    public String userBlogWrite(@PathVariable String email, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        List<Category> categoryList = categoryService.findAllByBlogId(blog.getId());

        model.addAttribute("email", email);
        model.addAttribute("categoryList", categoryList);
        return "/blog/write";
    }

    @PostMapping("/{email}/write")
    public String userBlogWrite(@PathVariable String email, @ModelAttribute Post post,
                                @RequestParam("categoryId") Long categoryId, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        Category category = categoryService.findById(categoryId);
        Post newPost = Post.builder()
                .blog(blog)
                .title(post.getTitle())
                .category(category)
                .summary(post.getSummary())
                .content(post.getContent())
                .build();
        postService.create(newPost);
        return "redirect:/blog/" + email;
    }

    @GetMapping("/{email}/update")
    public String userBlogUpdate(@PathVariable String email, @RequestParam("postId") Long id, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        List<Category> categoryList = categoryService.findAllByBlogId(blog.getId());
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("email", email);
        return "/blog/update";
    }

    @PostMapping("/{email}/update")
    public String userBlogUpdate(@PathVariable String email, @ModelAttribute Post post,
                                 @RequestParam("category.id") Long categoryId, Model model) {
        Blog blog = blogService.findById(userService.findByEmail(email).getId());
        Category category = categoryService.findById(categoryId);
        Post newPost = Post.builder()
                .id(post.getId())
                .blog(blog)
                .title(post.getTitle())
                .category(category)
                .summary(post.getSummary())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
        postService.update(post.getId(), newPost);
        return "redirect:/blog/" + email;
    }

    @PostMapping("/{email}/delete")
    public String userBlogDelete(@PathVariable String email, @RequestParam("postId") Long id, Model model) {
        postService.deleteById(id);
        return "redirect:/blog/" + email;
    }

    @PostMapping("/{email}/comment/write")
    public ResponseEntity<Map<String, Boolean>> postWrite(@PathVariable String email, @ModelAttribute Comment comment
            , @RequestParam("post_id") Long id, Model model) {
        boolean success;
        try {
            Object userData = findMe();
            String userEmail = null;
            if (userData instanceof User) {
                User user = (User) userData;
                userEmail = user.getEmail();
            } else if (userData instanceof CustomOAuth2User) {
                CustomOAuth2User customOAuth2User = (CustomOAuth2User) userData;
                userEmail = customOAuth2User.getEmail();
            }
            Comment newComment = Comment.builder()
                    .post(postService.findById(id))
                    .content(comment.getContent())
                    .blog(blogService.findByUserId(userService.findByEmail(userEmail).getId()))
                    .build();
            commentService.create(newComment);
            success = true;
        } catch (Exception e) {
            success = false;
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{email}/comment/update")
    public ResponseEntity<Map<String, Boolean>> postUpdate(@PathVariable String email,
                                                           @ModelAttribute Comment comment, Model model) {
        boolean success;
        try {
            Object userData = findMe();
            String userEmail = null;
            if (userData instanceof User) {
                User user = (User) userData;
                userEmail = user.getEmail();
            } else if (userData instanceof CustomOAuth2User) {
                CustomOAuth2User customOAuth2User = (CustomOAuth2User) userData;
                userEmail = customOAuth2User.getEmail();
            }
            Comment oldComment = commentService.findById(comment.getId());
            Comment newComment = Comment.builder()
                    .id(oldComment.getId())
                    .post(oldComment.getPost())
                    .content(comment.getContent())
                    .createdAt(oldComment.getCreatedAt())
                    .blog(oldComment.getBlog())
                    .build();
            commentService.update(oldComment.getId(), newComment);
            success = true;
        } catch (Exception e) {
            success = false;
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{email}/comment/delete")
    public ResponseEntity<Map<String, Boolean>> postWrite(@PathVariable String email, @RequestParam("id") Long id,
                                                          Model model) {
        boolean success;
        try {
            commentService.deleteById(id);
            success = true;
        } catch (Exception e) {
            success = false;
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

}
