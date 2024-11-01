package taehyeon.com.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taehyeon.com.blog.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
}
