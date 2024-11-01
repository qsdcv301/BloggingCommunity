package taehyeon.com.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taehyeon.com.blog.entity.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
}
