package taehyeon.com.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taehyeon.com.blog.entity.Neighbor;

import java.util.List;
import java.util.Optional;

@Repository
public interface NeighborRepository extends JpaRepository<Neighbor, Long> {
    List<Neighbor> findAllByBlogId(Long id);
    Optional<Neighbor> findByBlogIdAndNeighborBlogId(Long blogId, Long neighborId);
}
