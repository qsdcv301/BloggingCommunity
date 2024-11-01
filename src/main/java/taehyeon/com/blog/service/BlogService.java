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

    public Blog findById(Integer id) {
        return blogRepository.findById(id).orElse(null);
    }

    public List<Blog> findAll() {
        return blogRepository.findAll();
    }

    public Blog save(Blog blog) {
        return blogRepository.save(blog);
    }

    public Blog update(Integer id, Blog blog) {
        Optional<Blog> blogData = blogRepository.findById(id);
        if (blogData.isPresent()) {
            blogData.get().update(blog.getTitle(), blog.getDescription());
            return blogRepository.save(blogData.get());
        } else {
            System.out.println("블로그 정보를 수정하는 과정에서 오류가 발생했습니다.");
            return null;
        }
    }

    public void delete(Integer id) {
        blogRepository.deleteById(id);
    }
}
