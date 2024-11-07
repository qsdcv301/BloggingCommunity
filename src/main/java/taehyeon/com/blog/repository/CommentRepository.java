package taehyeon.com.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taehyeon.com.blog.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
