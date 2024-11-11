package taehyeon.com.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import taehyeon.com.blog.entity.Post;
import taehyeon.com.blog.repository.PostRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post findById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post create(Post post) {
        return postRepository.save(post);
    }

    public Post update(Long id, Post post) {
        Post newPost = Post.builder()
                .id(id)
                .blog(post.getBlog())
                .title(post.getTitle())
                .summary(post.getSummary())
                .content(post.getContent())
                .category(post.getCategory())
                .build();
        return postRepository.save(newPost);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    public List<Post> findAllByBlogId(Long blogId) {
        return postRepository.findAllByBlogId(blogId);
    }

    public Page<Post> findAllByBlogId(Long blogId, Pageable pageable) {
        return postRepository.findAllByBlogId(blogId, pageable);
    }

    public Page<Post> findAllByBlogIdAndCategoryId(Long blogId, Long categoryId, Pageable pageable) {
        return postRepository.findAllByBlogIdAndCategoryId(blogId, categoryId, pageable);
    }

}
