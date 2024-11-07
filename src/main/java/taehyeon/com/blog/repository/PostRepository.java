package taehyeon.com.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taehyeon.com.blog.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
