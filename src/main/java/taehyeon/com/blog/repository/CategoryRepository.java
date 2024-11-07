package taehyeon.com.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taehyeon.com.blog.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Integer countByBlogId(Long blogId);
}
