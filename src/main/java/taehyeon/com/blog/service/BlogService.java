package taehyeon.com.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import taehyeon.com.blog.entity.Blog;
import taehyeon.com.blog.repository.BlogRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    public Blog findById(Long id) {
        return blogRepository.findById(id).orElse(null);
    }

    public List<Blog> findAll() {
        return blogRepository.findAll();
    }

    public Blog create(Blog blog) {
        return blogRepository.save(blog);
    }

    public Blog update(Long id, Blog blog) {
        Blog newBlog = findById(id);
        newBlog.Builder(newBlog.getId(), blog.getUserId(), blog.getTitle(), blog.getDescription());
        return blogRepository.save(newBlog);
    }

    public void delete(Long id) {
        blogRepository.deleteById(id);
    }

}
