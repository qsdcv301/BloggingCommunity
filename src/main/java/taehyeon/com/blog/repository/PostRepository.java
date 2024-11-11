package taehyeon.com.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taehyeon.com.blog.entity.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByBlogId(Long blogId);
    Page<Post> findAllByBlogId(Long blogId, Pageable pageable);
    Page<Post> findAllByBlogIdAndCategoryId(Long blogId, Long categoryId, Pageable pageable);
}
